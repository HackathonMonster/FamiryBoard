package com.hackm.famiryboard.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackm.famiryboard.model.enumerate.DrawerMenu;
import com.hackm.famiryboard.R;

/**
 * Created by shunhosaka on 2014/12/04.
 */
public class DrawerMenuAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public DrawerMenuAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DrawerMenu.values().length;
    }

    @Override
    public DrawerMenu getItem(int position) {
        return DrawerMenu.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_drawer_menu, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_drawer_menu_imageview);
            holder.textView = (TextView) convertView.findViewById(R.id.item_drawer_menu_textview);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        DrawerMenu content = getItem(position);
        holder.imageView.setImageResource(content.icon);
        holder.textView.setText(content.text);
        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
