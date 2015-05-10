package com.hackm.famiryboard.model.viewobject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.google.gson.JsonElement;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.widget.WhiteBoardView;

import org.json.JSONObject;


/**
 * Created by shunhosaka on 2014/12/27.
 */
public class DecoImage extends Deco {

    public String imageUrl;

    public DecoImage(Bitmap bitmap, float x, float y, float width, float height, int rotation, int type, String imageUrl, String boardId) {
        super(bitmap, x, y, width, height, rotation, type, boardId);
        this.imageUrl = imageUrl;
    }

    /**
     * No Create Bitmap
     * @param x
     * @param y
     * @param width
     * @param height
     * @param rotation
     * @param type
     * @param imageUrl
     */
    public DecoImage(float x, float y, float width, float height, int rotation, int type, String imageUrl, String boardId) {
        super(x, y, width, height, rotation, type, boardId);
        this.imageUrl = imageUrl;
    }
}
