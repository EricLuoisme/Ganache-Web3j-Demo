package com.example.ganacheweb3jdemo;

import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Doc:
 * https://docs.polygon.technology/docs/develop/network-details/network/
 *
 * Open-Api:
 * https://open-api-staging.polygon.technology/api-docs/#/Balance
 *
 * Testnet-Rpc:
 * https://rpc-mumbai.matic.today
 * https://matic-mumbai.chainstacklabs.com
 * https://rpc-mumbai.maticvigil.com
 * https://matic-testnet-archive-rpc.bwarelabs.com
 *
 * @author Roylic
 * 2022/5/12
 */
public class PolygonMumbaiTest {

    private final static String POLYGON_MUMBAI_TESTNET_PATH = "https://rpc.poa.psdk.io:8545";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    @Test
    public void simpleConnection() throws IOException {

        // Polygon Api 路径不变, 主要在于请求内容不同;
        Request requestPost = new Request.Builder()
                .url(POLYGON_MUMBAI_TESTNET_PATH)
                .post(RequestBody.create("{\"jsonrpc\":\"2.0\",\"method\":\"eth_chainId\",\"params\":[],\"id\":1}", MediaType.parse("application/json")))
                .build();

        Call call = okHttpClient.newCall(requestPost);
        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println(body.string());
        }

    }

}
