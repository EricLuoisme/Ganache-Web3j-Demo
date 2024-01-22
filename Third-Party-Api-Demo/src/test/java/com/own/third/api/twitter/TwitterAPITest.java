package com.own.third.api.twitter;

import com.alibaba.fastjson2.JSON;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Get2TweetsIdResponse;
import com.twitter.clientlib.model.ResourceUnauthorizedProblem;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class TwitterAPITest {

    private static final String BEARER = "";

    private static final TwitterApi apiInstance = new TwitterApi(new TwitterCredentialsBearer(BEARER));


    @Test
    public void retrieveTweetInfo() {

        String tweetId = "1749122435029299397";

        Set<String> tweetFields = new HashSet<>();
        tweetFields.add("created_at");
        tweetFields.add("referenced_tweets");

        Set<String> expansions = new HashSet<>();
        expansions.add("author_id");
        expansions.add("referenced_tweets.id");
        expansions.add("referenced_tweets.id.author_id");

        // request
        try {
            Get2TweetsIdResponse result = apiInstance.tweets()
                    .findTweetById(tweetId)
                    .tweetFields(tweetFields)
                    .expansions(expansions)
                    .execute();

            if (result.getErrors() != null) {
                System.out.println("Errors:");
                result.getErrors().forEach(e -> {
                    System.out.println(e.toString());
                    if (e instanceof ResourceUnauthorizedProblem) {
                        System.out.println(e.getTitle() + " " + e.getDetail());
                    }
                });
            } else {
                // success
                System.out.println("findTweetById - Tweet Text: " + result);
                System.out.println(JSON.toJSONString(result));
            }

        } catch (ApiException e) {
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }


    }

}
