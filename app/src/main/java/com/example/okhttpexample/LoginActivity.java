package com.example.okhttpexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.okhttpexample.common.constants.AppConstants;
import com.example.okhttpexample.common.utils.PreferencesUtils;
import com.example.okhttpexample.network.NetworkManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextInputEditText tiUsername, tiPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tiUsername = findViewById(R.id.tiUsername);
        tiPassword = findViewById(R.id.tiPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        progressBar = findViewById(R.id.progress);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                // Start - Call API
                OkHttpClient client = NetworkManager.getInstance(LoginActivity.this).getClient();

                // #1. Generate request message with Json
                HashMap<String, Object> reqMap = new LinkedHashMap<>();
                reqMap.put("username", tiUsername.getText().toString());
                reqMap.put("password", tiPassword.getText().toString());

                Gson gson = new Gson();
                String json = gson.toJson(reqMap);

                RequestBody jsonBody = RequestBody.create(
                        MediaType.parse(AppConstants.JSON_CONTENT_TYPE), json);

                // #2. Create request with post
                String url = BuildConfig.BASE_URL + "/rest/v1/auth";

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
                            Log.d(TAG, "onResponse: " + map.toString());

                            Map<String, String> sharedMap = new HashMap<String, String>();
                            sharedMap.put(AppConstants.ACCESS_TOKEN_KEY, (String) map.get(AppConstants.ACCESS_TOKEN_KEY));
                            sharedMap.put(AppConstants.REFRESH_TOKEN_KEY, (String) map.get(AppConstants.REFRESH_TOKEN_KEY));
                            PreferencesUtils.setPreferences(LoginActivity.this, sharedMap);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                // End - Call API

                progressBar.setVisibility(View.GONE);
            }
        });
    }
}