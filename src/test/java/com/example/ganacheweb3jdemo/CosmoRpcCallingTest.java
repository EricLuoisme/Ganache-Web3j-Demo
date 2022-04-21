package com.example.ganacheweb3jdemo;


import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * 尝试进行RPC解析测试
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class CosmoRpcCallingTest {

    private final static String TERRA_BOMBAY_TEST_URL = "https://bombay-lcd.terra.dev/";

    private final static String TEST_WALLET_ADDRESS = "terra1n6ery8d7gq8m9ut0fvsj3n3rw7zw03s7ludxtl";

    private final static String BALANCE_PATH = "cosmos/bank/v1beta1/balances/";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");


    @Test
    public void callRpc() {

        byte[] inputData = TEST_WALLET_ADDRESS.getBytes(StandardCharsets.UTF_8);

        RequestBody requestBody = RequestBody.create(inputData, JSON_MEDIA_TYPE);

        new Request.Builder().url(TERRA_BOMBAY_TEST_URL + BALANCE_PATH).get().build();

    }
}
