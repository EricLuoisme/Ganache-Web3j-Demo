package com.example.web3j.combination.solana.dto.extra;

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
public class SigPair {
    private String signature;
    private Long blockDt;
    private String account;
}
