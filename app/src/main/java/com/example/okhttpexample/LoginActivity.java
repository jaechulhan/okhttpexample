package com.example.okhttpexample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.okhttpexample.common.constants.AppConstants;
import com.example.okhttpexample.common.utils.PreferencesUtils;
import com.example.okhttpexample.network.NetworkManager;
import com.example.okhttpexample.network.NetworkResponseListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

        // #1. Clear Shared Reference
        clearSharedReferences();

        tiUsername = findViewById(R.id.tiUsername);
        tiPassword = findViewById(R.id.tiPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        progressBar = findViewById(R.id.progress);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                this.checkRequiredField();

                if (!TextUtils.isEmpty(tiUsername.getText()) && !TextUtils.isEmpty(tiPassword.getText())) {
                    // #1. Generate request message with Json
                    Map<String, String> reqBodyMap = new LinkedHashMap<>();
                    reqBodyMap.put("username", tiUsername.getText().toString());
                    reqBodyMap.put("password", tiPassword.getText().toString());

                    // #2. Call API
                    NetworkManager.sendToServer(LoginActivity.this, "/rest/v1/auth", reqBodyMap, new NetworkResponseListener() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, "Login Error");
                            // #3. Close progressbar
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onSuccess(Map<String, Object> resMap) {
                            if (resMap.get("error") != null) {
                                Log.e(TAG, "Login Error");
                            } else {
                                // #1. Save Access Token / Refresh Token
                                Map<String, String> sharedMap = new HashMap<String, String>();
                                sharedMap.put(AppConstants.ACCESS_TOKEN_KEY, (String) resMap.get(AppConstants.ACCESS_TOKEN_KEY));
                                sharedMap.put(AppConstants.REFRESH_TOKEN_KEY, (String) resMap.get(AppConstants.REFRESH_TOKEN_KEY));
                                PreferencesUtils.setPreferences(LoginActivity.this, sharedMap);
                                // #2. Move to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            // #3. Close progressbar
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }

            }

            /**
             * Check Required Fields
             */
            private void checkRequiredField() {
                if (TextUtils.isEmpty(tiUsername.getText())) {
                    tiUsername.setError(getResources().getString(R.string.required_field_error, "Username"));
                } else {
                    tiUsername.setError(null);
                }

                if (TextUtils.isEmpty(tiPassword.getText())) {
                    tiPassword.setError(getResources().getString(R.string.required_field_error, "Password"));
                } else {
                    tiPassword.setError(null);
                }
            }
        });
    }

    /**
     * Clear Shared References
     * Access Token & Refresh Token
     */
    private void clearSharedReferences() {
        Map<String, String> sharedMap = new HashMap<String, String>();
        sharedMap.put(AppConstants.ACCESS_TOKEN_KEY, "");
        sharedMap.put(AppConstants.REFRESH_TOKEN_KEY, "");
        PreferencesUtils.setPreferences(LoginActivity.this, sharedMap);
    }
}