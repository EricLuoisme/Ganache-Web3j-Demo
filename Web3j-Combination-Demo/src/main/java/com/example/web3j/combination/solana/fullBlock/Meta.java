package com.example.web3j.combination.solana.fullBlock;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Roylic
 * 2023/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private Integer computeUnitsConsumed;
    private Object err;
    private Integer fee;
    private List<InnerInstructions> innerInstructions;
    private JSONObject loadedAddresses;
    private JSONArray logMessages;
    private JSONArray postBalances;
    private JSONArray postTokenBalances;
    private JSONArray preBalances;
    private JSONArray preTokenBalances;
    private Object rewards;
    private JSONObject status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerInstructions {
        private Integer index;
        private List<Instructions> instructions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instructions {
        private List<Integer> accounts;
        private String data;
        private Integer programIdIndex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenBalance {
        private Integer accountIndex;
        private String mint;
        private String owner;
        private String programId;
        private TokenAmt uiTokenAmount;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TokenAmt {
            private String amount;
            private Integer decimals;
            private Double uiAmount;
            private String uiTokenAmount;
        }
    }
}


