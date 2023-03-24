package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solana account info
 *
 * @author Roylic
 * 2023/3/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    private Account account;
    private String pubkey;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account {
        private String[] data;
        private boolean executable;
        private Long lamports;
        private String owner;
        private Long rentEpoch;
    }

}
