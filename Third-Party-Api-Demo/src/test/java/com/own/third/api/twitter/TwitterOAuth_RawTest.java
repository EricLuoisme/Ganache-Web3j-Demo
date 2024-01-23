package com.own.third.api.twitter;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TwitterOAuth_RawTest {

    private static final String baseAuthUrl = "https://api.twitter.com/oauth";

    private static final String CONSUMER_K = "";

    private static final String CONSUMER_S = "";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    @Test
    public void oauth1_accTokenWholeProcess() {

        Twitter twitterIns = new TwitterFactory().getInstance();
        twitterIns.setOAuthConsumer(CONSUMER_K, CONSUMER_S);

        try {
            // 1. get request token
            RequestToken requestToken = twitterIns.getOAuthRequestToken();
            String token = requestToken.getToken();
            String tokenSecret = requestToken.getTokenSecret();
            System.out.println("Token: " + token);
            System.out.println("Token Secret: " + tokenSecret);

            // 2. build request path
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseAuthUrl)
                    .newBuilder();
            urlBuilder.addPathSegment("authorize")
                    .addQueryParameter("oauth_token", token)
                    .addQueryParameter("oauth_token_secret", tokenSecret)
                    .addQueryParameter("oauth_callback_confirmed", "true");
            System.out.println("Authorize URL: " + urlBuilder.build());

        } catch (TwitterException e) {
            System.err.println("Status code: " + e.getStatusCode());
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("Error message: " + e.getErrorMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void oauth1_accessToken() {

        String token = "5uIbXwAAAAABr1btAAABjTUUQl4";
        String oauthVerifier = "4846211";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseAuthUrl)
                .newBuilder();
        urlBuilder.addPathSegment("access_token")
                .addQueryParameter("oauth_token", token)
                .addQueryParameter("oauth_verifier", oauthVerifier);
        HttpUrl url = urlBuilder.build();
        System.out.println("url: " + url);

        RequestBody requestBody = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String respStr = response.body().string();
            if (respStr.contains("&")) {
                Map<String, String> params = parseStringToMap(respStr);
                System.out.println("OAuth Token: " + params.get("oauth_token"));
                System.out.println("OAuth Token Secret: " + params.get("oauth_token_secret"));
                System.out.println("User ID: " + params.get("user_id"));
                System.out.println("Screen Name: " + params.get("screen_name"));
            } else {
                System.out.println(respStr);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    private static Map<String, String> parseStringToMap(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            params.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return params;
    }
}
