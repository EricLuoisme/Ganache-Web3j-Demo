package com.example.web3j.combination.solana.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.dto.AccountInfo;
import com.example.web3j.combination.solana.dto.SigResult;
import com.example.web3j.combination.solana.dto.TxnResult;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/24
 */
public class SolanaReqUtil {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://api.devnet.solana.com").newBuilder().build().toString();

    /**
     * Get associated token accounts
     */
    public static List<AccountInfo> rpcAssociatedTokenAccountByOwner(OkHttpClient okHttpClient, String address) {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTokenAccountsByOwner\",\"params\":[\"%s\",{\"programId\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\"},{\"encoding\":\"base64\"}]}";
        String resp = jsonRpcReq(okHttpClient, String.format(req, address));
        if (!StringUtils.hasLength(resp)) {
            return new ArrayList<>();
        }
        JSONObject respObject = JSONObject.parseObject(resp);
        return JSON.parseArray(respObject.getJSONObject("result").getJSONArray("value").toJSONString(), AccountInfo.class);
    }

    /**
     * Get account the latest signatures with limits
     */
    public static List<SigResult> rpcAccountSignaturesWithLimit(OkHttpClient okHttpClient, String address, int limit) {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":%d}]}";
        String resp = jsonRpcReq(okHttpClient, String.format(req, address, limit));
        if (!StringUtils.hasLength(resp)) {
            return new ArrayList<>();
        }
        JSONObject respObject = JSONObject.parseObject(resp);
        return JSON.parseArray(respObject.getJSONArray("result").toJSONString(), SigResult.class);
    }

    /**
     * Get transaction full detail by signature
     */
    public static TxnResult rpcTransactionBySignature(OkHttpClient okHttpClient, String signature) {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTransaction\",\"params\":[\"%s\",{\"encoding\":\"json\"}]}";
        String resp = jsonRpcReq(okHttpClient, String.format(req, signature));
        if (!StringUtils.hasLength(resp)) {
            return new TxnResult();
        }
        JSONObject respObject = JSONObject.parseObject(resp);
        return JSON.parseObject(respObject.getJSONObject("result").toJSONString(), TxnResult.class);
    }


    private static String jsonRpcReq(OkHttpClient okHttpClient, String jsonMsg) {
        RequestBody body = RequestBody.create(jsonMsg, MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
