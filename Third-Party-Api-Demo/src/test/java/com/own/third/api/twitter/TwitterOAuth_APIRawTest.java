package com.own.third.api.twitter;

import com.alibaba.fastjson2.JSONObject;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TwitterOAuth_APIRawTest {

    private static final String USER_ID = "1571040121339904002";

    private static final String TWITTER_ID = "1573323429251612673";

    private static final String BASE_URL = "https://api.twitter.com/2";

    private static final String CONSUMER_K = "";
    private static final String CONSUMER_S = "";

    private static final String AUTH_T = "";

    private static final String AUTH_S = "";

    private static final String BEARER = "";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();


    @Test
    public void retweetForUser() throws IOException, ExecutionException, InterruptedException {

        // OAuth1.0a
        OAuth10aService auth10aService = new ServiceBuilder(CONSUMER_K)
                .apiSecret(CONSUMER_S)
                .build(TwitterApi.instance());

        String url = BASE_URL + "/users/" + USER_ID + "/retweets";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("tweet_id", TWITTER_ID);

        OAuthRequest request = new OAuthRequest(Verb.POST, url);
        request.addHeader("Content-Type", "application/json");
        request.setPayload(jsonBody.toJSONString());
        auth10aService.signRequest(new OAuth1AccessToken(AUTH_T, AUTH_S), request);

        Response resp = auth10aService.execute(request);
        String bodyStr = resp.getBody();
        System.out.println(bodyStr);

    }

    @Test
    public void retweetByUser() {

        // path
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addPathSegment("tweets")
                .addPathSegment(TWITTER_ID)
                .addPathSegment("retweeted_by");
        HttpUrl url = urlBuilder.build();
        System.out.println("url: " + url);

        // request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER)
                .get()
                .build();

        try {
            okhttp3.Response response = okHttpClient.newCall(request).execute();
            String respStr = response.body().string();
            System.out.println("Response: " + respStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void likedByUser() {
        // path
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addPathSegment("tweets")
                .addPathSegment(TWITTER_ID)
                .addPathSegment("retweeted_by");
        HttpUrl url = urlBuilder.build();
        System.out.println("url: " + url);

        // request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER)
                .get()
                .build();

        try {
            okhttp3.Response response = okHttpClient.newCall(request).execute();
            String respStr = response.body().string();
            System.out.println("Response: " + respStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}