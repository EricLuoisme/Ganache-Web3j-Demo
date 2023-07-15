package com.own.third.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.alchemy.dto.AddressNFTBalance;
import com.own.third.api.alchemy.dto.Erc20TokenMetadata;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NFTTest {

    private final String apiKey = "";

    private final MediaType mediaType = MediaType.parse("application/json");

    private final ObjectMapper om = new ObjectMapper();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();

    @Test
    public void getAccountNfts() throws IOException {
        String owner = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://eth-goerli.g.alchemy.com/nft/v2/" + apiKey).newBuilder();
        urlBuilder.addPathSegment("getNFTs").addQueryParameter("owner", owner);
        String url = urlBuilder.build().toString();
        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            AddressNFTBalance addressNFTBalance = JSON.parseObject(response.body().string(), AddressNFTBalance.class);
            addressNFTBalance.getOwnedNfts().forEach(ownedNft ->
                    ownedNft.setUsedTokenId(Numeric.toBigInt(ownedNft.getId().getTokenId()).toString()));
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(addressNFTBalance));
        } else {
            System.out.println("Error");
        }
        System.out.println();
    }

    @Test
    public void tokenDetailRetrieving() throws IOException {
        String contractAddress = "0x07865c6e87b9f70255377e024ace6630c1eaa37f";

        JSONObject jsonRpc = new JSONObject();
        JSONArray params = new JSONArray();
        params.add(contractAddress);
        jsonRpc.put("id", 1);
        jsonRpc.put("jsonrpc", "2.0");
        jsonRpc.put("method", "alchemy_getTokenMetadata");
        jsonRpc.put("params", params);

        RequestBody body = RequestBody.create(jsonRpc.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url("https://eth-goerli.g.alchemy.com/v2/" + apiKey)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        JSONObject respJson = JSON.parseObject(response.body().string());
        Erc20TokenMetadata result = JSON.parseObject(respJson.getJSONObject("result").toJSONString(), Erc20TokenMetadata.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

}
