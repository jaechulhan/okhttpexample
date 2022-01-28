package com.example.okhttpexample.network;

import android.content.Context;

import com.example.okhttpexample.network.interceptor.AuthTokenInterceptor;
import com.example.okhttpexample.network.interceptor.CacheInterceptor;
import com.example.okhttpexample.network.interceptor.ErrorInterceptor;
import com.example.okhttpexample.network.interceptor.ForceCacheInterceptor;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class NetworkManager {
    private static NetworkManager instance;
    private OkHttpClient client;

    /**
     * By using Singleton Pattern we can share cookie, client values.
     */
    private NetworkManager(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        builder.cookieJar(cookieJar);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.followRedirects(true);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.addNetworkInterceptor(httpLoggingInterceptor);

        // Custom Interceptor
        builder.addInterceptor(new ErrorInterceptor());
        builder.addInterceptor(new ForceCacheInterceptor(context));
        builder.addInterceptor(new AuthTokenInterceptor(context));
        builder.addNetworkInterceptor(new CacheInterceptor()); // only if not enabled from the server

        File cacheDir = new File(context.getCacheDir(), "network");

        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);

        builder.cache(cache);

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        client = builder.build();
    }

    /**
     * Get Instance of NetworkManager
     * @return
     */
    public static NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    /**
     * Get OkHttpClient
     * @return
     */
    public OkHttpClient getClient() {
        return client;
    }
}
