package com.hackm.famiryboard.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.pojo.StampCategory;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.controller.util.JSONRequestUtil;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.activity.SelectStampActivity_;
import com.hackm.famiryboard.view.adapter.StampCategoryAdapter;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_select_stamp_category)
public class SelectStampCategoryActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @ViewById(R.id.stamp_category_listview)
    ListView mListView;
    StampCategoryAdapter mAdapter;

    @AfterViews
    void onAfterViews() {
        setAdapter();

        mToolbar.setTitle(R.string.title_activity_select_stamp_category);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        onStampCategoryRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PicassoHelper.with(this).cancelTag(SelectStampCategoryActivity.class.getSimpleName());
    }

    private void setAdapter() {
        mAdapter = new StampCategoryAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void onStampCategoryRequest() {
        JSONRequestUtil stampCategoryRequest = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                List<StampCategory> stampCategorys = new ArrayList<StampCategory>();
                //Adapterの更新
                try {
                    JSONArray categoryArray = ((JSONObject) object).getJSONArray("categories");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        StampCategory category = new Gson().fromJson(categoryArray.getJSONObject(i).toString(), StampCategory.class);
                        stampCategorys.add(category);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.updateContents(stampCategorys);
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
                UriUtil.getStampCategoryUri(),
                NetworkTasks.GetStampCategory);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StampCategory stampCategory = mAdapter.getItem(position);
        if (stampCategory == null) return;
        SelectStampActivity_.intent(this).mCategoryId(stampCategory.category_id).startForResult(AppConfig.ID_ACTIVITY_SELECT_STAMP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConfig.ID_ACTIVITY_SELECT_STAMP:
                    //送られてきたデータをそのまま挿入する
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
    }
}
