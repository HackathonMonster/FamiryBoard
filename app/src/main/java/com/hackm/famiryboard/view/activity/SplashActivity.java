package com.hackm.famiryboard.view.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import com.android.volley.Request;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.JSONRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.activity.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


@EActivity(R.layout.activity_splash)
public class SplashActivity extends ActionBarActivity {

    private android.os.Handler mHandler = new android.os.Handler();
    private Context mContext;

    private String irregularShippingBegin = null;
    private String irregularShippingEnd = null;

    @AfterViews
    void onAfterViews() {
        mContext = this;
        onDeliveryDayRequest();
    }

    private void onDeliveryDayRequest() {
        JSONRequestUtil deliveryDayRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                try {
                    irregularShippingBegin = ((JSONObject) object).getString("irregular_shipping_begin");
                    irregularShippingEnd = ((JSONObject) object).getString("irregular_shipping_end");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // タイマーのセット
                Timer timer = new Timer(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity_.intent(mContext).mIrregularShippingBegin(irregularShippingBegin).mIrregularShippingEnd(irregularShippingEnd).start();
                                finish();
                            }
                        });
                    }
                }, 500); // 0.3
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                // タイマーのセット
                Timer timer = new Timer(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity_.intent(mContext).mIrregularShippingBegin(irregularShippingBegin).start();
                                finish();
                            }
                        });
                    }
                }, 500); // 0.3
            }
        },
        getClass().getSimpleName(),
        null);
        deliveryDayRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.LOW,
                UriUtil.getDeliveryDayUri(),
                NetworkTasks.GetDeliveryDay);
    }

}
