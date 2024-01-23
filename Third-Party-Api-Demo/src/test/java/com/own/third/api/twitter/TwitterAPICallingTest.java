package com.own.third.api.twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TwitterAPICallingTest {

    private static final String USER_ID = "1571040121339904002";

    private static final String TWITTER_ID = "1746964887949934958";


    private static final String BASE_URL = "https://api.twitter.com/2/";


    private static final String CLIENT_ID = "";
    private static final String CLIENT_S = "";


    private final MediaType mediaType = MediaType.parse("application/json");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();


    @Test
    public void checkRetweet_WithTweetIdAndUserId() {

        // OAuth1.0a
        OAuth10aService auth10aService = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_S)
                .build(TwitterApi.instance());


        // path
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL)
//                .newBuilder();
//        urlBuilder.addPathSegment("users")
//                .addPathSegment(USER_ID)
//                .addPathSegment("retweets");
//        HttpUrl url = urlBuilder.build();
//        System.out.println("url: " + url);
//
//        // body
//        JSONObject obj = new JSONObject();
//        obj.put("tweet_id", TWITTER_ID);
//        RequestBody requestBody = RequestBody.create(obj.toJSONString(), mediaType);
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("accept", "application/json")
//                .addHeader("content-type", "application/json")
//                .addHeader("Authorization", "Bearer " + BEARER)
//                .post(requestBody)
//                .build();
//
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            String respStr = response.body().string();
//            JSONObject jsonObject = JSON.parseObject(respStr);
//            System.out.println();
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static Map<String, String> parseStringToMap(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            params.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return params;
    }

}