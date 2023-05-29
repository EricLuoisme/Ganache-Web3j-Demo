package com.example.web3j.combination.cosmos;

import cosmos.base.tendermint.v1beta1.Query;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    public void connectionTest() throws IOException {

        ManagedChannel build = ManagedChannelBuilder.forAddress("https://testnet-fx-grpc.functionx.io", 9090)
                .usePlaintext()
                .build();

        Request req = new Request.Builder()
                .url("https://testnet-fx-grpc.functionx.io:9090/status")
                .get()
                .build();

        Response execute = okHttpClient.newCall(req).execute();
        System.out.println();


        Query.GetLatestBlockRequest request = Query.GetLatestBlockRequest.newBuilder().build();


    }

}
