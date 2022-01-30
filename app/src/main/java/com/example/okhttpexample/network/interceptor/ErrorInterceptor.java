package com.example.okhttpexample.network.interceptor;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.okhttpexample.common.constants.AppConstants;
import com.example.okhttpexample.common.utils.PreferencesUtils;
import com.example.okhttpexample.network.NetworkManager;
import com.example.okhttpexample.network.NetworkResponseListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorInterceptor implements Interceptor {
    private static final String TAG = ErrorInterceptor.class.getSimpleName();

    private Context context;
    private String accessToken;

    public ErrorInterceptor(Context context) {this.context = context;}

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 400) {
            Log.e(TAG, "Bad Request Error");
        } else if (response.code() == 401) {
            Log.e(TAG, "Unauthorized Error");
            this.refreshToken(context);
            String accessToken = PreferencesUtils.getPreferences(context, AppConstants.ACCESS_TOKEN_KEY);
            if (accessToken == null || accessToken.equalsIgnoreCase("")) {
                throw new IOException("Access Token Error");
            } else {
                Request newRequest = chain.request().newBuilder()
                        .addHeader(AppConstants.AUTH_HEADER_NAME, AppConstants.AUTH_PREFIX + accessToken)
                        .build();
                return chain.proceed(newRequest);
            }
        } else if (response.code() == 403) {
            Log.e(TAG, "Forbidden Error");
        } else if (response.code() == 404) {
            Log.e(TAG, "NotFound Error");
        }

        return response;
    }

    /**
     * Refresh Token
     * @param context
     * @throws IOException
     */
    private void refreshToken(Context context) throws IOException {
        // #1. Generate request message with Json
        String refreshToken = PreferencesUtils.getPreferences(context, AppConstants.REFRESH_TOKEN_KEY);
        if (refreshToken == null || refreshToken.equalsIgnoreCase("")) {
            throw new IOException("Refresh Token Error");
        }

        Map<String, String> reqBodyMap = new LinkedHashMap<>();
        reqBodyMap.put(AppConstants.REFRESH_TOKEN_KEY, refreshToken);

        // #2. Call API
        NetworkManager.sendToServer(context, "/rest/v1/auth/refresh_token", reqBodyMap, new NetworkResponseListener() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Refresh Token Error");
            }

            @Override
            public void onSuccess(Map<String, Object> resMap) {
                // #1. Save Access Token / Refresh Token
                Map<String, String> sharedMap = new HashMap<String, String>();
                sharedMap.put(AppConstants.ACCESS_TOKEN_KEY, (String) resMap.get(AppConstants.ACCESS_TOKEN_KEY));
                sharedMap.put(AppConstants.REFRESH_TOKEN_KEY, (String) resMap.get(AppConstants.REFRESH_TOKEN_KEY));
                PreferencesUtils.setPreferences(context, sharedMap);
            }
        });
    }
}
