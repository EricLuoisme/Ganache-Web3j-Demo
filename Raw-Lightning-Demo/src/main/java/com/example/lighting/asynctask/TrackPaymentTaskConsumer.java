package com.example.lighting.asynctask;

import com.wokoworks.framework.data.ReturnValue;
import com.wokoworks.lightning.code.LndErrorCodes;
import com.wokoworks.lightning.entities.dto.PaymentDto;
import com.wokoworks.lightning.entities.enums.DebitPaymentStatus;
import com.wokoworks.lightning.handler.BusCallbackHandler;
import com.wokoworks.lightning.handler.RepoHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * Consume result of track-payment
 *
 * @author Roylic
 * 2022/6/30
 */
@Slf4j
public class TrackPaymentTaskConsumer implements Consumer<TrackPaymentTask> {

    private final RepoHandler repoHandler;

    private final BusCallbackHandler busCallbackHandler;


    public TrackPaymentTaskConsumer(RepoHandler repoHandler, BusCallbackHandler busCallbackHandler) {
        this.repoHandler = repoHandler;
        this.busCallbackHandler = busCallbackHandler;
    }


    @Override
    public void accept(TrackPaymentTask task) {
        ReturnValue<PaymentDto, LndErrorCodes> returnValue = task.getReturnValue();

        // fail section start
        if (returnValue.hasError()) {
            LndErrorCodes error = returnValue.getError();
            if (error == LndErrorCodes.LND_PAYMENT_NOT_FOUND_INIT) {// a. Not Found, might be paid by other lnd
                log.info("<<<<<< [TrackPayment-Consumer] convert to FAIL for lnd_debit_order-ID:{}, which is already sent Send-Payment", task.getDebitOrderId());
                repoHandler.updateDebitOrderAndPreDebit_OCC(
                        task.getDebitOrderId(), task.getPreDebitId(), task.getNodeIp(), DebitPaymentStatus.FAILED, null, error.outerErrMsg);
                // async callback
                busCallbackHandler.debitOrderCallbackNotification(task.getDebitOrderId());
            } else {
                // b. can not judge, return to init
                log.info("<<<<<< [TrackPayment-Consumer] could not judge for lnd_debit_order-ID:{}, convert back to INIT", task.getDebitOrderId());
                repoHandler.updateDebitOrderAndPreDebit_OCC(
                        task.getDebitOrderId(), task.getPreDebitId(), task.getNodeIp(), DebitPaymentStatus.INIT, null, error.outerErrMsg);
            }
            // fail section end
            return;
        }

        // success section start, status according to the result, but skip the PENDING state
        DebitPaymentStatus status = returnValue.getData().getStatus();
        if (DebitPaymentStatus.PENDING.equals(status)) {
            log.info("<<<<<< [TrackPayment-Consumer] found payment result, debit_order-ID:{}, skip for state:{}", task.getDebitOrderId(), status);
        } else {
            log.info("<<<<<< [TrackPayment-Consumer] debit_order-ID:{}, found state:{}", task.getDebitOrderId(), status);
            repoHandler.updateDebitOrderAndPreDebit_OCC(
                    task.getDebitOrderId(), task.getPreDebitId(),
                    task.getNodeIp(), returnValue.getData().getStatus(), returnValue.getData(), null);
            // async callback
            busCallbackHandler.debitOrderCallbackNotification(task.getDebitOrderId());
        }
        // success section end
    }
}
