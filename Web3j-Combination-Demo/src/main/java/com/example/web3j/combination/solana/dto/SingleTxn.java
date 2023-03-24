package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/3/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleTxn {
    private Long blockTime;
    private Meta meta;
    private Long slot;
    private Transaction transaction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        private TxnMsg message;
    }
}
