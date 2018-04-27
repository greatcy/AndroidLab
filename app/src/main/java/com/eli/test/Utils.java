package com.eli.test;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eli on 18-4-21.
 */

public class Utils {
    //年-月-日 时:分:秒格式
    private static final String YMDHMS = "HH:mm:ss";

    /**
     * long型时间转化为指定格式的字符串
     *
     * @param time
     * @return
     */
    public static String transferLongToFormat(long time) {
        time = time / 1000;
        return String.format(Locale.CHINA, "%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率PX(像素)转成DP
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static String formatSize(Context context, long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return context.getString(R.string.size_gb, f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return context.getString(R.string.size_mb, f);
        } else {
            float f = s / 1024f;
            return context.getString(R.string.size_kb, f);
        }
    }
}
