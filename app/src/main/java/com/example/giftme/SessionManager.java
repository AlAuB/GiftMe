package com.example.giftme;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SessionManager {

    static final String USER_EMAIL = "userEmail";
    static final String USER_STATUS = "userStatus";
    static final String USER_NAME = "userName";
    static final String USER_PFP = "userPFP";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setSession(Context context, String email, String name, String picURL) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_EMAIL, email);
        editor.putBoolean(USER_STATUS, true);
        editor.putString(USER_NAME, name);
        editor.putString(USER_PFP, picURL);
        editor.apply();
    }

    //GETTERS
    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString(USER_EMAIL, "");
    }
    public static boolean getUserStatus(Context context) {
        return getSharedPreferences(context).getBoolean(USER_STATUS, false);
    }
    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(USER_NAME, "");
    }

    public static String getUserPFP(Context context) {
        return getSharedPreferences(context).getString(USER_PFP, "");
    }

    public static void clearSession(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(USER_EMAIL);
        editor.remove(USER_STATUS);
        editor.remove(USER_NAME);
        editor.remove(USER_PFP);
        editor.apply();
    }
}
