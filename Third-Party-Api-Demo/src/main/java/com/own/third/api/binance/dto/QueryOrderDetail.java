package com.own.third.api.binance.dto;

import lombok.Data;

@Data
public class QueryOrderDetail extends OrderDetail {
    private String stopPrice;
    private String icebergQty;
    private long time;
    private boolean isWorking;
    private String origQuoteOrderQty;
}
