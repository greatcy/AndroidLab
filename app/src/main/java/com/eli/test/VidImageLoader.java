package com.eli.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Created by chenjunheng on 2018/4/23.
 */

public class VidImageLoader {
    private static VidImageLoader mInstance;
    //定义LruCache，指定其key和保存数据的类型
    private LruCache<String, Bitmap> mImageCache;

    private VidImageLoader() {
        //获取当前进程可以使用的内存大小，单位换算为KB
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        //取总内存的1/4作为缓存
        final int cacheSize = maxMemory / 4;

        //初始化LruCache
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            //定义每一个存储对象的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public synchronized static VidImageLoader getInstance() {
        if (mInstance == null) {
            mInstance = new VidImageLoader();
        }
        return mInstance;
    }

    public void preLoadBitmap(String url) {
        if (!TextUtils.isEmpty(url)) {
            Bitmap bitmap = getDiskBitmap(url);
            if (bitmap != null) {
                putBitmap(url, bitmap);
            }
        }
    }

    //获取数据
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = mImageCache.get(url);
        if (bitmap == null) {
            bitmap = getDiskBitmap(url);
            putBitmap(url, bitmap);
        }
        return bitmap;
    }

    //存储数据
    public void putBitmap(String url, Bitmap bitmap) {
        mImageCache.put(url, bitmap);
    }

    private Bitmap getDiskBitmap(String url) {
        return ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
}
