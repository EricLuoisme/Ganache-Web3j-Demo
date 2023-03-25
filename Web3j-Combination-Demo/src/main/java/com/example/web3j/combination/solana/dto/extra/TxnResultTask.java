package com.example.web3j.combination.solana.dto.extra;

import com.example.web3j.combination.solana.dto.TxnResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TxnResultTask {
    private String associatedTokenAccount;
    private String signature;
    private TxnResult txnResult;
}
