package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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


//    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://api.devnet.solana.com").newBuilder().build().toString();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final String ACCOUNT = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
    private static final String TOKEN_ACCOUNT = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";
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

    // means to check the Address's token account remaining token amount
    @Test
    public void getTokenAccountBalance() throws IOException {
        String getTokenAccountBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getTokenAccountBalance\"," +
                " \"params\":[\"" + TOKEN_ACCOUNT + "\"]}";
        callAndPrint(getTokenAccountBalance);
    }

    // mean to check account-address's remaining tokens in that specific program with program id
    @Test
    public void getTokenAccountsByOwner() throws IOException {
        String getAddressRemainingToken = "{\"jsonrpc\":\"2.0\", \"id\":1, " +
                "\"method\":\"getTokenAccountsByOwner\", \"params\":[\"" + TOKEN_ACCOUNT + "\", " +
                "{\"programId\":\"" + PROGRAM_ID + "\"}]}";
        callAndPrint(getAddressRemainingToken);
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

}
