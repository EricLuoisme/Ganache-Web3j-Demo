package com.example.lighting.asynctask;

import com.wokoworks.lightning.asynctask.*;
import com.wokoworks.lightning.config.AsyncProperties;
import com.wokoworks.lightning.entities.enums.DebitPaymentStatus;
import com.wokoworks.lightning.entities.po.LndDebitOrder;
import lombok.extern.slf4j.Slf4j;
import org.jctools.queues.MpscBlockingConsumerArrayQueue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Async, all updates with occ
 *
 * @author Roylic
 * 2022/6/28
 */
@Slf4j
@Component
public class AsyncHandler_Deprecated {

    private final ThreadPoolExecutor TASK_HANDLE_EXECUTOR;

    private final ThreadPoolExecutor REQ_HANDLE_EXECUTOR;

    private final ThreadPoolExecutor RESULT_HANDLE_EXECUTOR;

    private final Integer RESULT_CONCURRENT_CONSUME_NUM;

    private final MpscBlockingConsumerArrayQueue<AsyncTask> resultQueue;

    private final GrpcReqHandler reqHandler;

    private final GrpcStubHandler stubHandler;

    private final RepoHandler repoHandler;


    public AsyncHandler_Deprecated(AsyncProperties properties, GrpcReqHandler reqHandler,
                                   GrpcStubHandler stubHandler, RepoHandler repoHandler) {

        // for task handling (submit to REQ_HANDLE_EXECUTOR, help offer to Queue)
        AsyncProperties.ThreadPoolProperties taskPool = properties.getTaskThreadPool().getThreadPoolProperties();
        this.TASK_HANDLE_EXECUTOR =
                new ThreadPoolExecutor(
                        taskPool.getCorePoolSize(),
                        taskPool.getMaxPoolSize(),
                        taskPool.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(taskPool.getMaxPoolSize()));

        // for req handling (sending the request)
        this.REQ_HANDLE_EXECUTOR =
                new ThreadPoolExecutor(
                        taskPool.getCorePoolSize(),
                        taskPool.getMaxPoolSize(),
                        taskPool.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(taskPool.getTaskQueueMaxSize()));

        // for result handling (update db according to the request-response)
        this.RESULT_HANDLE_EXECUTOR = new ThreadPoolExecutor(
                taskPool.getCorePoolSize(),
                taskPool.getMaxPoolSize(),
                taskPool.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(taskPool.getTaskQueueMaxSize()));

        // for queue
        this.RESULT_CONCURRENT_CONSUME_NUM = 5;
        this.resultQueue = new MpscBlockingConsumerArrayQueue<>(100);
        this.reqHandler = reqHandler;
        this.stubHandler = stubHandler;
        this.repoHandler = repoHandler;
    }


    /**
     * Calling Send-Payment
     */
    public void sendPaymentTask(Integer debitOrderId) {
        log.info(">>>>>> [Task] calling Send-Payment Task for lnd_debit_order-ID:{}", debitOrderId);
        TASK_HANDLE_EXECUTOR.submit(() -> invokeTask(debitOrderId, AsyncTaskEnum.SEND_PAYMENT));
    }

    /**
     * Calling Track-Payment
     */
    public void tackPaymentTask(Integer debitOrderId) {
        log.info(">>>>>> [Task] calling Track-Payment Task for lnd_debit_order-ID:{}", debitOrderId);
        TASK_HANDLE_EXECUTOR.submit(() -> invokeTask(debitOrderId, AsyncTaskEnum.TRACK_PAYMENT));
    }


    /**
     * Async Task Main Processes, construct task by @param taskName
     */
    private void invokeTask(Integer debitOrderId, AsyncTaskEnum taskName) {

        // 1. find and check status, check sent or not
        LndDebitOrder order = repoHandler.findDebitOrderByIdOrPayReq(debitOrderId, null).get();
        if (DebitPaymentStatus.INIT.validConvert.contains(order.getPaymentStatus())) {
            log.info("<<<<<< [Producer] terminate task:{} request for lnd_debit_order-ID:{}, cur order state not init", taskName, debitOrderId);
            return;
        }
        if (AsyncTaskEnum.SEND_PAYMENT.equals(taskName) && order.getHadSent()) {
            log.info("<<<<<< [Producer] terminate send-payment request for lnd_debit_order-ID:{}, send-payment has already been called", debitOrderId);
            return;
        }
        if (AsyncTaskEnum.TRACK_PAYMENT.equals(taskName) && !order.getHadSent()) {
            log.info("<<<<<< [Producer] terminate track-payment request for lnd_debit_order-ID:{}, send-payment not yet calling", debitOrderId);
            return;
        }

        // 2. update the db into PENDING status, also fill the node stuff
        GrpcStubHandler.LndBlockStubHolder lndBlockStubHolder = stubHandler.getUsableBlockHolderByIp(order.getNodeIp()).get();
        boolean updateStatus = repoHandler.updateDebitOrderAndPreDebit_OCC(
                order.getId(), order.getPreDebitId(), lndBlockStubHolder.getIp(), DebitPaymentStatus.PENDING, null, null);
        if (!updateStatus) {
            // stop this task if occ update fail
            log.info("<<<<<< [Producer] terminate task:{} request for lnd_debit_order-ID:{}, update to pending status fail", taskName, debitOrderId);
            return;
        }

        // 3. double check
        Integer curPaymentStatus = repoHandler.findDebitOrderByIdOrPayReq(order.getId(), null).get().getPaymentStatus();
        if (DebitPaymentStatus.PENDING.dbCode - curPaymentStatus != 0) {
            // stop this task if occ update fail
            log.info("<<<<<< [Producer] terminate task:{} request for lnd_debit_order-ID:{}, double check on update to pending status fail", taskName, debitOrderId);
            return;
        }

        try {
            // 4.1 construct supplier, by task name
            AsyncTaskSupplier supplier = AsyncTaskSupplier.builder()
                    .taskName(taskName)
                    .reqHandler(reqHandler)
                    .order(order)
                    .build();

            // 4.2 supply the task async
            CompletableFuture<AsyncTask> reqFutureTask = CompletableFuture.supplyAsync(supplier, REQ_HANDLE_EXECUTOR);

            // 4.3 if async task finished, let TASK_HANDLE_EXECUTOR help to offer it into the queue
            reqFutureTask.whenCompleteAsync(
                    ((asyncTask, throwable) -> {
                        // if return false, let next task handle, do not change the status
                        resultQueue.offer(asyncTask);
                    }),
                    TASK_HANDLE_EXECUTOR);

            log.debug(">>>>>> [Producer] submitted task:{}, for lnd_debit_order-ID:{}", taskName, order.getId());

        } catch (RejectedExecutionException e) {

            e.printStackTrace();
            log.error("<<<<<< [Fatal] Error, Abort Policy Occur: {}, cur lnd_debit_order-ID:{}, for task:{}", e.getMessage(), order.getId(), taskName);
            // roll-back the db into INIT status, occ
            repoHandler.updateDebitOrderAndPreDebit_OCC(
                    order.getId(), order.getPreDebitId(), lndBlockStubHolder.getIp(), DebitPaymentStatus.INIT, null, e.getMessage());

        }
    }

    /**
     * Loop thread for retrieving task from queue and sending them into thread-pool-consumer
     */
    @PostConstruct
    public void consumePaymentResult() {
        new Thread(() -> {
            while (true) {
                resultQueue.drain(asyncTask -> {
                    // dispatch the task
//                    if (SendPaymentTask.TASK_NAME.equals(taskName)) {
//                        // a. send-payment
//                        SendPaymentTask singleTask = (SendPaymentTask) asyncTask;
//                        CompletableFuture.runAsync(
//                                () -> new SendPaymentTaskConsumerDeprecated(this, repoHandler).accept(singleTask),
//                                RESULT_HANDLE_EXECUTOR);
//
//                    } else if (TrackPaymentTask.TASK_NAME.equals(taskName)) {
//                        // b. track-payment
//                        TrackPaymentTask singleTask = (TrackPaymentTask) asyncTask;
//                        CompletableFuture.runAsync(
//                                () -> new TrackPaymentTaskConsumer(repoHandler, callbackHandler).accept(singleTask),
//                                RESULT_HANDLE_EXECUTOR);
//
//                    } else {
//                        // TODO ????????
//                        log.error("<><><><>>><><><><><><><><><><><><><><><><><><><><><><><><><><><<><><><<><><><><><><><");
//                    }

                }, RESULT_CONCURRENT_CONSUME_NUM);
            }
        }).start();
    }


}
