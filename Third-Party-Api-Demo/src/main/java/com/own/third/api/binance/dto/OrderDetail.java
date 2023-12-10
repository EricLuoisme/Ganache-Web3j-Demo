package com.own.third.api.binance.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetail {
    private String symbol;
    private long orderId;
    private int orderListId;
    private String clientOrderId;
    private long transactTime;
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private String status;
    private String timeInForce;
    private String type;
    private String side;
    private long workingTime;
    private List<Fill> fills;
    private String selfTradePreventionMode;

    @Data
    public static class Fill {
        private String price;
        private String qty;
        private String commission;
        private String commissionAsset;
        private int tradeId;
    }
}
