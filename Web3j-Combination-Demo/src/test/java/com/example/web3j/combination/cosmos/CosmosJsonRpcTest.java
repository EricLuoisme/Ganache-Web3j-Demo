package com.example.web3j.combination.cosmos;

import com.alibaba.fastjson.JSONArray;
import com.example.web3j.combination.ssl.TrustAllX509CertManager;
import cosmos.base.tendermint.v1beta1.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import org.bouncycastle.util.encoders.Hex;
import org.example.cosmos.ABCIQueryParam;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Cosmos related test
 *
 * @author Roylic
 * 2023/5/23
 */
public class CosmosJsonRpcTest {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

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
            String respStr = resp.body().string();
            System.out.println(respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void blockDecodingTest() {
        Request req = new Request.Builder()
                .url("https://testnet-fx-json.functionx.io:26657/block?height=8610264")
                .build();
        try (Response resp = okHttpClient.newCall(req).execute()) {
            String respStr = resp.body().string();
            System.out.println(respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void accountBalanceTest() {
        Request request = new Request.Builder()
                .url("https://testnet-fx-json.functionx.io:26657/cosmos/bank/v1/balances/" + "fx1nperuyt9ag7zdmqw35axdjuxw675ttksdvkpds")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void abciCallingTest() throws IOException {

        Query.GetBlockByHeightRequest request = Query.GetBlockByHeightRequest.newBuilder()
                .setHeight(8610264)
                .build();

        String encodedData = Hex.toHexString(request.toByteArray());

        ABCIQueryParam queryParam = ABCIQueryParam.builder()
                .data(encodedData)
                .path("/cosmos.base.tendermint.v1beta1.Service/GetBlockByHeight")
                .build();


        ArrayList<JsonRpcStandard> listInput = new ArrayList<>();
        listInput.add(
                JsonRpcStandard.builder()
                        .jsonRpc("2.0")
                        .id("java-roy01cup")
                        .method("abci_query")
                        .params(queryParam)
                        .build());

        String postBody = JSONArray.toJSONString(listInput);

        Request jsonRpcRequest = new Request.Builder()
                .url("https://testnet-fx-json.functionx.io:26657")
                .post(RequestBody.create(postBody.getBytes(StandardCharsets.UTF_8), JSON_MEDIA_TYPE))
                .build();

        Response response = okHttpClient.newCall(jsonRpcRequest).execute();
        String string = response.body().string();
        System.out.println(string);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonRpcStandard<T> {
        private String jsonRpc;
        private String id;
        private String method;
        private T params;
    }

}
