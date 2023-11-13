package com.own.third.api.slack;

import com.own.third.api.TrustAllX509CertManager;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SlackWebhookTest {

    private static String PAYLOAD = "{\"blocks\":[{\"type\":\"header\",\"text\":{\"type\":\"plain_text\",\"text\":\"%s\",\"emoji\":true}},{\"type\":\"divider\"},{\"type\":\"section\",\"fields\":[{\"type\":\"mrkdwn\",\"text\":\"*Server*:\\n%s\"},{\"type\":\"mrkdwn\",\"text\":\"*Date*:\\n%s\"},{\"type\":\"mrkdwn\",\"text\":\"*Description*:\\n%s\"}]}]}";

    private static final String REQUEST_URL = "https://hooks.slack.com/services/";

    private final MediaType mediaType = MediaType.parse("application/json");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void apiCalling() throws IOException {

        String serverName = "Spring-Cloud-Specific-Service";
        String description = "Description of the Error";
        String dateFmt = "2099-10-10 06:29:49";

        RequestBody body = RequestBody.create(String.format(PAYLOAD, serverName, serverName, description, dateFmt), mediaType);
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        System.out.println(response.body().string()); // Slack response in pure string
    }
}
