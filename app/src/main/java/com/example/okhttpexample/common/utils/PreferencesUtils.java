package com.example.okhttpexample.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class PreferencesUtils {

    /**
     * Set Shared Preferences
     * @param context
     * @param map
     */
    public static void setPreferences(Context context, Map<String, String> map) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        for (Map.Entry<String,String> entry : map.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }

        editor.apply();
    }

    /**
     * Get Shared Preferences
     * @param context
     * @param key
     * @return
     */
    public static String getPreferences(Context context, String key) {
        return getPreferences(context, key, "");
    }

    /**
     * Get Shared Preferences
     * @param context
     * @param key
     * @return
     */
    public static String getPreferences(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }
}
