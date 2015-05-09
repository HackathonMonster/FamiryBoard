package com.hackm.famiryboard.model.viewobject;

import android.graphics.Bitmap;

import com.hackm.famiryboard.model.pojo.FontStyles;

/**
 * Created by shunhosaka on 2015/01/09.
 */
public class DecoText extends Deco {
    public FontStyles styles;

    public DecoText(Bitmap bitmap, float x, float y, float width, float height, int rotation, int decoType, FontStyles styles) {
        super(bitmap, x, y, width, height, rotation, decoType);
        this.styles = styles;
    }

    @Override
    public String encodeToSvg() {
        return null;
    }

    @Override
    public Deco decoveBySvg(String svgData) {
        return null;
    }
}

