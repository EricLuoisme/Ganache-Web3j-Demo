package com.example.web3j.combination.wss;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Test with okhttp
 *
 * @author Roylic
 * 2022/11/9
 */
public class OkhttpWssTest {

    private static final String SUBSCRIBE_NEW_BLOCK = "{\"jsonrpc\":\"2.0\", \"id\": 1, \"method\": \"eth_subscribe\", \"params\": [\"newHeads\"]}";

    // address is for the log emitter (the contract), not for specific balance address
    // the topics -> is the event signature
    private static final String SUBSCRIBE_EVENT_LOG = "{\"jsonrpc\":\"2.0\",\"id\": 1, \"method\": \"eth_subscribe\", \"params\": [\"logs\", {" +
//            "\"address\": \"0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48\", " +
            "\"topics\": [\"0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef\"]}]}";


    private static final String wssGoerli = "wss://goerli.infura.io/ws/v3/3f0482cf4c3545dbabaeab75f414e467";
    private static final String wssArbitrum = "wss://arb-goerli.g.alchemy.com/v2/OYmX5E0ny2dezNXeETpswUgDXzuZdE8w";
    private static final String wssOptimism = "wss://opt-goerli.g.alchemy.com/v2/h_8ml7t3FxTmkRzww_NvLqiw0ZbusFwN";
    private static final String wssPolygon = "wss://polygon-mumbai.g.alchemy.com/v2/0AvU4bENYqbsSI6km3CEwrgBbyFY_NZX";


    private final static Request request = new Request.Builder().url(wssPolygon).build();


    @Test
    public void getEventSignature() {
        // 0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef
        Event transfer = new Event("Transfer", Arrays.asList(
                TypeReference.create(Address.class, true),
                TypeReference.create(Address.class, true),
                TypeReference.create(Uint256.class)));

        String encodeEventSignature = EventEncoder.encode(transfer);
        System.out.println(encodeEventSignature);
    }


    @Test
    public void subscribeSpecificEvent() throws InterruptedException {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .pingInterval(5, TimeUnit.SECONDS)
                .build();

        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                System.out.println("Web Socket Closed");
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                System.out.println("Web Socket is Closing");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                System.out.println("received event: " + text);
                if (JSONObject.parseObject(text).getOrDefault("method", "").equals("eth_subscription")) {
                    JSONObject contractEventJsonObj = JSONObject.parseObject(text).getJSONObject("params").getJSONObject("result");
                    EthLog.LogObject logObject = JSONObject.parseObject(contractEventJsonObj.toJSONString(), EthLog.LogObject.class);
                    System.out.println("txnHash:" + logObject.getTransactionHash());
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                System.out.println("Web Socket Opened");
                webSocket.send(SUBSCRIBE_EVENT_LOG);
            }
        });

        Thread.currentThread().join();
    }


    @Test
    public void subscribeNewBlock() throws InterruptedException {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .pingInterval(5, TimeUnit.SECONDS)
                .build();

        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                System.out.println("Web Socket Closed");
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                System.out.println("Web Socket is Closing");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                System.out.println("text: " + text);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                System.out.println("Web Socket Opened");
                webSocket.send(SUBSCRIBE_NEW_BLOCK);
            }
        });

        Thread.currentThread().join();
    }


}
