package com.example.lighting.asynctask;

import com.example.lighting.asynctask.AsyncHandler_Deprecated;
import com.wokoworks.framework.data.ReturnValue;
import com.wokoworks.lightning.code.LndErrorCodes;
import com.wokoworks.lightning.entities.dto.PaymentDto;
import com.wokoworks.lightning.entities.enums.DebitPaymentStatus;
import com.wokoworks.lightning.handler.RepoHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * Consume result of send-payment
 *
 * @author Roylic
 * 2022/6/29
 */
@Slf4j
public class SendPaymentTaskConsumer_Deprecated implements Consumer<SendPaymentTask> {

    private final AsyncHandler_Deprecated asyncHandler;

    private final RepoHandler repoHandler;


    public SendPaymentTaskConsumer_Deprecated(AsyncHandler_Deprecated asyncHandler, RepoHandler repoHandler) {
        this.asyncHandler = asyncHandler;
        this.repoHandler = repoHandler;
    }


    @Override
    public void accept(SendPaymentTask task) {
        ReturnValue<PaymentDto, LndErrorCodes> returnValue = task.getReturnValue();
        // hadSent update
        repoHandler.updateDebitOrderHadSent(task.getDebitOrderId());
        // fail section start
        if (returnValue.hasError()) {
            LndErrorCodes error = returnValue.getError();
            log.info("<<<<<< [SendPayment-Consumer] found payment NOT-SUCCESS result with:{}", error);
            switch (error) {
                case LND_PAYMENT_INVOICE_EXPIRED:
                case LND_INSUFFICIENT_LOCAL_BALANCE:
                case LND_PAYMENT_INSUFFICIENT_BALANCE: {
                    // a. expired -> FAIL
                    // b. not enough balance -> FAIL
                    log.info("<<<<<< [SendPayment-Consumer] convert lnd_debit_order-ID:{}, to status: {}",
                            task.getDebitOrderId(), DebitPaymentStatus.FAILED.name());
                    repoHandler.updateDebitOrderAndPreDebit_OCC(
                            task.getDebitOrderId(), task.getPreDebitId(), task.getNodeIp(), DebitPaymentStatus.FAILED, null, error.outerErrMsg);
                    // TODO async resp
                    break;
                }
                case LND_PAYMENT_INVOICE_NO_ROUTE:
                case LND_PAYMENT_INVOICE_ALREADY_PAID: {
                    // c. no_route -> revert to INIT, then -> TrackPaymentTask
                    // d. already paid -> revert to INIT, then -> TrackPaymentTask
                    log.info("<<<<<< [SendPayment-Consumer] need tack-payment for lnd_debit_order-ID:{}", task.getDebitOrderId());
                    repoHandler.updateDebitOrderAndPreDebit_OCC(
                            task.getDebitOrderId(), task.getPreDebitId(), task.getNodeIp(), DebitPaymentStatus.INIT, null, error.outerErrMsg);
                    asyncHandler.tackPaymentTask(task.getDebitOrderId());
                    break;
                }
                default: {
                    // d. update lnd_debit_order & lnd_pre_debit into INIT status
                    log.error("<<<<<< [SendPayment-Consumer] Cannot judge error for lnd_debit_order-ID:{}, with error:{}",
                            task.getDebitOrderId(), error);
                    repoHandler.updateDebitOrderAndPreDebit_OCC(
                            task.getDebitOrderId(), task.getPreDebitId(), task.getNodeIp(), DebitPaymentStatus.INIT, null, error.outerErrMsg);
                    break;
                }
            }
            // fail section end
            return;
        }
        // success section start, need to set succeeded manually
        log.info("<<<<<< [SendPayment-Consumer] found payment SUCCESS result, debit_order-ID:{}", task.getDebitOrderId());
        repoHandler.updateDebitOrderAndPreDebit_OCC(
                task.getDebitOrderId(), task.getPreDebitId(),
                task.getNodeIp(), DebitPaymentStatus.SUCCEEDED, returnValue.getData(), null);
        // TODO async resp
        // success section end
    }
}
