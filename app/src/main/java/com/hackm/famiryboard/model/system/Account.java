package com.hackm.famiryboard.model.system;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hackm.famiryboard.controller.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shunhosaka on 15/05/10.
 */
public class Account {
    private static final String PREF_KEY_JSON = "pref_key_json";
    //ログインしているがどうかをチェックする
    private static final String PREF_KEY_ISLOGIN = "pref_key_login";
    //Account Type
    public int type = 0;
    public String access_token;
    public String token_type;
    public String expires_in;
    public String userName;

    /**
     * Load Data from Preferences
     * @param context
     */
    public static Account getAccount(Context context) {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getPreferences(context, SharedPreferencesUtil.PrefKey.Account);
        String json = sharedPreferences.getString(PREF_KEY_JSON, "");
        if (!json.isEmpty()) {
            return new Gson().fromJson(json, Account.class);
        }
        return null;
    }

    //check have to account
    public static boolean isAccount(Context context){
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getPreferences(context, SharedPreferencesUtil.PrefKey.Account);
        return sharedPreferences.getBoolean(PREF_KEY_ISLOGIN, false);
    }

    /**
     * @param type
     * @param access_token
     * @param token_type
     * @param expires_in
     * @param userName
     */
    public Account(int type, String access_token, String token_type, String expires_in, String userName) {
        this.type = type;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.userName = userName;
    }

    /**
     * Save Account Data
     * @param context
     */
    public boolean saveAccount(Context context) {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getPreferences(context, SharedPreferencesUtil.PrefKey.Account);
        sharedPreferences.edit().putString(PREF_KEY_JSON, new Gson().toJson(this)).commit();
        if (token_type!=null && access_token != null) {
            sharedPreferences.edit().putBoolean(PREF_KEY_ISLOGIN, true).commit();
            return true;
        }
        return false;
    }

    public void logoutAccount(Context context) {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getPreferences(context, SharedPreferencesUtil.PrefKey.Account);
        sharedPreferences.edit().clear().commit();
    }

    public Map<String, String> getAccountHeader() {
        Map<String, String> authorizationHeader = new HashMap<>();
        authorizationHeader.put("Authorization", token_type + access_token);
        return authorizationHeader;
    }
}
