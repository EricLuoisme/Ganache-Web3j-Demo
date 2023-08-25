package com.example.web3j.combination.lightning;


import com.example.web3j.combination.web3j.okhttp.interceptor.ApplicationInterceptorImp;
import com.example.web3j.combination.web3j.okhttp.interceptor.LogInterceptorImp;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Lightning network -> remote payment status checking
 */
public class LightningPaymentChecking {


    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(new ApplicationInterceptorImp())
            .addNetworkInterceptor(new LogInterceptorImp())
            .retryOnConnectionFailure(false)
            .build();

    @Test
    public void remotePayment() throws IOException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://zakeovuu6nkxv2cmfaoy2i7hvammbksej7sic5ojp5gjxafigu5l6zid.onion/" + "v1/getinfo").newBuilder();
        String url = urlBuilder
                .build()
                .toString();

        Request requestGet = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(requestGet);

        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println();
            System.out.println(body.string());
            System.out.println();
        }
    }

}
