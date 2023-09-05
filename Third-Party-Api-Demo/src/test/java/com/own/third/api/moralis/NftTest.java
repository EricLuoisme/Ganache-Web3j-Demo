package com.own.third.api.moralis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Roylic
 * 2023/9/5
 */
public class NftTest {

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
    public void getNFTsByAddress() {

        String wallet = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String chain = "bsc testnet";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(MORALIS_URL + wallet).newBuilder();
        urlBuilder.addPathSegment("nft")
                .addQueryParameter("chain", chain)
                .addQueryParameter("media_items", "true")
                .addQueryParameter("format", "decimal");

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("content-type", "application/json")
                .addHeader("X-API-Key", API_KEY)
                .get()
                .build();


    }


}
