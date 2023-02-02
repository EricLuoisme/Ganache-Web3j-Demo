package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBalanceDif {
    private String owner;
    private String programId;
    private String preRawTokenAmt;
    private String postRawTokenAmt;
    private String preTokenAmt;
    private String postTokenAmt;
    private Integer tokenDecimal;
    private String txHash;
}
