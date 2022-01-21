package com.example.okhttpexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.okhttpexample.network.NetworkManager;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
                .url("http://dummy.restapiexample.com/api/v1/employees")
                .build();

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

        //new HttpAsyncTask(this).execute("http://dummy.restapiexample.com/api/v1/employees");
    }

    private static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private static OkHttpClient client;

        private final Context mContext;

        public HttpAsyncTask(final Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            String url = strings[0];

            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));

            if (client == null) {
                client = new OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .build();
            }

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();

                // JSON Parser with GSON - JSON to Map Class
                Gson gson = new Gson();
                Map map = gson.fromJson(result, Map.class);

                // Logging
                Log.d(TAG, "doInBackground: " + map.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.d(TAG, "onPostExecute: " + s);
            }
        }

    }
}