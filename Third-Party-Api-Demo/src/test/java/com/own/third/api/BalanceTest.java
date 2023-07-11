package com.own.third.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BalanceTest {

    private final String apiKey = "docs-demo";

    private final MediaType mediaType = MediaType.parse("application/json");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();

    @Test
    public void getTokenBalances() throws IOException {

        String owner = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String checkedTokens = "erc20";

        JSONObject jsonRpc = new JSONObject();
        JSONArray params = new JSONArray();
        params.add(owner);
        params.add(checkedTokens);
        jsonRpc.put("id", 1);
        jsonRpc.put("jsonrpc", "2.0");
        jsonRpc.put("method", "alchemy_getTokenBalances");
        jsonRpc.put("params", params);

        RequestBody body = RequestBody.create(jsonRpc.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url("https://eth-goerli.g.alchemy.com/v2/" + apiKey)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String respStr = response.body().string();
        System.out.println(respStr);
    }

}
