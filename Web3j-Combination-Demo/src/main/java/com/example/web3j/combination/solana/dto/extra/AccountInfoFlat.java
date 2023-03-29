package com.example.web3j.combination.solana.dto.extra;

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
public class AccountInfoFlat {

    // this own address
    private String atAddress;
    private String mintAddress;
    private String ownerAddress;
    private int amount;

    private boolean executable;
    private Long lamports;
    private Long rentEpoch;

}
