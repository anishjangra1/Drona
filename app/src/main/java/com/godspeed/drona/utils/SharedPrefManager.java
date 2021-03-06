package com.godspeed.drona.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPreferences mSharedPref;
    public static final String WEB_TOKEN = "WEB_TOKEN";
    public static final String SCREEN_NAME = "SCREEN_NAME";
    public static final String SIGN_UP_SCREEN = "SIGN_UP_SCREEN";
    public static final String FORGOT_SCREEN = "FORGOT_SCREEN";
    public static final String LOGIN_SCREEN = "LOGIN_SCREEN";
    public static final String WEB_TOKEN_ID = "WEB_TOKEN_ID";
    public static final String TOKEN = "TOKEN";
    public static final String IMEI = "IMEI";
    public static final String LOCAL_USER_ID = "LOCAL_USER_ID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_PASS = "USER_PASS";
    public static final String LOCAL_USER_PASS = "LOCAL_USER_PASS";
    public static final String IS_LOGINED = "IS_LOGINED";

    public static final String IS_LOGIN = "IS_LOGIN";


    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}