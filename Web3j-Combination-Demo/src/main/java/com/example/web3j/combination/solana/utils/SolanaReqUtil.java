package com.example.web3j.combination.solana.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.dto.*;
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
    //    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://api.devnet.solana.com").newBuilder().build().toString();
    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();

    /**
     * Get LatestBlock
     */
    public static LatestBlock rpcLatestBlock(OkHttpClient okHttpClient) {
        String getLatestBlock = "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"getLatestBlockhash\",\"params\":[{\"commitment\":\"confirmed\"}]}";
        String resp = jsonRpcReq(okHttpClient, getLatestBlock);
        if (!StringUtils.hasLength(resp)) {
            return new LatestBlock();
        }
        JSONObject respObject = JSONObject.parseObject(resp);
        return JSONObject.parseObject(respObject.getJSONObject("result").getJSONObject("value").toJSONString(), LatestBlock.class);
    }

    /**
     * Get Block Full Detail
     */
    public static BlockResult rpcFullBlockOnAccountInfo(OkHttpClient okHttpClient, Long blockHeight) {
        String getBlock = "{\"jsonrpc\": \"2.0\",\"id\":1,\"method\":\"getBlock\",\"params\":[%d, {\"encoding\": \"json\",\"maxSupportedTransactionVersion\":0,\"transactionDetails\":\"accounts\",\"rewards\":false}]}";
        String resp = jsonRpcReq(okHttpClient, String.format(getBlock, blockHeight));
        if (!StringUtils.hasLength(resp)) {
            return new BlockResult();
        }
        JSONObject respObject = JSONObject.parseObject(resp);
        return JSONObject.parseObject(respObject.getJSONObject("result").toJSONString(), BlockResult.class);
    }

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
    public static List<SigResult> rpcAccountSignaturesWithLimit(OkHttpClient okHttpClient, String account, int limit) {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":%d}]}";
        String resp = jsonRpcReq(okHttpClient, String.format(req, account, limit));
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


//    public static void main(String[] args) throws JsonProcessingException {
//        OkHttpClient client = new OkHttpClient.Builder().build();
//        BlockResult blockResult = rpcFullBlockOnAccountInfo(client, 203493212L);
//        ObjectMapper om = new ObjectMapper();
//        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(blockResult));
//    }

}
