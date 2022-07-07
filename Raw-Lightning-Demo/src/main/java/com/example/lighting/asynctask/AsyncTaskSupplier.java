package com.example.lighting.asynctask;

import com.wokoworks.lightning.entities.po.LndDebitOrder;
import com.wokoworks.lightning.handler.GrpcReqHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Supplier for SendPayment-Task
 *
 * @author Roylic
 * 2022/6/29
 */
@Data
@Builder
@Slf4j
public class AsyncTaskSupplier implements Supplier<AsyncTask> {

    private final AsyncTaskEnum taskName;

    private final GrpcReqHandler reqHandler;

    private final LndDebitOrder order;


    public AsyncTaskSupplier(AsyncTaskEnum taskName, GrpcReqHandler reqHandler, LndDebitOrder order) {
        this.taskName = taskName;
        this.reqHandler = reqHandler;
        this.order = order;
    }


    @Override
    public AsyncTask get() {
        switch (taskName) {
            case SEND_PAYMENT:
                return SendPaymentTask.buildTask(order, reqHandler);
            case TRACK_PAYMENT:
                return TrackPaymentTask.buildTask(order, reqHandler);
            default:
                log.error("><><><><><>");
                return null;
        }
    }
}
