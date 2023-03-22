package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.dto.AssetChanging;
import com.example.web3j.combination.solana.fullBlock.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Roylic
 * 2023/2/2
 */
public class SolanaBlockDecodingTest {


    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();

    private static final MediaType mediaType = MediaType.parse("application/json");

    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    @Test
    @Benchmark
    public void blockDecoding_support_full_and_account() throws IOException {

        StopWatch stopWatch = new StopWatch("Full Block Decoding");
        stopWatch.start("JsonRpc Request");

        String pureTxnBlockHeight = "192792360";
//        String tokenTxnBlockHeight = "192792378";
        String tokenTxnBlockHeight = "203493212";
        String ops = "full";
//        String ops = "accounts";

        String getBlock = "{\"jsonrpc\": \"2.0\",\"id\":1,\"method\":\"getBlock\",\"params\":["
                + tokenTxnBlockHeight + ", {\"encoding\": \"json\",\"maxSupportedTransactionVersion\":0,\"transactionDetails\":\""
                + ops + "\",\"rewards\":false}]}";

        RequestBody body = RequestBody.create(getBlock, mediaType);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        stopWatch.stop();


        stopWatch.start("Parsing Obj");
        JSONObject respObj = JSONObject.parseObject(response.body().string());

        // parse & filter for only caring txns
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
        stopWatch.stop();


        System.out.println("Block Height: " + blockResult.getBlockHeight());
        System.out.println("Block Time: " + blockResult.getBlockHeight());
        System.out.println("Block Hash: " + blockResult.getBlockHeight());

        stopWatch.start("Filter + Decoding");

        List<Txn> caredTxn = null;
        if (ops.equals("full")) {
            caredTxn = blockResult.getTransactions().stream()
                    .filter(txn -> txn.getTransaction().getMessage().getAccountKeys().contains(ADDRESS) || txn.getTransaction().getMessage().getAccountKeys().contains("GCTFHbBcLs3vsNkCKLz2uCq5boZhMHbuawuFuhbYttc8"))
                    .collect(Collectors.toList());
        } else if (ops.equals("accounts")) {
            caredTxn = blockResult.getTransactions().stream()
                    .filter(txn -> txn.getTransaction().getAccountKeys().stream().anyMatch(key -> key.getPubkey().equalsIgnoreCase(ADDRESS) || key.getPubkey().equalsIgnoreCase("GCTFHbBcLs3vsNkCKLz2uCq5boZhMHbuawuFuhbYttc8")))
                    .collect(Collectors.toList());
        }

        // decoding handler
        Map<String, AssetChanging> assetDifInTxn = FullBlockDecHandler.getAssetDifInTxn(caredTxn.get(0));

        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(assetDifInTxn.get(ADDRESS)));
        stopWatch.stop();
        System.out.println(stopWatch);
    }


    @Test
    public void specificBlockTxnDecoding() throws IOException {

        String nftCreationBlockHeight = "202616989";
        String ops = "full";
//        String ops = "accounts";

        String getBlock = "{\"jsonrpc\": \"2.0\",\"id\":1,\"method\":\"getBlock\",\"params\":["
                + nftCreationBlockHeight + ", {\"encoding\": \"json\",\"maxSupportedTransactionVersion\":0,\"transactionDetails\":\""
                + ops + "\",\"rewards\":false}]}";

        RequestBody body = RequestBody.create(getBlock, mediaType);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        JSONObject respObj = JSONObject.parseObject(response.body().string());

        // parse & filter for only caring txns
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

        List<Txn> caredTxn = null;
        if (ops.equals("full")) {
            caredTxn = blockResult.getTransactions().stream()
                    .filter(txn -> txn.getTransaction().getMessage().getAccountKeys().contains(ADDRESS))
                    .collect(Collectors.toList());
        } else if (ops.equals("accounts")) {
            caredTxn = blockResult.getTransactions().stream()
                    .filter(txn -> txn.getTransaction().getAccountKeys().stream().anyMatch(key -> key.getPubkey().equalsIgnoreCase(ADDRESS)))
                    .collect(Collectors.toList());
        }

        // 1. find user address's index
        Txn realCareTxn = caredTxn.get(0);
        TxnMsg message = realCareTxn.getTransaction().getMessage();
        int idx = 0;
        Iterator<String> iterator = message.getAccountKeys().iterator();
        while (iterator.hasNext()) {
            if (ADDRESS.equalsIgnoreCase(iterator.next())) {
                break;
            } else {
                idx++;
            }
        }

        // 2. find instructions that related to this index
        int finalIdx = idx;
        List<Instructions> collect = message.getInstructions().stream()
                .filter(instructions -> instructions.getAccounts().contains(finalIdx))
                .collect(Collectors.toList());


    }


    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

}
