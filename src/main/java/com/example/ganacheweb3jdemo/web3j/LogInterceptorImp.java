package com.example.ganacheweb3jdemo.web3j;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Okhttp对外请求与接收的拦截
 *
 * @author Roylic
 * @date 2022/4/24
 */
@Slf4j
public class LogInterceptorImp implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        // similar to the reflection's invoke, we could add any logic between the chain.proceed
        log.info(" >>> Okhttp log intercept request, method:{}", request.method());
        Response response = chain.proceed(request);
        log.info(" >>> Okhttp log intercept response, status:{}, time (million seconds):{}",
                response.isSuccessful(),
                response.receivedResponseAtMillis() - response.sentRequestAtMillis());
        return response;
    }
}
