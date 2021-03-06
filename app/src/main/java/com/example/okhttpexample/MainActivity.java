package com.example.okhttpexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.okhttpexample.network.NetworkManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView tvMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        OkHttpClient client = NetworkManager.getInstance(this).getClient();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_URL + "/rest/v1/user/info?username=admin")
                .build();

        Log.d(TAG, "BASE_URL: " + BuildConfig.BASE_URL);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                tvMessage.setText("There is an error while calling an API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Gson gson = new Gson();
                    Map map = gson.fromJson(result, Map.class);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onResponse: " + map.toString());
                            tvMessage.setText(map.toString());
                        }
                    });
                }
            }
        });
    }
}