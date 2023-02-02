package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * For storing address related balance & token changing in one txn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetChanging {
    private String address;
    private Long preBalance;
    private Long postBalance;
    private TokenBalanceDif tokenBalanceDif;
    private String txHash;
}
