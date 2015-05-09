package com.hackm.famiryboard.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hackm.famiryboard.model.pojo.FontStyles;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.model.viewobject.DecoImage;
import com.hackm.famiryboard.model.viewobject.DecoText;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.viewobject.Deco;

import java.util.ArrayList;

/**
 * Created by shunhosaka on 2014/12/18.
 */
public class WhiteBoardView extends View {

    private Paint mFramePaint;
    public static final float MENU_BUTTON_SIZE = 100.0f;
    private Bitmap mOpenBitmap;
    private Bitmap mDeleteBitmap;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_TAP = 1;
    public static final int TYPE_MOVE = 2;
    public static final int TYPE_CHANGE = 3;
    public static final int TYPE_DELETE = 4;

    private int mTouchType = TYPE_NONE;

    private ArrayList<Deco> mDecos = new ArrayList<Deco>();
    //タッチしているオブジェクトのindex.
    private int mFocusObjectIndex = -1;

    //Points
    private Point mLastPoint = null;

    private OnTextTapListener mTextTapListener;

    public WhiteBoardView(Context context) {
        super(context);
        initialize();
    }

    public WhiteBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * Initialize to Frame Paint
     */
    private void initialize() {
        Resources resources = getResources();
        mFramePaint = new Paint();
        mFramePaint.setStrokeWidth(resources.getDimensionPixelSize(R.dimen.deco_view_frame_width));
        mFramePaint.setColor(resources.getColor(R.color.accent_gray));

        mOpenBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_open);
        mDeleteBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_delete);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Deco deco : mDecos) {
            deco.draw(canvas);
        }
        if (mFocusObjectIndex >= 0 && mFocusObjectIndex < mDecos.size()) {
            drawFrame(canvas, mDecos.get(mFocusObjectIndex));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //ボタンを押したとき
                judgeTouchPoint(touchPoint);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchType == TYPE_TAP || mTouchType == TYPE_MOVE) {
                    mTouchType = TYPE_MOVE;
                    //移動処理
                    try {
                        Deco focusDeco = mDecos.get(mFocusObjectIndex);
                        //差分から移動を行う
                        focusDeco.x += (touchPoint.x - mLastPoint.x);
                        focusDeco.y += (touchPoint.y - mLastPoint.y);
                        mDecos.set(mFocusObjectIndex, focusDeco);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else if (mTouchType == TYPE_CHANGE) {
                    try {
                        Deco focusDeco = mDecos.get(mFocusObjectIndex);
                        focusDeco.setChange(touchPoint);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.d("CakeDecoView", "judgeTouchPoint touchType:"+Integer.toString(mTouchType));
                if (mTouchType == TYPE_DELETE) {
                    try {
                        Deco focusDeco = mDecos.get(mFocusObjectIndex);
                        if (focusDeco.getTouchType(touchPoint) == TYPE_DELETE) {
                            mDecos.remove(mFocusObjectIndex);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else if (mTouchType == TYPE_TAP && mDecos.get(mFocusObjectIndex).type == Deco.TYPE_TEXT) {
                    try {
                        DecoText decoText = (DecoText) mDecos.get(mFocusObjectIndex);
                        if (mTextTapListener != null) {
                            mTextTapListener.onTextTaped(decoText.styles, mFocusObjectIndex);
                        }
                    } catch (ClassCastException e) {
                        //何もしない
                        mDecos.get(mFocusObjectIndex).type = Deco.TYPE_CAMERA;
                    }
                }
                //タッチのタイプを初期化する
                mTouchType = TYPE_NONE;
                break;
        }
        mLastPoint = touchPoint;
        //画面の更新
        invalidate();
        return true;
    }

    /**
     * Add new Deco Item
     *
     * @param bitmap
     */
    public DecoImage addDecoImageItem(Bitmap bitmap, String imageUrl, int type) {
        float scale = 1.0f;
        if (Math.max(bitmap.getWidth(), bitmap.getHeight()) > 0) {
            scale = (float) Math.max(getWidth() / 2, getHeight() / 2) / (float) Math.max(bitmap.getWidth(), bitmap.getHeight());
        }
        if (AppConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "AddDecoItem" + Float.toString(scale));
        }
        return (DecoImage) this.addDecoItem(new DecoImage(bitmap, getWidth() / 2, getHeight() / 2, bitmap.getWidth() * scale, bitmap.getHeight() * scale, 0, type, imageUrl));
    }

    /**
     * Add new Deco Item
     *
     * @param bitmap
     */
    public DecoText addDecoTextItem(Bitmap bitmap, int type, FontStyles fontStyles) {
        return (DecoText) this.addDecoItem(new DecoText(bitmap, getWidth() / 2, getHeight() / 2, bitmap.getWidth(), bitmap.getHeight(), 0, type, fontStyles));
    }

    /**
     * Add new Deco Item
     * @param deco
     */
    public Deco addDecoItem(Deco deco) {
        if (mDecos != null) {
            mDecos.add(deco);
        }
        //Update
        invalidate();
        return deco;
    }

    public DecoText replaceDecoItem(Bitmap bitmap, int type, FontStyles fontStyles, int index) {
        DecoText decoText = null;
        try {
            decoText = (DecoText) mDecos.get(index);
            decoText.bitmap = bitmap;
            decoText.styles = fontStyles;
            mDecos.set(index, decoText);
        } catch (ArrayIndexOutOfBoundsException e) {
            addDecoTextItem(bitmap, type, fontStyles);
            return null;
        } catch (ClassCastException e) {
            addDecoTextItem(bitmap, type, fontStyles);
            return null;
        }
        //Update
        invalidate();
        return decoText;
    }

    /**
     * Set mTouchType and mFocusObjectIndex
     *
     * @param touch
     */
    private void judgeTouchPoint(Point touch) {
        //タッチ状態の初期化
        mTouchType = TYPE_NONE;
        //オブジェクトのチェック
        for (int i = mDecos.size() - 1; i >= 0; i--) {
            int touchType = mDecos.get(i).getTouchType(touch);
            if (AppConfig.DEBUG) {
                Log.d("CakeDecoView", "judgeTouchPoint touchType:" + Integer.toString(touchType));
            }
            //タップしていた場合
            if (touchType != TYPE_NONE) {
                if (mFocusObjectIndex == i) {
                    //そのオブジェクトにすでにフォーカスがあたっていた場合。
                    mTouchType = touchType;
                } else {
                    //フォーカスがあたっていなかった場合はムーブ
                    mTouchType = TYPE_MOVE;
                }
                mFocusObjectIndex = i;
                //処理終了
                return;
            } else if (mFocusObjectIndex == i) {
                //フォーカスを解除する
                mFocusObjectIndex = -1;
            }
        }
    }

    /**
     * ビューの周りの灰色のフレームを描画するクラス
     */
    private void drawFrame(Canvas canvas, Deco deco) {
        Point[] points = deco.getRectPoints();
        for (int i = 0; i < points.length; i++) {
            canvas.drawLine(points[i].x, points[i].y, points[(i + 1) % points.length].x, points[(i + 1) % points.length].y, mFramePaint);
        }
        float halfImageSize = MENU_BUTTON_SIZE / 2;

        Paint paint = new Paint();
        Matrix deleteMatrix = new Matrix();
        deleteMatrix.setScale(MENU_BUTTON_SIZE / mDeleteBitmap.getWidth(), MENU_BUTTON_SIZE / mDeleteBitmap.getHeight());
        deleteMatrix.postRotate(deco.rotation, halfImageSize, halfImageSize);
        deleteMatrix.postTranslate(points[0].x - halfImageSize, points[0].y - halfImageSize);
        canvas.drawBitmap(mDeleteBitmap, deleteMatrix, paint);

        Matrix openMatrix = new Matrix();
        openMatrix.setScale(MENU_BUTTON_SIZE / mOpenBitmap.getWidth(), MENU_BUTTON_SIZE / mOpenBitmap.getHeight());
        openMatrix.postRotate(deco.rotation, halfImageSize, halfImageSize);
        openMatrix.postTranslate(points[1].x - halfImageSize, points[1].y - halfImageSize);
        canvas.drawBitmap(mOpenBitmap, openMatrix, paint);
    }

    public void removeFrame() {
        mFocusObjectIndex = -1;
        invalidate();
    }

    public int getDecosCount() {
        return mDecos.size();
    }

    public void setOnTextTapListener(OnTextTapListener listener) {
        mTextTapListener = listener;
    }

    public interface OnTextTapListener {
        public void onTextTaped(FontStyles fontStyles, int index);
    }

}
