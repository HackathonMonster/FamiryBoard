package com.hackm.famiryboard.view.activity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hackm.famiryboard.model.pojo.Cake;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.adapter.CakeAdapter;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.activity.WhiteBoardActivity_;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_select_board)
public class SelectBoardActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mToolbar;

    @ViewById(R.id.select_board_listview)
    ListView mContentListView;

    private CakeAdapter mAdapter;

    @AfterViews
    void onAfterViews() {
        setAdapter();
        mContentListView.setAdapter(mAdapter);
        mContentListView.setOnItemClickListener(this);

        mToolbar.setTitle(R.string.title_activity_select_cake);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
    }

    private void setAdapter() {
        List<Cake> cakeList = new ArrayList<Cake>();
        // JSON データをオブジェクトに変換
        String json = assetReader("json/select_cake.json");
        if(json != null) {
            try {
                JSONArray cakeArray = new JSONObject(json).getJSONArray("data");
                for (int i = 0; i < cakeArray.length(); i++) {
                    Cake cake = new Gson().fromJson(cakeArray.getJSONObject(i).toString(), Cake.class);
                    cakeList.add(cake);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter = new CakeAdapter(getApplicationContext(), cakeList);
    }

    private String assetReader(String path) {
        AssetManager as = getResources().getAssets();
        try {
            InputStream iStream = as.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream,"UTF-8"));
            String _line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((_line = reader.readLine()) != null) {
                stringBuilder.append(_line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cake cake = mAdapter.getItem(position);
        WhiteBoardActivity_.intent(this).mBoardId(AppConfig.BOARD_ID).start();
        finish();
    }
}
