package com.hackm.famiryboard.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackm.famiryboard.model.viewobject.AssetTypeface;

import java.util.List;

/**
 * Created by shunhosaka on 2015/01/19.
 */
public class TypefaceAdapter extends ArrayAdapter<AssetTypeface> {

    public TypefaceAdapter(Context context, int resource, List<AssetTypeface> contents) {
        super(context, resource, contents);
    }

    @Override
    public int getPosition(AssetTypeface item) {
        for (int i = 0; i < getCount(); i++) {
            AssetTypeface type = getItem(i);
            if (type.assetPath.equals(item.assetPath)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        AssetTypeface item = (AssetTypeface) getItem(position);
        int nameIndex = item.assetPath.indexOf(".");
        textView.setText(item.assetPath.substring(0, nameIndex));
        textView.setTypeface(item.typeface);
        return textView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        AssetTypeface item = getItem(position);
        int nameIndex = item.assetPath.indexOf(".");
        textView.setText(item.assetPath.substring(0, nameIndex));
        textView.setTypeface(item.typeface);
        return textView;
    }

}
