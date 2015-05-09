package com.hackm.famiryboard.model.viewobject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.widget.WhiteBoardView;


/**
 * Created by shunhosaka on 2014/12/27.
 */
public class DecoImage extends Deco {

    public String imageUrl;

    public DecoImage(Bitmap bitmap, float x, float y, float width, float height, int rotation, int type, String imageUrl) {
        super(bitmap, x, y, width, height, rotation, type);
        this.imageUrl = imageUrl;
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
