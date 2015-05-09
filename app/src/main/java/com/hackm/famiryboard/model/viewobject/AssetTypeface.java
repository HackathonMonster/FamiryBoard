package com.hackm.famiryboard.model.viewobject;

import android.graphics.Typeface;

/**
 * this class
 * Created by shunhosaka on 2015/01/09.
 */
public class AssetTypeface {
    public String assetPath;
    public Typeface typeface;

    /**
     *
     * @param assetPath
     * @param typeface
     */
    public AssetTypeface(String assetPath, Typeface typeface) {
        this.assetPath = assetPath;
        this.typeface = typeface;
    }
}
