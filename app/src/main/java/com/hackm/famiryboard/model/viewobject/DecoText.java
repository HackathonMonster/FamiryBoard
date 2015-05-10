package com.hackm.famiryboard.model.viewobject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hackm.famiryboard.model.pojo.FontStyles;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;

/**
 * Created by shunhosaka on 2015/01/09.
 */
public class DecoText extends Deco {

    public FontStyles styles;

    public DecoText(Bitmap bitmap, float x, float y, float width, float height, int rotation, int decoType, String boardId, FontStyles styles) {
        super(bitmap, x, y, width, height, rotation, decoType, boardId);
        this.styles = styles;
    }

    public DecoText(float x, float y, float width, float height, int rotation, int type, String boardId, FontStyles styles) {
        super(x, y, width, height, rotation, type, boardId);
        this.bitmap = createBitmap(width, height, styles);
        this.styles = styles;
    }

    public DecoText(Deco deco, FontStyles styles) {
        super(deco.bitmap, deco.x, deco.y, deco.width, deco.height, deco.rotation, deco.type, deco.boardId);
        if (this.bitmap == null) {
            this.bitmap = createBitmap(width, height, styles);
        }
        this.styles = styles;
    }

    public static Bitmap createBitmap(float width, float height, FontStyles fontStyles) {
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(fontStyles.color);
        paint.setTextSize(fontStyles.size);
        switch (fontStyles.gravity) {
            case Gravity.LEFT:
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case Gravity.CENTER:
                paint.setTextAlign(Paint.Align.CENTER);
                break;
            case Gravity.RIGHT:
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
        }
        canvas.drawText(fontStyles.text, 1, 1, paint);
        return bitmap;
    }

    /**
     * Encoding to Json
     *
     * @return
     */
    public String toJson() {
        String jsonStr = new Gson().toJson(this);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            jsonObject.put("text", styles.text);
            jsonObject.put("size", styles.size);
            jsonObject.put("typefacePath", styles.typefacePath);
            jsonObject.put("style", styles.style);
            jsonObject.put("gravity", styles.gravity);
            jsonObject.put("color", styles.color);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static DecoText parseDecoText(JSONObject jsonObject) {
        if (!jsonObject.has("text")) return null;
        FontStyles fontStyles = null;
        try {
            fontStyles = new FontStyles(jsonObject.getString("text"));
            fontStyles.typefacePath = jsonObject.getString("typefacePath");
            fontStyles.size = jsonObject.getInt("size");
            fontStyles.style = jsonObject.getInt("style");
            fontStyles.gravity = jsonObject.getInt("gravity");
            fontStyles.color = jsonObject.getInt("color");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Deco deco = new Gson().fromJson(jsonObject.toString(), Deco.class);
        DecoText decoText = (DecoText) deco;
        decoText.styles = fontStyles;
        decoText.bitmap = createBitmap(deco.width, deco.height, decoText.styles);
        return decoText;
    }
}

