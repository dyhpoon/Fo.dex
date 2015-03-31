package com.dyhpoon.fodex.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.LruCache;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by darrenpoon on 14/3/15.
 */
public class CacheImageManager {

    // Sets the initial threadpool size to 1
    private static final int CORE_POOL_SIZE = 1;
    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> mCacheQueue;

    /*
     * Creates a cache of bitmap arrays indexed by image URLs. As new items are added to the
     * cache, the oldest items are ejected and subject to garbage collection.
     */
    private final LruCache<String, Bitmap> mCache;

    private static CacheImageManager mInstance = null;
    private final ThreadPoolExecutor mCacheThreadPool;

    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        mInstance = new CacheImageManager();
    }

    private CacheImageManager() {
        mCacheQueue = new LinkedBlockingQueue<>();
        final int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int size =  value.getByteCount() / 1024;
                return size;
            }
        };

        mCacheThreadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mCacheQueue);
    }

    public static void cacheImage(final Uri uri, final Drawable drawable) {
        mInstance.mCacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String key = uri.toString();
                if (mInstance.mCache.get(key) == null) {
                    Bitmap bitmap = DrawableUtils.toBitmap(drawable, Bitmap.Config.RGB_565);
                    mInstance.mCache.put(key, bitmap);
                }
            }
        });
    }

    public static Bitmap getCache(Uri uri) {
        return mInstance.mCache.get(uri.toString());
    }

    public static void clear() {
        mInstance.mCacheThreadPool.purge();
        mInstance.mCache.evictAll();
    }

}
