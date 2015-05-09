package com.hackm.famiryboard.view.activity;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.JSONRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.system.Account;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Login Activity
 * show onetime.
 * success login->MainActivity
 */
@NoTitle
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

    @ViewById(R.id.login_layout_progress)
    SwipeRefreshLayout mProgressLayout;

    @ViewById(R.id.login_edittext_mail)
    EditText mMailEditText;
    @ViewById(R.id.login_edittext_password)
    EditText mPasswordEditText;

    @AfterViews
    void onAfterViews() {
        // 通信時のProgress用のレイアウト
        // 通信時だけEnableをtruにする
        mProgressLayout.setEnabled(false);
        //TODO プログレスのカラー設定
        //mProgressLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //プログレスが動いてたら停止する。
        if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
    }

    /**
     * Start Login Task
     * Getting User Infomation
     */
    @Click(R.id.login_button_login)
    void clickLogin() {
        JSONRequestUtil loginRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                Account account = new Gson().fromJson(object.toString(), Account.class);
                if (account != null) {
                    //TODO ログイン
                    account.saveAccount(getApplicationContext());
                }
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                //TODO ログイン失敗のダイログ
            }
        },
        LoginActivity.class.getSimpleName(),
        new HashMap<String, String>());
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("password", mMailEditText.getText().toString());
            bodyParams.put("username", mPasswordEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
        loginRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.postLoginUrl(),
                NetworkTasks.PostLogin,
                bodyParams
        );
    }

}
