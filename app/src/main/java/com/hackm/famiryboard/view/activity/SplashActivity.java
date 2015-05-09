package com.hackm.famiryboard.view.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.JSONRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.system.Account;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


@EActivity(R.layout.activity_splash)
public class SplashActivity extends ActionBarActivity {

    private android.os.Handler mHandler = new android.os.Handler();
    private Context mContext;

    @AfterViews
    void onAfterViews() {
        mContext = this;
        if (Account.isAccount(getApplicationContext())) {
            onGetTokenRequest();
        } else {
            StartActivity_.intent(mContext).start();
            finish();
        }
    }

    private void onGetTokenRequest() {
        Account account = Account.getAccount(getApplicationContext());
        JSONRequestUtil loginRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                Account account = new Gson().fromJson(object.toString(), Account.class);
                if (account != null) {
                    Account.updateToken(account.access_token, getApplicationContext());
                }
                intentMain();
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                intentMain();
            }
        },
                LoginActivity.class.getSimpleName(),
                new HashMap<String, String>());
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("username", account.userName);
            bodyParams.put("password", account.password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        loginRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.postLoginUrl(),
                NetworkTasks.PostLogin,
                bodyParams
        );
    }

    private void intentMain() {
        // タイマーのセット
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity_.intent(mContext).start();
                        finish();
                    }
                });
            }
        }, 500);
    }
}
