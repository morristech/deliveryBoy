package com.yoscholar.deliveryboy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by agrim on 24/10/16.
 */

public class AppPreference {

    //USER DETAILS (details of the logged in person)
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CITY = "city";
    public static final String TOKEN = "token";

    public static void saveInt(Context context, String key, int value) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(key, -1);
    }

    public static void saveString(Context context, String key, String value) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(key, "null");
    }

    public static void saveBoolean(Context context, String key, boolean value) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(key, false);
    }

    public static void clearPreferencesLogout(Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(IS_LOGGED_IN);
        editor.remove(ID);
        editor.remove(NAME);
        editor.remove(CITY);
        editor.remove(TOKEN);
        editor.commit();

    }

}
