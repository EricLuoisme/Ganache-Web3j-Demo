package com.example.lighting.asynctask;

import com.wokoworks.lightning.asynctask.*;
import com.wokoworks.lightning.config.AsyncProperties;
import com.wokoworks.lightning.entities.enums.DebitPaymentStatus;
import com.wokoworks.lightning.entities.po.LndDebitOrder;
import com.wokoworks.lightning.schedule.DebitInitScanner;
import lombok.extern.slf4j.Slf4j;
import org.jctools.queues.MpscBlockingConsumerArrayQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Async, all updates with occ
 *
 * @author Roylic
 * 2022/6/28
 */
@Slf4j
@Component
public class AsyncHandler {

    private ThreadPoolExecutor TASK_HANDLE_EXECUTOR;

    private ThreadPoolExecutor REQUEST_HANDLE_EXECUTOR;

    private ThreadPoolExecutor RESULT_HANDLE_EXECUTOR;

    @Autowired
    private AsyncProperties asyncProperties;

    @Autowired
    private GrpcReqHandler grpcReqHandler;

    @Autowired
    private GrpcStubHandler grpcStubHandler;

    @Autowired
    private RepoHandler repoHandler;

    @Autowired
    private BusCallbackHandler busCallbackHandler;

    @Autowired
    private DebitInitScanner debitInitScanner;

    @PostConstruct
    public void post() {

        AsyncProperties.ThreadPoolProperties taskPoolProps = asyncProperties.getTaskThreadPool().getThreadPoolProperties();
        this.TASK_HANDLE_EXECUTOR =
                new ThreadPoolExecutor(
                        taskPoolProps.getCorePoolSize(),
                        taskPoolProps.getMaxPoolSize(),
                        taskPoolProps.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new MpscBlockingConsumerArrayQueue<>(taskPoolProps.getTaskQueueMaxSize()),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        AsyncProperties.ThreadPoolProperties reqPoolProps = asyncProperties.getRequestThreadPool().getThreadPoolProperties();
        this.REQUEST_HANDLE_EXECUTOR =
                new ThreadPoolExecutor(
                        reqPoolProps.getCorePoolSize(),
                        reqPoolProps.getMaxPoolSize(),
                        reqPoolProps.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new MpscBlockingConsumerArrayQueue<>(reqPoolProps.getTaskQueueMaxSize()),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        AsyncProperties.ThreadPoolProperties resultPoolProps = asyncProperties.getResultThreadPool().getThreadPoolProperties();
        this.RESULT_HANDLE_EXECUTOR =
                new ThreadPoolExecutor(
                        resultPoolProps.getCorePoolSize(),
                        resultPoolProps.getMaxPoolSize(),
                        resultPoolProps.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new MpscBlockingConsumerArrayQueue<>(resultPoolProps.getTaskQueueMaxSize()),
                        new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void submitIntiToPendingTask(List<LndDebitOrder> debitOrderList) {
        log.info(">>>>>> [AsyncHandler] Lightning, convert into pending status, with input:{}", debitOrderList);
        debitOrderList.forEach(debitOrder -> TASK_HANDLE_EXECUTOR.submit(() -> convertDebitOrderIntoPending(debitOrder)));
    }

    public void submitPendingToRequestTask(List<LndDebitOrder> debitOrderList) {
        log.info(">>>>>> [AsyncHandler] Lightning, construct debit order req, with input:{}", debitOrderList);
        debitOrderList.forEach(debitOrder -> TASK_HANDLE_EXECUTOR.submit(() -> constructDebitOrderReq(debitOrder)));
    }

    private void convertDebitOrderIntoPending(LndDebitOrder debitOrder) {
        if (repoHandler.updateDebitOrderAndPreDebit_OCC(
                debitOrder.getId(), debitOrder.getPreDebitId(),
                debitOrder.getNodeIp(), DebitPaymentStatus.PENDING, null, null)) {
            // submit to the thread pool as task
            submitPendingToRequestTask(Collections.singletonList(debitOrder));
        }
    }

    private void constructDebitOrderReq(LndDebitOrder debitOrder) {
        Optional<GrpcStubHandler.LndBlockStubHolder> blockHolderByIp
                = grpcStubHandler.getUsableBlockHolderByIp(debitOrder.getNodeIp());
        if (!blockHolderByIp.isPresent()) {
            log.error("<<<<<< [AsyncHandler] Lightning, do not find debit order, with input:{}", debitOrder);
            return;
        }
        // construct supplier
        AsyncTaskSupplier supplier = AsyncTaskSupplier.builder()
                .taskName(debitOrder.getHadSent() ? AsyncTaskEnum.TRACK_PAYMENT : AsyncTaskEnum.SEND_PAYMENT)
                .reqHandler(grpcReqHandler)
                .order(debitOrder)
                .build();
        // supply async-task
        CompletableFuture<AsyncTask> asyncFuture = CompletableFuture.supplyAsync(supplier, REQUEST_HANDLE_EXECUTOR);
        // dispatch async-task's result
        asyncFuture.whenCompleteAsync(this::dispatchAsyncTaskResult, TASK_HANDLE_EXECUTOR);
    }

    /**
     * dispatch & handling the async-task's result
     */
    private void dispatchAsyncTaskResult(AsyncTask asyncTask, Throwable throwable) {

        log.info("<<<<<< [AsyncHandler] Lightning, dispatch async-task results, with input:{}, error:{}", asyncTask, throwable);

        // a. contains error
        if (null != throwable) {
            // occ lnd_debit_order into INIT states
            log.error("<<<<<< [AsyncHandler] Lightning, convert debit order into INIT, with input:{}, error:{}", asyncTask, throwable);
            repoHandler.updateDebitOrderAndPreDebit_OCC(
                    asyncTask.getDebitOrderId(), asyncTask.getPreDebitId(), asyncTask.getNodeIp(),
                    DebitPaymentStatus.INIT, null, throwable.getMessage());
            return;
        }
        switch (asyncTask.getTaskName()) {
            // b. send-payment task
            case SEND_PAYMENT: {
                log.info("<<<<<< [AsyncHandler] Lightning, send-payment task, with input:{}", asyncTask);
                SendPaymentTask singleTask = (SendPaymentTask) asyncTask;
                CompletableFuture.runAsync(
                        () -> new SendPaymentTaskConsumer(repoHandler, busCallbackHandler, debitInitScanner).accept(singleTask),
                        RESULT_HANDLE_EXECUTOR);
                return;
            }
            // c. track-payment task
            case TRACK_PAYMENT: {
                log.info("<<<<<< [AsyncHandler] Lightning, track-payment task, with input:{}", asyncTask);
                TrackPaymentTask singleTask = (TrackPaymentTask) asyncTask;
                CompletableFuture.runAsync(
                        () -> new TrackPaymentTaskConsumer(repoHandler, busCallbackHandler).accept(singleTask),
                        RESULT_HANDLE_EXECUTOR);
                return;
            }
            // d. error (should not into here)
            default: {
                log.error("<<<<<< [AsyncHandler] Lightning, task unknown, with input:{}", asyncTask);
            }
        }
    }

}
