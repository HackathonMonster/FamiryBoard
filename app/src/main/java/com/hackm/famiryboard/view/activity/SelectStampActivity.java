package com.hackm.famiryboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.JSONRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.pojo.Stamp;
import com.hackm.famiryboard.model.system.AppConfig;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_select_stamp)
public class SelectStampActivity extends ActionBarActivity implements View.OnClickListener {

    @Extra("category_id")
    String mCategoryId;
    @ViewById(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @ViewById(R.id.select_stamp_layout_container)
    LinearLayout mContainerLayout;

    private List<Stamp> mStampList = new ArrayList<Stamp>();
    private LayoutInflater mInflater;


    @AfterInject
    void onAfterInject() {
        if(mCategoryId == null || mCategoryId.length() < 1) {
            finish();
        }
    }

    @AfterViews
    void onAfterViews() {
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mToolbar.setTitle(R.string.title_activity_select_stamp);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        onRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PicassoHelper.with(this).cancelTag(SelectStampActivity.class.getSimpleName());

    }

    private void onRequest() {
        JSONRequestUtil stampCategoryRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                //初期化
                if(mStampList == null || mStampList.size() > 0 ) {
                    mStampList = new ArrayList<Stamp>();
                }
                try {
                    JSONArray stampsArray = ((JSONObject) object).getJSONArray("stamps");
                    for (int i = 0; i < stampsArray.length(); i++) {
                        Stamp stamp = new Gson().fromJson(stampsArray.getJSONObject(i).toString(), Stamp.class);
                        mStampList.add(stamp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(AppConfig.DEBUG) {
                    Log.d("SelectStampActivity", "StampListLength:" + Integer.toString(mStampList.size()));
                }
                updateStampList();
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                //通信に失敗したときの処理
                Toast.makeText(getApplicationContext(), getString(R.string.failed_network_text), Toast.LENGTH_LONG).show();
            }
        },
                getClass().getSimpleName(),
                null);
        stampCategoryRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.getStampUri(mCategoryId),
                NetworkTasks.GetStamp);
    }

    private void updateStampList() {
        mContainerLayout.removeAllViews();
        if(mStampList == null) return;
        View lastLayout = null;
        for (int i = 0; i < mStampList.size(); i++) {
            if (i % 3 == 0) {
                lastLayout = mInflater.inflate(R.layout.item_stamps, null);
                lastLayout.setBackgroundColor((int) (i / 3) % 2 == 0 ?
                        getResources().getColor(R.color.select_cake_plain_back) :
                        getResources().getColor(R.color.select_cake_choco_back));
                mContainerLayout.addView(lastLayout);
            }
            if(lastLayout == null) continue;
            final ImageButton targetImageButton;
            switch (i % 3) {
                case 0:
                    targetImageButton = (ImageButton) lastLayout.findViewById(R.id.stamps_imagebutton_thumb1);
                    break;
                case 1:
                    targetImageButton = (ImageButton) lastLayout.findViewById(R.id.stamps_imagebutton_thumb2);
                    break;
                case 2:
                    targetImageButton = (ImageButton) lastLayout.findViewById(R.id.stamps_imagebutton_thumb3);
                    break;
                default:
                    continue;
            }
            if(AppConfig.DEBUG) {
                Log.d("SelectStampActivity", "StampThumUrl:" + mStampList.get(i).stamp_thumb_url);
            }
            PicassoHelper.with(this).load(mStampList.get(i).stamp_thumb_url).tag(SelectStampActivity.class.getSimpleName()).into(targetImageButton);
            targetImageButton.setTag(mStampList.get(i));
            targetImageButton.setVisibility(View.VISIBLE);
            targetImageButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stamps_imagebutton_thumb1:
            case R.id.stamps_imagebutton_thumb2:
            case R.id.stamps_imagebutton_thumb3:
                if (view.getTag() == null) return;
                Stamp stamp = (Stamp) view.getTag();
                Intent data = new Intent();
                data.putExtra("stamp", new Gson().toJson(stamp, Stamp.class));
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }
}
