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
 *
 * @author Roylic
 * 2022/10/10
 */
public class SolanaProgramRelated {


    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
    private static final String TOKEN_ADDRESS = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";


    // could not be used with HTTPS
    private static final OkHttpClient http2Client = new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.H2_PRIOR_KNOWLEDGE))
            .connectionPool(new ConnectionPool(10, 1000L, TimeUnit.MILLISECONDS))
            .build();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    @Test
    public void getBalance() throws IOException {
        String getBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getBalance\", \"params\":[\"" + ADDRESS + "\"]}";
        callAndPrint(getBalance);
    }

    // means to check the token's program remaining token amount
    @Test
    public void getTokenAccountBalance() throws IOException {
        String getTokenAccountBalance = "{\"jsonrpc\":\"2.0\", \"id\":1, \"method\":\"getTokenAccountBalance\", \"params\":[\"" + TOKEN_ADDRESS + "\"]}";
        callAndPrint(getTokenAccountBalance);
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
