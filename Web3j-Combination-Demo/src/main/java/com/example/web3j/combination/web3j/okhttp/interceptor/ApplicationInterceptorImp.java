package com.example.web3j.combination.web3j.okhttp.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 应用调用Okhttp服务的前后拦截
 *
 * @author Roylic
 * @date 2022/4/24
 */
@Slf4j
public class ApplicationInterceptorImp implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        log.info(" >>> Application request intercept, url:{}, body:{}", request.url(), request.body());
        // need to set proceed, similar to the reflection that calling method.invoke() to force processing
        Response response = chain.proceed(request);
        log.info(" >>> Application response intercept, body:{}", response.body());
        return response;
    }
}
