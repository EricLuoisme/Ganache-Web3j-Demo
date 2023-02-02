package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.fullBlock.BlockResult;
import com.example.web3j.combination.solana.fullBlock.Txn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Solana related tests
 *
 * @author Roylic
 * 2022/10/10
 */
public class SolanaTest {


    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";


    // could not be used with HTTPS
    private static final OkHttpClient http2Client = new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.H2_PRIOR_KNOWLEDGE))
            .connectionPool(new ConnectionPool(10, 1000L, TimeUnit.MILLISECONDS))
            .build();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    @Test
    public void getBlockHeightTest() throws IOException {
//        String getBlockHeight = "{\"jsonrpc\":\"2.0\",\"id\":1, \"method\":\"getBlockHeight\"}";
        String getBlockProduction = "{\"jsonrpc\":\"2.0\",\"id\":1, \"method\":\"getBlockProduction\"}";
        callAndPrint(getBlockProduction);
    }

    @Test
    public void getBalance() throws IOException {
        String getBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getBalance\", \"params\":[\"" + ADDRESS + "\"]}";
        callAndPrint(getBalance);
    }

    @Test
    public void fullBlockDecoding() throws IOException {

        String pureTxnBlockHeight = "192792360";
        String tokenTxnBlockHeight = "192792378";

        String getBlock = "{\"jsonrpc\": \"2.0\",\"id\":1,\"method\":\"getBlock\",\"params\":[" + tokenTxnBlockHeight + ", {\"encoding\": \"json\",\"maxSupportedTransactionVersion\":0,\"transactionDetails\":\"full\",\"rewards\":false}]}";

        RequestBody body = RequestBody.create(getBlock, mediaType);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();

        JSONObject respObj = JSONObject.parseObject(response.body().string());

        // 1. parse & filter for only caring txns
        ObjectMapper om = new ObjectMapper();
        JSONObject blockResultJson = respObj.getJSONObject("result");
        BlockResult blockResult = BlockResult.builder()
                .blockHeight(blockResultJson.getInteger("blockHeight"))
                .blockTime(blockResultJson.getInteger("blockTime"))
                .blockhash(blockResultJson.getString("blockhash"))
                .parentSlot(blockResultJson.getInteger("parentSlot"))
                .previousBlockhash(blockResultJson.getString("previousBlockhash"))
                .build();

        // parsing
        List<Txn> txns = new LinkedList<>();
        JSONArray transactions = blockResultJson.getJSONArray("transactions");
        transactions.forEach(txn -> {
            try {
                Txn parsedTxn = om.readValue(((JSONObject) txn).toJSONString(), Txn.class);
                txns.add(parsedTxn);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        blockResult.setTransactions(txns);


        System.out.println("Block Height: " + blockResult.getBlockHeight());
        System.out.println("Block Time: " + blockResult.getBlockHeight());
        System.out.println("Block Hash: " + blockResult.getBlockHeight());


        List<Txn> caredTxn = blockResult.getTransactions().stream()
                .filter(txn -> txn.getTransaction().getMessage().getAccountKeys().contains(ADDRESS))
                .collect(Collectors.toList());

        System.out.println(caredTxn);
    }

    private static void callAndPrint(String jsonMsg) throws IOException {
        RequestBody body = RequestBody.create(jsonMsg, mediaType);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        ObjectMapper om = new ObjectMapper();
        String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(response.body().string()));
        System.out.println(str);
    }


//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class SolanaAccountKey {
//        private String pubkey;
//        private Boolean signer;
//        private String source;
//        private Boolean writable;
//    }

}
