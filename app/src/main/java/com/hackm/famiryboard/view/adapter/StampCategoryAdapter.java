package com.hackm.famiryboard.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.model.pojo.StampCategory;
import com.hackm.famiryboard.view.activity.SelectStampCategoryActivity;
import com.hackm.famiryboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 2014/12/04.
 */
public class StampCategoryAdapter extends BaseAdapter {

    private List<StampCategory> mContents = new ArrayList<StampCategory>();
    private LayoutInflater mInflater;
    private Context mContext;

    public StampCategoryAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mContents.size();
    }

    @Override
    public StampCategory getItem(int position) {
        return mContents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_stamp_category, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.stamp_category_textview);
            holder.thumbImage1 = (ImageView) convertView.findViewById(R.id.stamp_category_imageview_1);
            holder.thumbImage2 = (ImageView) convertView.findViewById(R.id.stamp_category_imageview_2);
            holder.thumbImage3 = (ImageView) convertView.findViewById(R.id.stamp_category_imageview_3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        StampCategory content = getItem(position);
        holder.textView.setText(content.category_name_en);
        holder.textView.setBackgroundResource(position%2==0?
                R.drawable.bg_cake_plain:
                R.drawable.bg_cake_choco);
        PicassoHelper.with(mContext).load(content.category_thumb1).tag(SelectStampCategoryActivity.class.getSimpleName()).into(holder.thumbImage1);
        PicassoHelper.with(mContext).load(content.category_thumb2).tag(SelectStampCategoryActivity.class.getSimpleName()).into(holder.thumbImage2);
        PicassoHelper.with(mContext).load(content.category_thumb3).tag(SelectStampCategoryActivity.class.getSimpleName()).into(holder.thumbImage3);
        convertView.setBackgroundColor(position%2==0?
                mContext.getResources().getColor(R.color.select_cake_plain_back):
                mContext.getResources().getColor(R.color.select_cake_choco_back));
        return convertView;
    }

    public void updateContents(List<StampCategory> contents) {
        mContents = contents;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView textView;
        ImageView thumbImage1;
        ImageView thumbImage2;
        ImageView thumbImage3;
    }

}
