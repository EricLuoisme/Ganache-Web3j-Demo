package com.own.third.api.twitter;

import com.alibaba.fastjson2.JSONObject;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TwitterOAuth_APICallingTest {

    private static final String USER_ID = "1571040121339904002";

    private static final String TWITTER_ID = "1746964887949934958";


    private static final String CONSUMER_K = "";
    private static final String CONSUMER_S = "";

    private static final String AUTH_T = "";

    private static final String AUTH_S = "";


    private final MediaType mediaType = MediaType.parse("application/json");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();


    @Test
    public void checkRetweet_WithTweetIdAndUserId() {

        // OAuth1.0a
        OAuth10aService auth10aService = new ServiceBuilder(CONSUMER_K)
                .apiSecret(CONSUMER_S)
                .build(TwitterApi.instance());

        String url = "https://api.twitter.com/2/users/" + USER_ID + "/retweets";

        OAuthRequest request = new OAuthRequest(Verb.POST, url);
        request.addBodyParameter("tweet_id", TWITTER_ID);
        auth10aService.signRequest(new OAuth1AccessToken(AUTH_T, AUTH_S), request);


        // Create JSON object for the body
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("tweet_id", TWITTER_ID);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), mediaType);
        Request okHttpRequest = new Request.Builder()
                .url(request.getCompleteUrl())
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        try {
            Response response = okHttpClient.newCall(okHttpRequest).execute();
            String respStr = response.body().string();
            System.out.println(respStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}