package com.example.web3j.combination.cosmos;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Cosmos related test
 *
 * @author Roylic
 * 2023/5/23
 */
public class CosmosConnectionTest {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    @Test
    public void connectionTest() {

    }

}
