package com.example.okhttpexample.network.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorInterceptor implements Interceptor {
    private static final String TAG = ErrorInterceptor.class.getSimpleName();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 400) {
            Log.e(TAG, "intercept: Bad Request Error");
        } else if (response.code() == 401) {
            Log.e(TAG, "intercept: Unauthorized Error");
            String newToken = "";
            if (newToken != null) {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + newToken)
                        .build();
                return chain.proceed(newRequest);
            }
        } else if (response.code() == 403) {
            Log.e(TAG, "intercept: Forbidden Error");
        } else if (response.code() == 404) {
            Log.e(TAG, "intercept: NotFound Error");
        }

        return response;
    }
}
