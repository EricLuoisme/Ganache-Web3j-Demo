package com.own.third.api.quicknode;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BalanceTest {


    private final String webChainUrl = "";

    private final MediaType mediaType = MediaType.parse("application/json");

    private final ObjectMapper om = new ObjectMapper();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void getBalanceByAddress() throws IOException {

        String wallet = "0xE16dF6503Acd3c79b6E032f62c61752bEC16eeF2";

        JSONObject jsonRpc = new JSONObject();
        JSONArray params = new JSONArray();
        JSONObject singleParam = new JSONObject();
        singleParam.put("wallet", wallet);
        params.add(singleParam);

        jsonRpc.put("id", 1);
        jsonRpc.put("jsonrpc", "2.0");
        jsonRpc.put("method", "qn_getWalletTokenBalance");
        jsonRpc.put("params", params);

        RequestBody body = RequestBody.create(jsonRpc.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(webChainUrl)
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        JSONObject respJson = JSON.parseObject(response.body().string());
        TokenBalances tokenBalances = JSON.parseObject(respJson.getJSONObject("result").toJSONString(), TokenBalances.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(tokenBalances));
    }
}
