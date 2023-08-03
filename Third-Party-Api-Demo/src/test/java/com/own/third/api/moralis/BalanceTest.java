package com.own.third.api.moralis;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BalanceTest {


    private final String MORALIS_URL = "https://deep-index.moralis.io/api/v2/";

    private final String API_KEY = "";

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
    public void getTokenBalanceByAddress() throws IOException {

        String wallet = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String chain = "goerli";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(MORALIS_URL + wallet).newBuilder();
        urlBuilder.addPathSegment("erc20")
                .addQueryParameter("chain", chain);

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("content-type", "application/json")
                .addHeader("X-API-Key", API_KEY)
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        List<Token> tokenList = JSON.parseArray(response.body().string(), Token.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(tokenList));
    }

}
