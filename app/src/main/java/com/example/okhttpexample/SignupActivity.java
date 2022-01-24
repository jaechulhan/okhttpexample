package com.example.okhttpexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText tiFullname, tiUsername, tiPassword, tiEmail;
    Button btnSignup;
    TextView tvLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tiFullname = findViewById(R.id.tiFullname);
        tiUsername = findViewById(R.id.tiUsername);
        tiPassword = findViewById(R.id.tiPassword);
        tiEmail = findViewById(R.id.tiEmail);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progress);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
            }
        });
    }
}