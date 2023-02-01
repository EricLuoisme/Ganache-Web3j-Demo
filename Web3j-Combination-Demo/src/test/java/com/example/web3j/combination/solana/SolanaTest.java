package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
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
    public void blockDecoding() throws IOException {

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
        SolanaBlockResult blockResult = new SolanaBlockResult.SolanaBlockResultBuilder()
                .blockHeight(blockResultJson.getInteger("blockHeight"))
                .blockTime(blockResultJson.getInteger("blockTime"))
                .blockhash(blockResultJson.getString("blockhash"))
                .parentSlot(blockResultJson.getInteger("parentSlot"))
                .previousBlockhash(blockResultJson.getString("previousBlockhash"))
                .transactions(
                        om.readValue(
                                blockResultJson.getJSONArray("transactions").toJSONString(),
                                new TypeReference<List<Txns>>() {
                                }))
                .build();

        System.out.println("Block Height: " + blockResult.getBlockHeight());
        System.out.println("Block Time: " + blockResult.getBlockHeight());
        System.out.println("Block Hash: " + blockResult.getBlockHeight());

//        List<SolanaAccTxns> ownUserTxn = blockResult.getTransactions().stream()
//                .filter(txn -> {
//                    Set<String> pubKeySet = txn.getTransaction()
//                            .getAccountKeys().stream()
//                            .map(SolanaAccountKey::getPubkey).collect(Collectors.toSet());
//                    return pubKeySet.contains(ADDRESS);
//                }).collect(Collectors.toList());

        List<Txns> caredFullTxn = blockResult.getTransactions().stream()
                .filter(txn -> txn.getTransaction().getMessage().getAccountKeys().contains(ADDRESS))
                .collect(Collectors.toList());
        System.out.println();
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SolanaBlockResult {
        private Integer blockHeight;
        private Integer blockTime;
        private String blockhash;
        private Integer parentSlot;
        private String previousBlockhash;
        private List<Txns> transactions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Txns {
        private Meta meta;
        private Txn transaction;
        private String version;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private Integer computeUnitsConsumed;
        private Integer fee;
        private JSONArray innerInstructions;
        private JSONArray loadedAddresses;
        private JSONArray logMessages;
        private List<Integer> postBalances;
        private JSONArray postTokenBalances;
        private List<Integer> preBalances;
        private JSONArray preTokenBalances;
        private JSONArray status;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Txn {
        private TxnMsg message;
        private List<String> signatures;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TxnMsg {
        private List<String> accountKeys;
        private JSONObject header;
        private JSONArray instructions;
        private String recentBlockhash;
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
