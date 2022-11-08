package com.e2pay.demo;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.e2pay.demo.E2PayUriEnums.AUTHORIZATION;

/**
 * E2Pay Api Connection Utils
 *
 * @author Roylic
 * 2022/11/8
 */
public class E2PayReqUtils {

    private static final String E2PAY_BASE_URL = "https://mobiletest.mbayar.co.id/switching/";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    private static final OkHttpClient OKHTTP_E2PAY_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(1500, TimeUnit.MILLISECONDS)
            .writeTimeout(1500, TimeUnit.MILLISECONDS)
            .connectionPool(new ConnectionPool(3, 1, TimeUnit.MINUTES))
            .build();


    public static String getCustomerAuthorizationCode(String userPhone, String md5Pwd, int retryTimes) {

        final Map<String, Object> jsonData = new HashMap<>(6);
        jsonData.put("client_id", "U0owV2XgERIrfkUGE0Ge609xGWuKFE1t");
        jsonData.put("client_secret", "BhZnzaqtxmx5KIylFySotj03ENdf3PTc");
        jsonData.put("response_type", "code");
        jsonData.put("user_type", "CUSTOMER");
        jsonData.put("username", userPhone);
        jsonData.put("password", md5Pwd);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + AUTHORIZATION.uri).newBuilder();
        RequestBody body = RequestBody.create(new JSONObject(jsonData).toJSONString(), MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .post(body)
                .build();

        return executeE2PayRequest(retryTimes, request);
    }


    @NotNull
    private static String executeE2PayRequest(int retryTimes, Request request) {
        int time = 0;
        String respBodyStr = "";
        while (time++ < retryTimes) {
            try {
                ResponseBody respBody = OKHTTP_E2PAY_CLIENT.newCall(request).execute().body();
                respBodyStr = null == respBody ? "" : respBody.string();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respBodyStr;
    }

}
