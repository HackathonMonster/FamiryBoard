package com.hackm.famiryboard.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hackm.famiryboard.R;

/**
 * Created by shunhosaka on 2015/01/08.
 */
public class ColorSelectorView extends View {

    private OnColorChangeListener mChangeListener;
    private int[] mFontColors;
    private Bitmap mSelectorImage;
    private int mSelect = 0;
    private boolean mIsSelectTouch = false;

    public ColorSelectorView(Context context) {
        super(context);
        initialize();
    }

    public ColorSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        TypedArray array_color = getResources().obtainTypedArray(R.array.font_colors_array);
        mFontColors = new int[array_color.length()];
        for (int i = 0; i < array_color.length(); i++) {
            mFontColors[i] = array_color.getColor(i, 0);
        }
        mSelectorImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_font_color_selector);
        mSelect = 0;
        mIsSelectTouch = false;
    }

    public void setColor(int color) {
        for (int i = 0; i < mFontColors.length; i++) {
            if (mFontColors[i] == color) {
                mSelect = i;
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPalette(canvas);
        drawSelector(canvas, mSelect);
    }

    private void drawPalette(Canvas canvas) {
        if (mFontColors.length == 0) return;

        float parWidth = getWidth() / mFontColors.length;
        parWidth = (getWidth() - parWidth / 15 * 4) / mFontColors.length;
        //15:19の関係
        float sideWidth = parWidth / 15 * 2;

        int parHeight = getHeight() / 32 * 3 / 2;
        RectF rect;
        Paint paint = new Paint();
        for (int i = 0; i < mFontColors.length; i++) {
            rect = new RectF(parWidth * i + sideWidth, parHeight, parWidth * (i + 1) + sideWidth, getHeight() - parHeight);
            paint.setColor(mFontColors[i]);
            canvas.drawRect(rect, paint);
        }
    }

    private void drawSelector(Canvas canvas, int select) {
        if (mFontColors.length == 0 || mSelectorImage.getWidth() == 0) return;
        float parWidth = getWidth() / mFontColors.length;
        parWidth = (getWidth() - parWidth / 15 * 4) / mFontColors.length;
        float sideWidth = parWidth / 15 * 2;
        //15:19の関係
        float selectorWidth = parWidth / 15 * 19;
        Matrix matrix = new Matrix();
        matrix.setScale(selectorWidth / mSelectorImage.getWidth(),(float) getHeight() / (float) mSelectorImage.getHeight());
        matrix.postTranslate(select * parWidth, 0);
        canvas.drawBitmap(mSelectorImage, matrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsSelectTouch = (getPointToSelect(touchX) == mSelect);
                break;
            case MotionEvent.ACTION_MOVE:
                updateSelect(touchX);
                break;
            case MotionEvent.ACTION_UP:
                updateSelect(touchX);
                mIsSelectTouch = false;
                break;
        }
        invalidate();
        return true;
    }

    private void updateSelect(float touchX) {
        if (mIsSelectTouch) {
            Log.d("ColorSelectorView", "UpdateSelect");
            mSelect = getPointToSelect(touchX);
            if (mChangeListener != null) {
                if (mSelect > 0 && mSelect < mFontColors.length) {
                    mChangeListener.onColorChange(mFontColors[mSelect]);
                }
            }
        }
    }

    private int getPointToSelect(float x) {
        float parWidth = getWidth() / mFontColors.length;
        parWidth = (getWidth() - parWidth / 15 * 4) / mFontColors.length;
        //15:19の関係
        float sideWidth = parWidth / 15 * 2;
        Log.d("ColorSelectorView", "x:" + Float.toString(x) + "SideWidth:" + Float.toString(sideWidth) + "ParWidth" + Float.toString(parWidth));
        Log.d("ColorSelectorView", "Select:" + Integer.toString((int) ((x - sideWidth) / parWidth)));
        int select = (int) ((x - sideWidth) / parWidth);
        select = Math.max(select, 0);
        select = Math.min(select, mFontColors.length-1);
        return select;
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        this.mChangeListener = listener;
    }

    public interface OnColorChangeListener {
        public void onColorChange(int color);
    }
}
