package com.example.web3j.combination.cosmos;

import com.example.web3j.combination.ssl.TrustAllX509CertManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();

    @Test
    public void connectionTest() {

        Request req = new Request.Builder()
                .url("https://testnet-fx-json.functionx.io:26657/status")
                .build();

        try (Response resp = okHttpClient.newCall(req).execute()) {

            System.out.println(resp.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
