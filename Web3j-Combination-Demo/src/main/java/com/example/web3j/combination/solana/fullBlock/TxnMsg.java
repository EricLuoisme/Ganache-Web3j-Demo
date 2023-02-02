package com.example.web3j.combination.solana.fullBlock;

import com.alibaba.fastjson2.JSONObject;
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
public class TxnMsg {

    // list of base-58 encoded pub keys, first key must sign the transaction
    private List<String> accountKeys;

    // details the account types and signatures required by the transaction
    private JSONObject header;

    // list of program instructions that will be executed in sequence
    // and committed in one atomic transaction if all succeed
    private List<Instructions> instructions;

    private String recentBlockhash;
}
