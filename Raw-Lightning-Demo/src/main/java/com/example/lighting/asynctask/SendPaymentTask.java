package com.example.lighting.asynctask;

import com.wokoworks.framework.data.ReturnValue;
import com.wokoworks.lightning.code.LndErrorCodes;
import com.wokoworks.lightning.entities.dto.PaymentDto;
import com.wokoworks.lightning.entities.po.LndDebitOrder;
import com.wokoworks.lightning.handler.GrpcReqHandler;
import lombok.Builder;
import lombok.Getter;

/**
 * Minimum SendPayment task, let queue's occupation less
 *
 * @author Roylic
 * 2022/6/29
 */
@Getter
public class SendPaymentTask extends AsyncTask {

    private ReturnValue<PaymentDto, LndErrorCodes> returnValue;

    @Builder
    public SendPaymentTask(Integer preDebitId, Integer debitOrderId, String nodeIp,
                           ReturnValue<PaymentDto, LndErrorCodes> returnValue) {
        super(AsyncTaskEnum.SEND_PAYMENT, preDebitId, debitOrderId, nodeIp);
        this.returnValue = returnValue;
    }

    /**
     * Build a send-payment task with calling
     */
    public static SendPaymentTask buildTask(LndDebitOrder order, GrpcReqHandler reqHandler) {
        return SendPaymentTask.builder()
                .preDebitId(order.getPreDebitId())
                .debitOrderId(order.getId())
                .nodeIp(order.getNodeIp())
                .returnValue(
                        reqHandler.callSendPayment(order.getNodeIp(), order.getPayReq(),
                                order.getRealInvoiceAmt(), order.getMaxRouteFee()))
                .build();
    }
}
