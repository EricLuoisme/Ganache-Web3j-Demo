package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.utils.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Solana related tests
 * <p>
 * - programs do not contain any data, but instead reference accounts
 * where data can be stored. This enables programs to run concurrently,
 * as long as they are not accessing the same account data
 * <p>
 * - No need to deploy a new program for each new token -> a standard Token Program
 * already deployed and ready for anyone to use for creating, minting, trading, burning
 *
 * @author Roylic
 * 2022/10/10
 */
public class SolanaProgramRelated {


    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    //    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://api.devnet.solana.com").newBuilder().build().toString();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final String ACCOUNT = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
    private static final String ASSOCIATED_TOKEN_ACCOUNT = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";
    private static final String TOKEN_MINT_ACCOUNT = "Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr";
    private static final String NFT_MINT_ACCOUNT = "EZqtsCxYpYtNaX1Pd2ep3ZUVxS6qHLVQriugvbKGEahk";
    private static final String NFT_COLLECTION_MINT_ACCOUNT = "ARHE7qXefr79DqyApiEkZ2QwnyzfAnUew4jRXfkMBVT2";


    private static final String PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA";


    // could not be used with HTTPS
    private static final OkHttpClient http2Client = new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.H2_PRIOR_KNOWLEDGE))
            .connectionPool(new ConnectionPool(10, 1000L, TimeUnit.MILLISECONDS))
            .build();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    @Test
    public void getBalance() throws IOException {
        String getBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getBalance\", \"params\":[\"" + ACCOUNT + "\"]}";
        callAndPrint(getBalance);
    }

    @Test
    public void getAccountInfo() throws IOException {
        String getAccountInfo = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getAccountInfo\",\"params\":[\"" + ASSOCIATED_TOKEN_ACCOUNT + "\",{\"encoding\":\"base64\"}]}";
        callAndPrint(getAccountInfo);
    }


    // means to check the Address's token account remaining token amount
    @Test
    public void getTokenAccountBalance() throws IOException {
        String getTokenAccountBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getTokenAccountBalance\"," +
                " \"params\":[\"" + ASSOCIATED_TOKEN_ACCOUNT + "\"]}";
        callAndPrint(getTokenAccountBalance);
    }

    // mean to check account-address's remaining tokens in that specific program with program id
    @Test
    public void getTokenAccountsByOwner() throws IOException {
        String getAddressRemainingToken = "{\"jsonrpc\":\"2.0\", \"id\":1, " +
                "\"method\":\"getTokenAccountsByOwner\", \"params\":[\"" + ACCOUNT + "\", " +
                "{\"programId\":\"" + PROGRAM_ID + "\"}, {\"encoding\":\"base64\"}]}";
        String respString = callAndPrint(getAddressRemainingToken);
        JSONObject jsonRpcRespJson = JSONObject.parseObject(respString);
        JSONObject result = jsonRpcRespJson.getJSONObject("result");
        JSONArray value = result.getJSONArray("value");
        Object val_0 = value.get(0);
        String data_0_base64 = (String) ((JSONObject) val_0).getJSONObject("account").getJSONArray("data").get(0);
        byte[] decode = Base64.decode(data_0_base64);
        byte[] mint = new byte[32];
        byte[] owner = new byte[32];
        byte[] amount = new byte[8];

        System.arraycopy(decode, 0, mint, 0, 32);
        System.arraycopy(decode, 32, owner, 0, 32);
        System.arraycopy(decode, 64, amount, 0, 8);
        System.out.println("0: mint address " + Base58.encode(mint));
        System.out.println("0: owner address " + Base58.encode(owner));
        int unsignedInt = 0;
        for (int i = 0; i < amount.length; i++) {
            unsignedInt |= (amount[i] & 0xFF) << (8 * i);
        }
        System.out.println("0: amount " + unsignedInt);
        System.out.println();
    }

    @Test
    public void getAssociatedTokenAddress() throws Exception {
        PublicKey.ProgramDerivedAddress derivedAddress = PublicKey.findProgramAddress(
                Arrays.asList(
                        new PublicKey(ACCOUNT).toByteArray(),
                        new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA").toByteArray(),
                        new PublicKey("Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr").toByteArray()),
                new PublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL"));
        System.out.println(derivedAddress.getAddress());
        String getTokenAccountBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getTokenAccountBalance\"," +
                " \"params\":[\"" + derivedAddress.getAddress() + "\"]}";
        callAndPrint(getTokenAccountBalance);
    }


    private static String callAndPrint(String jsonMsg) throws IOException {
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
        return str;
    }

}
