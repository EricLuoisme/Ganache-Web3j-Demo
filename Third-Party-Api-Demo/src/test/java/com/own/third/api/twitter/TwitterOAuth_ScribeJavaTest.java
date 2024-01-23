package com.own.third.api.twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class TwitterOAuth_ScribeJavaTest {

    private static final String CONSUMER_K = "";
    private static final String CONSUMER_S = "";


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final OAuth10aService service = new ServiceBuilder(CONSUMER_K)
                .apiSecret(CONSUMER_S)
                .build(TwitterApi.instance());
        final Scanner in = new Scanner(System.in);

        System.out.println("=== Twitter's OAuth Workflow ===");
        System.out.println();
        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        final OAuth1RequestToken requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        final String oauthVerifier = in.nextLine();
        System.out.println();

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        Map<String, String> params = parseStringToMap(accessToken.getRawResponse());
        System.out.println("OAuth Token: " + params.get("oauth_token"));
        System.out.println("OAuth Token Secret: " + params.get("oauth_token_secret"));
        System.out.println("User ID: " + params.get("user_id"));
        System.out.println("Screen Name: " + params.get("screen_name"));
        System.out.println();
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
