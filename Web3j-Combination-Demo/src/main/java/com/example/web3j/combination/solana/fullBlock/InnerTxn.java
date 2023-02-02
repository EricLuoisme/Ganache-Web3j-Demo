package com.example.web3j.combination.solana.fullBlock;

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
public class InnerTxn {

    private TxnMsg message;

    // a list of base-58 encoded signatures applied to the transaction, the first one is used as transaction id
    private List<String> signatures;

    // only not null when calling getBlock with -> ACCOUNTS option
    private List<AccountBlockAccKeys> accountKeys;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountBlockAccKeys {
        private String pubkey;
        private Boolean signer;
        private String source;
        private Boolean writable;
    }
}
