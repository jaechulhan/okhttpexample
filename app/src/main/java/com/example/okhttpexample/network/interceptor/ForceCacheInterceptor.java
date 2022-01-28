package com.example.okhttpexample.network.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.okhttpexample.common.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ForceCacheInterceptor implements Interceptor {
    private static final String TAG = ForceCacheInterceptor.class.getSimpleName();

    private Context context;

    public ForceCacheInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if (!NetworkUtils.isNetworkAvailable(context)) {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        
        return chain.proceed(builder.build());
    }
}
