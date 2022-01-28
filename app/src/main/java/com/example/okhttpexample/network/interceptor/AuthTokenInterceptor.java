package com.example.okhttpexample.network.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.CacheControl;
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

        Log.i(TAG, "intercept() => " + originalRequest.url());

        String url = originalRequest.url().toString();
        if (url != null && !url.contains("/rest/v1/auth")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String accessToken = preferences.getString("access_token", "");
            if(accessToken.equalsIgnoreCase("")) {
                throw new IOException("There is no access token.");
            }

            Log.i(TAG, "AccessToken => " + accessToken);

            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken);
            Request request = builder.build();

            return chain.proceed(request);
        }

        return chain.proceed(originalRequest);
    }
}
