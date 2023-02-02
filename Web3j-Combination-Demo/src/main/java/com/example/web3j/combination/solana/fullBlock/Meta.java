package com.example.web3j.combination.solana.fullBlock;

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

    // number of compute units consumed by the transaction
    private Integer computeUnitsConsumed;

    // Error if transaction failed, null if transaction succeeded
    private Object err;

    // fee this transaction was charged, as u64 integer
    private Integer fee;

    private List<InnerInstructions> innerInstructions;

    private JSONObject loadedAddresses;

    private List<String> logMessages;

    private List<Long> postBalances;

    private List<TokenBalance> postTokenBalances;

    private List<Long> preBalances;

    private List<TokenBalance> preTokenBalances;

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
    public static class TokenBalance {
        // index of account in which the token balance is provided for
        private Integer accountIndex;

        // pub key of token's mint
        private String mint;

        // pub key of token balance's owner
        private String owner;

        // pub key of token program
        private String programId;

        private TokenAmt uiTokenAmount;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TokenAmt {
            // raw amount
            private String amount;

            // token decimals
            private Integer decimals;

            // deprecated
            private Double uiAmount;

            // token amount (accounting for decimals already)
            private String uiAmountString;
        }
    }
}


