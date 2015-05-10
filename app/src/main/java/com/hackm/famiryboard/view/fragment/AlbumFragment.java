package com.hackm.famiryboard.view.fragment;

import android.support.v4.app.Fragment;
import android.widget.GridView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.JSONArrayRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.pojo.Album;
import com.hackm.famiryboard.view.adapter.AlbumAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EFragment(R.layout.activity_album)
public class AlbumFragment extends Fragment {

    @ViewById(R.id.album_gridview)
    GridView mGridView;
    AlbumAdapter mAdapter;

    @AfterViews
    public void onAfterViews() {
        mAdapter = new AlbumAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        JSONArrayRequestUtil requestUtil = new JSONArrayRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                JSONArray array = (JSONArray) object;
                List<String> images = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    Album album = null;
                    try {
                        album = gson.fromJson(array.getJSONObject(i).toString(), Album.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    images.add(album.Url);
                }
                mAdapter.updateContent(images);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {

            }
        },
                AlbumFragment.class.getSimpleName(),
                new HashMap<String, String>());
        requestUtil.onRequest(VolleyHelper.getRequestQueue(getActivity()), Request.Priority.HIGH, UriUtil.getImagesUrl(), NetworkTasks.GetAlbums);
    }

}
