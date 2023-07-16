package com.own.third.api.quicknode;

import lombok.Data;

import java.util.List;

@Data
public class TokenBalances {

    private List<SingleToken> result;
    private int totalItems;
    private int totalPages;
    private int pageNumber;

    @Data
    public static class SingleToken {
        private String quantityIn;
        private String quantityOut;
        private String name;
        private String symbol;
        private String decimals;
        private String address;
        private String totalBalance;
    }
}
