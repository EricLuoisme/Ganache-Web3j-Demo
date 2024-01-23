package com.own.third.api.twitter;

import com.alibaba.fastjson2.JSONObject;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TwitterOAuth_APICallingTest {

    private static final String USER_ID = "1571040121339904002";

    private static final String TWITTER_ID = "1746964887949934958";

    private static final String CONSUMER_K = "";
    private static final String CONSUMER_S = "";

    private static final String AUTH_T = "";

    private static final String AUTH_S = "";


    @Test
    public void checkRetweet_WithTweetIdAndUserId() throws IOException, ExecutionException, InterruptedException {

        // OAuth1.0a
        OAuth10aService auth10aService = new ServiceBuilder(CONSUMER_K)
                .apiSecret(CONSUMER_S)
                .build(TwitterApi.instance());

        String url = "https://api.twitter.com/2/users/" + USER_ID + "/retweets";
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

}