package com.example.ganacheweb3jdemo;


import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void callRpc() throws IOException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(TERRA_BOMBAY_TEST_URL + BALANCE_PATH).newBuilder();
        // 由于并不是路径参数, 不能使用addQueryParameter, 而是作为路径拼接
        urlBuilder.addPathSegment(TEST_WALLET_ADDRESS);
        String url = urlBuilder.build().toString();
        System.out.println(url);

        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String string = response.body().string();
        System.out.println(string);

    }
}
