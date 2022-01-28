package com.example.okhttpexample.network.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ForceCacheInterceptor implements Interceptor {
    private static final String TAG = ForceCacheInterceptor.class.getSimpleName();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if (!isInternetAvailable()) {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        
        return chain.proceed(builder.build());
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.e(TAG, "isInternetAvailable() => UnknownHostException");
        }
        return false;
    }
}
