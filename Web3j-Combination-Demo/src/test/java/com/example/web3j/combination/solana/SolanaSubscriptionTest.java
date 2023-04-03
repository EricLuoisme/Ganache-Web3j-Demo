package com.example.web3j.combination.solana;

import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Roylic
 * 2023/2/2
 */
public class SolanaSubscriptionTest {


    //    private static final String SOLANA_DEV_WS_URL = "wss://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian";
    private static final String SOLANA_DEV_WS_URL = "ws://api.devnet.solana.com";

    private static final MediaType mediaType = MediaType.parse("application/json");

    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private static final String SUBSCRIBE_EVENT_LOG = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"logsSubscribe\",\"params\":[\"all\"]}";
    private static final String SUBSCRIBE_BLOCK = "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"blockSubscribe\",\"params\":[\"all\"]}";
    private static final String SUBSCRIBE_SLOT = "{\"jsonrpc\":\"2.0\",\"id\": 1,\"method\":\"slotSubscribe\"}";
    private static final String SUBSCRIBE_ACCOUNT = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"accountSubscribe\",\"params\":[\"%s\",{\"encoding\":\"jsonParsed\",\"commitment\":\"finalized\"}]}";


    @Test
    public void subscriptionTest() throws InterruptedException {

        Request request = new Request.Builder().url(SOLANA_DEV_WS_URL).build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                System.out.println("received event: " + text);
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                System.out.println("Web Socket Opened");
                String subscribeMsg = String.format(SUBSCRIBE_ACCOUNT, "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D");
                webSocket.send(subscribeMsg);
            }
        });

        TimeUnit.SECONDS.sleep(3);
        webSocket.send(String.format(SUBSCRIBE_ACCOUNT, "GQ6V9ZLVibN7eAtxEQxLJjXX8L9RybMJPpUCwi16vVgL"));


        Thread.currentThread().join();
    }
}
