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
public abstract class Deco {
    public static final int TYPE_CAMERA = 0;
    public static final int TYPE_GALLERY = 1;
    public static final int TYPE_STAMP = 1;
    public static final int TYPE_TEXT = 2;

    private Paint paint = null;
    public float x, y;
    public float width, height;
    public int rotation;
    public Bitmap bitmap;
    public int type = TYPE_CAMERA;

    public Deco(Bitmap bitmap, float x, float y, float width, float height, int rotation, int type) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.type = type;

        if(AppConfig.DEBUG) {
            Log.d("Deco", "Constructor Value:" + "(x:" + Float.toString(x) + ", y:" + Float.toString(y) + ")");
            Log.d("Deco", "Constructor Value:" + "(width:" + Float.toString(width) + ", height:" + Float.toString(height) + ")");
            Log.d("Deco", "Constructor Value:" + "(rotation:" + Integer.toString(rotation) + ")");
        }
    }

    /**
     * Draw this object
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (this.width == 0 || this.height == 0) {
            this.width = bitmap.getWidth() / 2;
            this.height = bitmap.getHeight() / 2;
        }
        canvas.drawBitmap(bitmap, getMatrix(), paint);
    }

    /**
     * @return Matrix
     */
    private Matrix getMatrix() {
        Matrix matrix = new Matrix();
        matrix.setScale(width / bitmap.getWidth(), height / bitmap.getHeight());
        matrix.postRotate(rotation, width / 2, height / 2);
        matrix.postTranslate(x - width / 2, y - height / 2);
        return  matrix;
    }

    /**
     * タッチしているかを返す。
     * @param touch is touch CakeDecoView point
     * @return
     */
    public int getTouchType(Point touch) {
        Point[] points = getRectPoints();
        int halfImageSize = (int) (WhiteBoardView.MENU_BUTTON_SIZE / 2);

        if (Math.sqrt((touch.x - points[0].x) * (touch.x - points[0].x) + (touch.y - points[0].y) * (touch.y - points[0].y)) < halfImageSize) {
            //points[0] -> delete
            return WhiteBoardView.TYPE_DELETE;
        } else if (Math.sqrt((touch.x - points[1].x) * (touch.x - points[1].x) + (touch.y - points[1].y) * (touch.y - points[1].y)) < halfImageSize) {
            //points[1] -> change
            return WhiteBoardView.TYPE_CHANGE;
        } else if(crossingNumber(points, touch)) {
            return WhiteBoardView.TYPE_TAP;
        } else {
            return WhiteBoardView.TYPE_NONE;
        }
    }

    public Point[] getRectPoints() {
        //ラジアン角に変換
        Double radian = rotation* Math.PI / 180;
        double coordinateX1 = width / 2 * Math.cos(radian);
        double coordinateX2 = height / 2 * Math.sin(radian);
        double coordinateY1 = width / 2 * Math.sin(radian);
        double coordinateY2 = height / 2 * Math.cos(radian);
        //頂点の角ポイント
        Point[] points = new Point[4];
        points[0] = new Point((int) (-coordinateX1 + coordinateX2 + x), (int) (-coordinateY1 - coordinateY2 + y));
        points[1] = new Point((int) (coordinateX1 + coordinateX2 + x), (int) (coordinateY1 - coordinateY2 + y));
        points[2] = new Point((int) (coordinateX1 - coordinateX2 + x), (int) (coordinateY1 + coordinateY2 + y));
        points[3] = new Point((int) (-coordinateX1 - coordinateX2 + x), (int) (-coordinateY1 + coordinateY2 + y));
        if(AppConfig.DEBUG) {
            for(Point point : points) {
                Log.d("getRectPoints","Point: "+point.toString());
            }
        }
        return points;
    }

    /**
     * Set new object size and rotation
     * @param touch (touch is RightTop point new Object size)
     * TODO
     */
    public void setChange(Point touch) {
        //拡張点のポイントを求める
        Point[] points = getRectPoints();
        Double radian = Math.toRadians(rotation);
        //最初の角度
        double firstRotaition = getRadian(x, y, points[1].x, points[1].y) * 180d / Math.PI;
        //新しい角度
        double newRotaition = getRadian(x, y, (double) touch.x, (double) touch.y)  * 180d / Math.PI;
        //差分を以前の角度に足す
        rotation -= (int) (newRotaition - firstRotaition);

        if(AppConfig.DEBUG) {
            Log.d("Deco", "Deco rotation:" + Integer.toString(rotation));
        }
        // 大きさ
        double currentLength = Math.sqrt(width * width + height * height) / 2;
        double newLength = Math.sqrt((touch.x - x) * (touch.x - x) + (touch.y - y) * (touch.y - y));
        double scale = newLength / currentLength;
        width = width * (float)scale;
        height = height * (float)scale;
    }

    protected double getRadian(double x, double y, double x2, double y2) {
        double radian = Math.atan2(x2 - x, y2 - y);
        return radian;
    }

    /**
     * Crossing Number Algorithm
     * @param polygon
     * @param touch
     * @return
     */
    boolean crossingNumber(Point[] polygon, Point touch) {
        int cnt = 0;
        for (int i = 0; i < 4; ++i) {
            final int x1 = polygon[(i + 1) % 4].x - polygon[i].x;
            final int y1 = polygon[(i + 1) % 4].y - polygon[i].y;
            final int x2 = touch.x - polygon[i].x;
            final int y2 = touch.y - polygon[i].y;
            if (x1 * y2 - x2 * y1 < 0) {
                ++cnt;
            } else {
                --cnt;
            }
        }
        if(AppConfig.DEBUG) {
            Log.d("Deco","Touch Point: "+touch.toString());
            Log.d("Deco", "CrossingNumber CN:" + Integer.toString(cnt));
        }
        return cnt == 4 || cnt == -4;
    }

    public abstract String encodeToSvg();
    public abstract Deco decoveBySvg(String svgData);
}
