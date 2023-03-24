package com.example.web3j.combination.solana.dto.extra;

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

    // basic
    private String address;
    private Long preSolBalance;
    private Long postSolBalance;
    private String signature;
    private boolean splTransfer;
    private ChangeEnum assetChangeEnum;

    // spl related
    private String owner;
    private String programId;
    private String preRawTokenAmt;
    private String postRawTokenAmt;
    private String preTokenAmt;
    private String postTokenAmt;
    private Integer tokenDecimal;


    public enum ChangeEnum {
        STEADY, SOL_CREDIT, SOL_DEBIT, SPL_CREDIT, SPL_DEBIT
    }
}
