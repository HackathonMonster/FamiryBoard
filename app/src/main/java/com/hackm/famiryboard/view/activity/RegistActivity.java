package com.hackm.famiryboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
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
@EActivity(R.layout.activity_regist)
public class RegistActivity extends Activity {

    @ViewById(R.id.regist_layout_progress)
    SwipeRefreshLayout mProgressLayout;

    @ViewById(R.id.regist_edittext_name)
    EditText mNameEditText;
    @ViewById(R.id.regist_edittext_mail)
    EditText mMailEditText;
    @ViewById(R.id.regist_edittext_password)
    EditText mPasswordEditText;
    @ViewById(R.id.regist_edittext_password_confilm)
    EditText mPasswordConfilmEditText;
    @ViewById(R.id.regist_edittext_birthday)
    EditText mBirthDayEditText;

    private Context mContext;

    @AfterViews
    void onAfterViews() {
        mContext = this;
        // 通信時のProgress用のレイアウト
        // 通信時だけEnableをtruにする
        mProgressLayout.setEnabled(false);
        mProgressLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
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
    @Click(R.id.regist_button_signup)
    void clickSignUp() {
        JSONRequestUtil loginRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                Account account = new Account();
                account.userName = mNameEditText.getText().toString();
                account.email = mMailEditText.getText().toString();
                account.password = mPasswordEditText.getText().toString();
                account.birthday = mBirthDayEditText.getText().toString();
                account.saveAccount(getApplicationContext());
                postLogin();
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                //TODO ログイン失敗のダイログ
                MainActivity_.intent(mContext).start();
            }
        },
                RegistActivity.class.getSimpleName(),
                new HashMap<String, String>());
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("name", mNameEditText.getText().toString());
            bodyParams.put("email", mMailEditText.getText().toString());
            bodyParams.put("password", mPasswordEditText.getText().toString());
            bodyParams.put("confirmPassword", mPasswordConfilmEditText.getText().toString());
            bodyParams.put("birthday", mBirthDayEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
        Log.d("RegistRequest", bodyParams.toString());
        loginRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.postRegistUrl(),
                NetworkTasks.PostRegistFamiry,
                bodyParams
        );
    }


    void postLogin() {
        JSONRequestUtil loginRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                JSONObject jsonObject = (JSONObject) object;
                try {
                    Account account = Account.getAccount(getApplicationContext());
                    account.access_token = jsonObject.getString("access_token");
                    account.token_type = jsonObject.getString("token_type");
                    account.expires_in = jsonObject.getString("expires_in");
                    account.saveAccount(getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                MainActivity_.intent(mContext).start();
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                //TODO ログイン失敗のダイログ
                MainActivity_.intent(mContext).start();
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
