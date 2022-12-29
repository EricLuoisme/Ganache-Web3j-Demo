package com.example.web3j.combination.opensea;


import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenseaApiCallingTest {

    private static final String OPENSEA_BASE_URL = "https://api.opensea.io/api/v1/";
    private static final String OPENSEA_TESTNET_BASE_URL = "https://testnets-api.opensea.io/api/v1/";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    // Step 1: for crawling all the images
    @Test
    public void retrieveAssets() throws IOException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(OPENSEA_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("assets");
        urlBuilder.addQueryParameter("order_direction", "desc");
        urlBuilder.addQueryParameter("limit", "10");
        urlBuilder.addQueryParameter("include_orders", "true");
        String url = urlBuilder.build().toString();
        System.out.println(url);

        Request requestGet = new Request.Builder()
                .header("accept", "application/json")
                .header("X-API-KEY", " ")
                .url(url)
                .build();

        Call call = okHttpClient.newCall(requestGet);
        Response execute = call.execute();

        JSONObject jsonObject = JSONObject.parseObject(execute.body().string());
        System.out.println();

        /**
         * token_id ->
         *
         * asset_contract -> address (contract address)
         *                -> schema_name (ERC-721/ERC-1155)
         *
         * permalink -> e.g https://opensea.io/assets/(chain-name)/(contract-address)/(token-id)
         */
    }

    @Test
    public void retrieveAssets_Single() throws IOException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(OPENSEA_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("asset");
        urlBuilder.addPathSegment("0x4c85a8d714f14b6c0bca75d5ef97a4f9f55d70ba");
        urlBuilder.addPathSegment("3425");
        urlBuilder.addQueryParameter("include_orders", "true");
        String url = urlBuilder.build().toString();
        System.out.println(url);

        Request requestGet = new Request.Builder()
                .header("accept", "application/json")
                .header("X-API-KEY", " ")
                .url(url)
                .build();

        Call call = okHttpClient.newCall(requestGet);
        Response execute = call.execute();

        JSONObject jsonObject = JSONObject.parseObject(execute.body().string());
        System.out.println();


        /**
         * token_id -> token's id
         *
         * token_metadata -> metadata url
         *
         * asset_contract ->
         *      address -> contract address
         *      schema_name -> ERC-721 / ERC-1155
         *
         * image_original_url -> url from the token
         *
         */
    }

}
