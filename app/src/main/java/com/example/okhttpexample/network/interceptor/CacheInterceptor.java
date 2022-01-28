package com.example.okhttpexample.network.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    private static final String TAG = CacheInterceptor.class.getSimpleName();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(10, TimeUnit.DAYS)
                .build();
        return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build();
    }
}
