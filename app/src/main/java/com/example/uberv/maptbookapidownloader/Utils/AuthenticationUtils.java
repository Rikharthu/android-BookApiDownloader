package com.example.uberv.maptbookapidownloader.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

public class AuthenticationUtils {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    private static SharedPreferences sPreferences;

    /**
     * Initialize shared preferences
     */
    public static void initSharedPref(Context context) {
        sPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getAuthPreferences() {
        return sPreferences;
    }

    @Nullable
    public static String get(String key) {
        return sPreferences.getString(key, null);
    }

    private static SharedPreferences.Editor editAuthPreferences() {
        return getAuthPreferences().edit();
    }

    @Nullable
    public static String getAccessToken() {
        return getAuthPreferences().getString(ACCESS_TOKEN, null);
    }

    @Nullable
    public static String getRefreshToken() {
        return getAuthPreferences().getString(REFRESH_TOKEN, null);
    }

    /**
     * Check if we have a token
     */
    public static boolean isAuthorized() {
        return TextUtils.isEmpty(getAccessToken());
    }

    public static void deAuthorize() {
        editAuthPreferences()
                .remove(ACCESS_TOKEN)
                .commit();
    }

    public static void authorize(String accessToken, String refreshToken) {
        editAuthPreferences()
                .putString(ACCESS_TOKEN, accessToken)
                .putString(REFRESH_TOKEN, refreshToken)
                .commit();
    }

    public static void setCredentials(String email, String password) {
        editAuthPreferences().putString("email", email).putString("password", password).commit();
    }

    public static Pair<String, String> getCredentials() {
        return new Pair<>(getAuthPreferences().getString("email", null), getAuthPreferences().getString("password", null));
    }
}
