package com.example.web3j.combination.solana.fullBlock;

import com.alibaba.fastjson2.JSONArray;
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
    private List<String> accountKeys;
    private JSONObject header;
    private JSONArray instructions;
    private String recentBlockhash;
}
