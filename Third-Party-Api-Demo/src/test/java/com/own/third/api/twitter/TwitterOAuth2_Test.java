package com.own.third.api.twitter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;

import java.util.Scanner;

public class TwitterOAuth2_Test {

    private static final String OAUTH_2_CLIENT_ID = "";

    private static final String OAUTH_2_CLIENT_S = "";

    private static final TwitterCredentialsOAuth2 oauth2Credential = new TwitterCredentialsOAuth2(
            OAUTH_2_CLIENT_ID, OAUTH_2_CLIENT_S, "", "");


    public static void main(String[] args) {

        // 1. get oauth 2 access token
        OAuth2AccessToken accessToken = getAccessToken(oauth2Credential);
        if (accessToken == null) {
            System.out.println("Error, cease because of null accessToken");
            return;
        }

        // 2. print
        System.out.println("Access Token: " + accessToken.getAccessToken());
        System.out.println("Refresh Token: " + accessToken.getRefreshToken());
        System.out.println("Expires In: " + accessToken.getExpiresIn());
        System.out.println("Token Type: " + accessToken.getTokenType());
        System.out.println("Scope: " + accessToken.getScope());
    }


    public static OAuth2AccessToken getAccessToken(TwitterCredentialsOAuth2 credentials) {
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "http://twitter.com",
                "offline.access tweet.read users.read");

        OAuth2AccessToken accessToken = null;
        try {
            final Scanner in = new Scanner(System.in, "UTF-8");
            System.out.println("Fetching the Authorization URL...");

            final String secretState = "state";
            PKCE pkce = new PKCE();
            pkce.setCodeChallenge("challenge");
            pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
            pkce.setCodeVerifier("challenge");
            String authorizationUrl = service.getAuthorizationUrl(pkce, secretState);

            System.out.println("Go to the Authorization URL and authorize your App:\n" +
                    authorizationUrl + "\nAfter that paste the authorization code here\n>>");
            final String code = in.nextLine();
            System.out.println("\nTrading the Authorization Code for an Access Token...");
            accessToken = service.getAccessToken(pkce, code);

            System.out.println("Access token: " + accessToken.getAccessToken());
            System.out.println("Refresh token: " + accessToken.getRefreshToken());
        } catch (Exception e) {
            System.err.println("Error while getting the access token:\n " + e);
            e.printStackTrace();
        }
        return accessToken;
    }

}
