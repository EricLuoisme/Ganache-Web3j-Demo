package com.own.third.api.binance.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarketDepth {

    private long lastUpdateId;
    private List<Order> bids;
    private List<Order> asks;

    @Data
    public static class Order {
        private String price;
        private String quantity;

        public Order(List<String> order) {
            if (order != null && order.size() >= 2) {
                this.price = order.get(0);
                this.quantity = order.get(1);
            }
        }
    }
}
