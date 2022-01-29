package com.example.okhttpexample.network.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.okhttpexample.common.constants.AppConstants;
import com.example.okhttpexample.common.utils.PreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthTokenInterceptor implements Interceptor {
    private static final String TAG = AuthTokenInterceptor.class.getSimpleName();

    private Context context;

    public AuthTokenInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        if (url != null) {
            if (url.contains("/rest/v1/auth") || url.contains("/rest/v1/auth/refresh_token")) {
                return chain.proceed(originalRequest);
            } else {
                String accessToken = PreferencesUtils.getPreferences(context, AppConstants.ACCESS_TOKEN_KEY);
                if (accessToken == null || accessToken.equalsIgnoreCase("")) {
                    throw new IOException("Access Token Error");
                }

                Request.Builder builder = originalRequest.newBuilder()
                        .header(AppConstants.AUTH_HEADER_NAME, AppConstants.AUTH_PREFIX + accessToken);
                Request request = builder.build();

                return chain.proceed(request);
            }
        } else {
            throw new IOException("URL Error");
        }
    }
}
