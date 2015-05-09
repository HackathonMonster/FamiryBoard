package com.hackm.famiryboard.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.ImageUtil;
import com.hackm.famiryboard.controller.util.OrderCakeRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.model.enumerate.DrawerMenu;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.pojo.Cake;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.activity.WebpageActivity_;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import com.android.volley.Request;

@EActivity(R.layout.activity_confilm)
public class ConfilmActivity extends ActionBarActivity {

    @Extra("preview_image")
    String mImageString;
    @Extra("cake_json")
    String mCakeJson;

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mActionBarToolbar;
    @ViewById(R.id.confilm_textview_price)
    TextView mPriceTextView;
    @ViewById(R.id.confilm_imageview_preview)
    ImageView mPreviewImageView;
    @ViewById(R.id.confilm_imageview_frame)
    ImageView mFrameImageView;
    @ViewById(R.id.confilm_rippleview_cart)
    RippleView mCartLayoutButton;

    @ViewById(R.id.confilm_swiperefreshlayout_progress)
    SwipeRefreshLayout mProgressLayout;

    private Bitmap mPreviewImage;
    private Cake mCake;
    private Context mContext;

    @AfterInject
    void onAfterInject() {
        if (mImageString != null) {
            mPreviewImage = ImageUtil.decodeImageBase64(mImageString);
        } else {
            SharedPreferences pref = getSharedPreferences(AppConfig.PREF_NAME, MODE_PRIVATE);
            mImageString = pref.getString(AppConfig.PREF_SAVED_IMAGE, "");
            if (mImageString != "") {
                mPreviewImage = ImageUtil.decodeImageBase64(mImageString);
            }
        }
        if (mCakeJson != null) {
            mCake = new Gson().fromJson(mCakeJson, Cake.class);
        }
    }

    @AfterViews
    void onAfterViews() {
        mContext = this;
        setActionBarToolbar();
        if (mCake != null) {
            mPriceTextView.setText(getString(R.string.confilm_textview_price, mCake.price));
            //Set image frame cake type. 0->plain 1->choco
            mFrameImageView.setImageResource(mCake.type%2 == 1 ? R.drawable.img_cake_frame : R.drawable.img_cake_frame);
        }
        if (mPreviewImage != null) {
            mPreviewImageView.setImageBitmap(mPreviewImage);
        }
        //プログレスレイアウトの設定
        mProgressLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        //ジェスチャを無効にする
        mProgressLayout.setEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //プログレスが動いてたら停止する。
        if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
    }

    @Click(R.id.confilm_rippleview_cart)
    void clickCart() {
        mCartLayoutButton.setEnabled(false);
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
        cakeOrderRequest();
    }

    private Toolbar setActionBarToolbar() {
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitle("");
            setSupportActionBar(mActionBarToolbar);
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_toolbar_close);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        return mActionBarToolbar;
    }

    private void cakeOrderRequest() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cakeType", Integer.toString(mCake.type)));
        params.add(new BasicNameValuePair("image", mImageString));

        OrderCakeRequestUtil cakeOrderRequest = new OrderCakeRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                //通信に失敗したときの処理
                Toast.makeText(getApplicationContext(), getString(R.string.failed_network_text), Toast.LENGTH_LONG).show();
                mCartLayoutButton.setEnabled(true);
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
            }
        });
        cakeOrderRequest.onRequest(getApplicationContext(), UriUtil.orderCakeUri(), NetworkTasks.PostOrderCake, params);
    }



}
