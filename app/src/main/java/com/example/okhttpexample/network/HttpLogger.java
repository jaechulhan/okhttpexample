package com.example.okhttpexample.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.okhttpexample.MainActivity;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger  {
    private static final String TAG = HttpLogger.class.getSimpleName();

    @Override
    public void log(@NonNull String message) {
        Log.d(TAG, message);
    }
}
