package com.example.okhttpexample.network.interceptor;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.okhttpexample.BuildConfig;
import com.example.okhttpexample.LoginActivity;
import com.example.okhttpexample.MainActivity;
import com.example.okhttpexample.common.constants.AppConstants;
import com.example.okhttpexample.common.utils.PreferencesUtils;
import com.example.okhttpexample.network.NetworkManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private void refreshToken(Context context) throws IOException{
        // Start - Call API
        OkHttpClient client = NetworkManager.getInstance(context).getClient();

        String refreshToken = PreferencesUtils.getPreferences(context, AppConstants.REFRESH_TOKEN_KEY);
        if (refreshToken == null || refreshToken.equalsIgnoreCase("")) {
            throw new IOException("Refresh Token Error");
        }

        // #1. Generate request message with Json
        HashMap<String, Object> reqMap = new LinkedHashMap<>();
        reqMap.put(AppConstants.REFRESH_TOKEN_KEY, refreshToken);

        Gson gson = new Gson();
        String json = gson.toJson(reqMap);

        RequestBody jsonBody = RequestBody.create(
                MediaType.parse(AppConstants.JSON_CONTENT_TYPE), json);

        // #2. Create request with post
        String url = BuildConfig.BASE_URL + "/rest/v1/auth/refresh_token";

        Request request = new Request.Builder()
                .url(url)
                .post(jsonBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Gson gson = new Gson();
                    Map map = gson.fromJson(result, Map.class);

                    Map<String, String> sharedMap = new HashMap<String, String>();
                    sharedMap.put(AppConstants.ACCESS_TOKEN_KEY, (String) map.get(AppConstants.ACCESS_TOKEN_KEY));
                    sharedMap.put(AppConstants.REFRESH_TOKEN_KEY, (String) map.get(AppConstants.REFRESH_TOKEN_KEY));
                    PreferencesUtils.setPreferences(context, sharedMap);
                }
            }
        });
        // End - Call API
    }
}
