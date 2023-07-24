package com.own.third.api.infura;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Infura not decoded all NFT, thus, some 'name', 'description', 'imgUrl' are result in null
 *
 * @author Roylic
 * 2023/7/24
 */
public class NFTTest {

    private final ObjectMapper om = new ObjectMapper();

    private static final String NFT_REQUEST_URL = "https://nft.api.infura.io/networks/%d/accounts/%s/assets/nfts";

    private static final String CREDENTIAL = Credentials.basic("", "");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void getAllNftOwnedByAddress() throws IOException {

        String owner = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        Long chainId = 5L;

        Request request = new Request.Builder()
                .url(String.format(NFT_REQUEST_URL, chainId, owner))
                .addHeader("accept", "application/json")
                .addHeader("Authorization", CREDENTIAL)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        NftResponse nftResponse = JSON.parseObject(response.body().string(), NftResponse.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(nftResponse));
        System.out.println();
    }

}
