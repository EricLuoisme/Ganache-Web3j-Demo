package com.example.web3j.combination.stream;

import okhttp3.*;
import okio.BufferedSource;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FluxOkHttp {

    private final OkHttpClient client = new OkHttpClient();

    @Test
    public void streamTest() throws IOException {

        Request request = new Request.Builder()
                .url("http://localhost:8005/sse/testFlux")
                .get()
                .build();

        // execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Un expected code:" + response);
            }
            // extract resp body
            try (ResponseBody body = response.body()) {
                if (body == null) {
                    throw new IOException("Empty body");
                }
                // use stream reader
                try (BufferedSource source = body.source()) {
                    while (!source.exhausted()) {
                        System.out.println(source.readUtf8Line());
                    }
                }
//                try (
//                        InputStream inputStream = body.byteStream();
//                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
//                ) {
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        System.out.println(line);
//                    }
//                }
            }
        }
    }

}
