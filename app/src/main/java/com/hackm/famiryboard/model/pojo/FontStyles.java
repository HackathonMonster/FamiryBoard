package com.hackm.famiryboard.model.pojo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;

/**
 * Created by shunhosaka on 2015/01/09.
 */
public class FontStyles {
    public String text;
    public int size = 24;
    public String typefacePath = "Roboto-Black.ttf";
    public int style = Typeface.NORMAL;
    public int gravity = Gravity.LEFT;
    public int color = Color.BLACK;

    public FontStyles(String text) {
        this.text = text;
    }

    public FontStyles(String text, int size, String typefacePath, int style, int gravity, int color) {
        this.text = text;
        this.size = size;
        this.typefacePath = typefacePath;
        this.style = style;
        this.gravity = gravity;
        this.color = color;
    }
}
