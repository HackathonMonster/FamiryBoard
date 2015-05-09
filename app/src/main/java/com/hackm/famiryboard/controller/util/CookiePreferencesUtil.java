package com.hackm.famiryboard.controller.util;

import android.content.Context;

import com.hackm.famiryboard.model.system.AppConfig;

/**
 * Created by shunhosaka on 2015/01/30.
 */
public class CookiePreferencesUtil {

    public static void setCookie(Context context, String cookie) {
        if (context != null) {
            context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(AppConfig.PREF_SAVED_COOKIE, cookie)
                    .commit();
        }
    }

    public static String getCookie(Context context) {
        if (context != null) {
            return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE)
                    .getString(AppConfig.PREF_SAVED_COOKIE, "");
        }
        return "";
    }

}
