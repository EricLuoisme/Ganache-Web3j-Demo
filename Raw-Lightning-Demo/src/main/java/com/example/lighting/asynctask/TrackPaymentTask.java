package com.example.lighting.asynctask;

import com.wokoworks.framework.data.ReturnValue;
import com.wokoworks.lightning.code.LndErrorCodes;
import com.wokoworks.lightning.entities.dto.PaymentDto;
import com.wokoworks.lightning.entities.po.LndDebitOrder;
import com.wokoworks.lightning.handler.GrpcReqHandler;

/**
 * Minimum trackPayment task, let queue's occupation less
 *
 * @author Roylic
 * 2022/6/29
 */
@Getter
public class TrackPaymentTask extends AsyncTask {

    private ReturnValue<PaymentDto, LndErrorCodes> returnValue;

    @Builder
    public TrackPaymentTask(Integer preDebitId, Integer debitOrderId, String nodeIp,
                            ReturnValue<PaymentDto, LndErrorCodes> returnValue) {
        super(AsyncTaskEnum.TRACK_PAYMENT, preDebitId, debitOrderId, nodeIp);
        this.returnValue = returnValue;
    }

    /**
     * Build a send-payment task with calling
     */
    public static TrackPaymentTask buildTask(LndDebitOrder order, GrpcReqHandler reqHandler) {
        return TrackPaymentTask.builder()
                .preDebitId(order.getPreDebitId())
                .debitOrderId(order.getId())
                .nodeIp(order.getNodeIp())
                .returnValue(
                        reqHandler.callTrackPayment(order.getNodeIp(), order.getPaymentHash())
                )
                .build();
    }
}
