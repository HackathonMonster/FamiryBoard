package com.hackm.famiryboard.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 本用のRecyclerViewAdapter
 * Created by shunhosaka on 2015/02/27.
 */
public class AlbumAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;
     private List<String> mImages = new ArrayList<>();

    /**
     * コンストラクタ
     * @param context
     */
    public AlbumAdapter(Context context) {
        mContext = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateContent(List<String> images) {
        this.mImages = images;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(mContext);
        }
        Picasso.with(mContext).load((String) getItem(position)).into((ImageView) convertView);
        return convertView;
    }
}
