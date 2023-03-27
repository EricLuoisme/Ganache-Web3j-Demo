package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/3/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestBlock {
    private String blockhash;
    private Long lastValidBlockHeight;
}
