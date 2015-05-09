package com.hackm.famiryboard.controller.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.hackm.famiryboard.model.system.AppConfig;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by shunhosaka on 2015/01/21.
 */
public class PicassoHelper {
    static private Picasso picasso = null;
    static Cache cache = null;

    public static Picasso with(int cacheSize, Context context) {
        if (picasso == null) {
            int maxSize = calculateMemoryCacheSize(context);
            cache = new LruCache(cacheSize <= maxSize ? cacheSize : maxSize);
            picasso = new Picasso.Builder(context)
                    .memoryCache(cache)
                    .build();
            if (AppConfig.DEBUG) {
                picasso.setIndicatorsEnabled(true);
                picasso.setLoggingEnabled(true);
            }
        }
        return picasso;
    }

    public static Picasso with(Context context) {
        if (picasso == null) {
            cache = new LruCache(calculateMemoryCacheSize(context));
            picasso = new Picasso.Builder(context)
                    .memoryCache(cache)
                    .build();
            if (AppConfig.DEBUG) {
                picasso.setIndicatorsEnabled(true);
                picasso.setLoggingEnabled(true);
            }
        }
        return picasso;
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        return 1024 * 1024 * memoryClass / 10;//7;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

}
