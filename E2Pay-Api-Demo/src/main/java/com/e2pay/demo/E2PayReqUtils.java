package com.e2pay.demo;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.e2pay.demo.E2PayUriEnums.*;

/**
 * E2Pay Api Connection Utils
 *
 * @author Roylic
 * 2022/11/8
 */
public class E2PayReqUtils {

    private static final String E2PAY_BASE_URL = "https://mobiletest.mbayar.co.id/switching";

    private static final OkHttpClient OKHTTP_E2PAY_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(3, 1, TimeUnit.MINUTES))
            .build();


    public static String getCustomerAuthCodeByJSON(String userPhone, String md5Pwd, int retryTimes) {

        final Map<String, Object> jsonData = new HashMap<>(6);
        jsonData.put("client_id", "U0owV2XgERIrfkUGE0Ge609xGWuKFE1t");
        jsonData.put("client_secret", "BhZnzaqtxmx5KIylFySotj03ENdf3PTc");
        jsonData.put("response_type", "code");
        jsonData.put("user_type", "CUSTOMER");
        jsonData.put("username", userPhone);
        jsonData.put("password", md5Pwd);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + AUTH_CODE.uri).newBuilder();
        RequestBody body = RequestBody.create(new JSONObject(jsonData).toJSONString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();

        return executeE2PayRequest(retryTimes, request);
    }

    public static String getCustomerAuthTokenByForm(String userAuthCode, int retryTimes) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + OAUTH_TOKEN.uri).newBuilder();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("client_id", "U0owV2XgERIrfkUGE0Ge609xGWuKFE1t");
        formBodyBuilder.add("client_secret", "BhZnzaqtxmx5KIylFySotj03ENdf3PTc");
        formBodyBuilder.add("grant_type", "authorization_code");
        formBodyBuilder.add("code", userAuthCode);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(formBodyBuilder.build())
                .build();

        return executeE2PayRequest(retryTimes, request);
    }

    public static String getCustomerInfo(String accessToken, int retryTimes) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + CUSTOMER_INFO.uri).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        return executeE2PayRequest(retryTimes, request);
    }

    public static String getCustomerLimit(String accessToken, String accountId, int retryTimes) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + CUSTOMER_LIMIT.uri)
                .newBuilder()
                .addQueryParameter("accountId", accountId);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        return executeE2PayRequest(retryTimes, request);
    }

    public static String inquiryQRIS(String accessToken, String qrCode, int retryTimes) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + QRIS_INQUIRY.uri)
                .newBuilder()
                .addQueryParameter("qrCode", qrCode);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        return executeE2PayRequest(retryTimes, request);
    }

    public static String payQRIS(String accessToken, String inquiryId, String md5Pwd, double amount, String localDebitId, double tip, int retryTimes) {

        final Map<String, Object> jsonData = new HashMap<>(6);
        jsonData.put("inquiryId", inquiryId);
        jsonData.put("password", md5Pwd);
        jsonData.put("amount", amount);
        jsonData.put("clientRef", localDebitId);
        jsonData.put("sourceId", "PUNDIX");
        jsonData.put("tip", tip);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(E2PAY_BASE_URL + QRIS_PAYMENT.uri).newBuilder();
        RequestBody body = RequestBody.create(new JSONObject(jsonData).toJSONString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + accessToken)
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
