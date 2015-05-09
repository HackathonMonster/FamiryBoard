package com.hackm.famiryboard.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.model.pojo.Cake;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 2014/12/04.
 */
public class CakeAdapter extends BaseAdapter {

    private List<Cake> mContent = new ArrayList<Cake>();
    private LayoutInflater mInflater;
    private Context mContext;

    public CakeAdapter(Context context, List<Cake> content) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mContent = content;
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public Cake getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_cake, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.cake_imageview_icon);
            holder.labelTextView = (TextView) convertView.findViewById(R.id.cake_textview_type);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.cake_textview_name);
            holder.typeNameTextView = (TextView) convertView.findViewById(R.id.cake_textview_type_name);
            holder.sizeTextView = (TextView) convertView.findViewById(R.id.cake_textview_size);
            holder.targetTextView = (TextView) convertView.findViewById(R.id.cake_textview_target);
            holder.pliceTextView = (TextView) convertView.findViewById(R.id.cake_textview_plice);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Cake content = getItem(position);
        PicassoHelper.with(mContext).load(content.image_path).into(holder.iconImageView);
        holder.nameTextView.setText(content.name);
        holder.labelTextView.setText(content.type % 2 == 1 ? mContext.getString(R.string.cake_label_plain) : mContext.getString(R.string.cake_label_choco));
        holder.labelTextView.setBackgroundResource(content.type % 2 == 1 ? R.drawable.bg_cake_plain : R.drawable.bg_cake_choco);
        holder.typeNameTextView.setText(content.type_name);
        holder.sizeTextView.setText(mContext.getString(R.string.cake_size, content.width, content.height));
        holder.targetTextView.setText(mContext.getString(R.string.cake_target, content.target));
        holder.pliceTextView.setText(mContext.getString(R.string.cake_price, content.price));
        convertView.setBackgroundColor(content.type % 2 == 1 ?
                mContext.getResources().getColor(R.color.select_cake_plain_back) :
                mContext.getResources().getColor(R.color.select_cake_choco_back));
        return convertView;
    }

    private class ViewHolder{
        ImageView iconImageView;
        TextView labelTextView;
        TextView nameTextView;
        TextView typeNameTextView;
        TextView sizeTextView;
        TextView targetTextView;
        TextView pliceTextView;
    }

}
