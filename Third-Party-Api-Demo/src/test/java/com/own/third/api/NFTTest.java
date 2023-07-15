package com.own.third.api;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.alchemy.dto.AddressNFTBalance;
import com.own.third.api.alchemy.dto.NFTMetadata;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NFTTest {

    private final String apiKey = "";

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
    public void getHoldingNFTsByAddress() throws IOException {
        String owner = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://eth-goerli.g.alchemy.com/nft/v2/" + apiKey).newBuilder();
        urlBuilder.addPathSegment("getNFTs").addQueryParameter("owner", owner);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            AddressNFTBalance addressNFTBalance = JSON.parseObject(response.body().string(), AddressNFTBalance.class);
            addressNFTBalance.getNftMetadata().forEach(ownedNft ->
                    ownedNft.setUsedTokenId(Numeric.toBigInt(ownedNft.getId().getTokenId()).toString()));
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(addressNFTBalance));
        } else {
            System.out.println("Error");
        }
        System.out.println();
    }

    @Test
    public void nftMetadataQuery() throws IOException {

        String nftContract = "0xb1fac5b5b535fbdb4323aa7a0aac6039ce731c7f";
        String tokenId = "6390";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://eth-goerli.g.alchemy.com/nft/v2/" + apiKey).newBuilder();
        urlBuilder.addPathSegment("getNFTMetadata")
                .addQueryParameter("contractAddress", nftContract)
                .addQueryParameter("tokenId", tokenId);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            NFTMetadata nftMetadata = JSON.parseObject(response.body().string(), NFTMetadata.class);
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(nftMetadata));
        } else {
            System.out.println("Error");
        }
        System.out.println();


    }

}
