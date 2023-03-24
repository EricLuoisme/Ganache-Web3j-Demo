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
public class SigResult {
    private String confirmationStatus;
    private String signature;
    private Long slot;
    private Long blockTime;
    private Object err;
    private String memo;
}
