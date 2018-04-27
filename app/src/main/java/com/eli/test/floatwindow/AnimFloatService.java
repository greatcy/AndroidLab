package com.eli.test.floatwindow;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import com.eli.test.R;
import com.eli.test.Utils;

/**
 * Created by eli on 18-4-26.
 */

public class AnimFloatService extends NewFloatService {
    private View mMenuBtn;
    private Point point = new Point();

    private int minLine;
    private int maxLine;

    private ValueAnimator.AnimatorUpdateListener updateXListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cur = (float) animation.getAnimatedValue();
                    params.x = (int) cur;
                    windowManager.updateViewLayout(toucherLayout, params);
                }
            };

    private ValueAnimator.AnimatorUpdateListener updateYListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cur = (float) animation.getAnimatedValue();
                    params.y = (int) cur;
                    windowManager.updateViewLayout(toucherLayout, params);
                }
            };

    @Override
    protected void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);


        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        final int mScreenHeight = outMetrics.heightPixels;

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.gravity = Gravity.START | Gravity.TOP;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;


        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (FrameLayout) inflater.inflate(R.layout.anim_float_view, null);
        mMenuBtn = toucherLayout.findViewById(R.id.iv_ctr);

        mMenuBtn.setOnClickListener(this);


        Log.i(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        mMenuBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //这就是状态栏偏移量用的地方
                params.x = (int) event.getRawX() - toucherLayout.getWidth() / 2;
                params.y = (int) event.getRawY() - toucherLayout.getHeight() / 2 - statusBarHeight;
                windowManager.updateViewLayout(toucherLayout, params);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    AnimatorSet as = new AnimatorSet();
                    ValueAnimator va;
                    if (event.getRawX() < point.x) {
                        va = ValueAnimator.ofFloat(event.getRawX(), 0);
                    } else {
                        va = ValueAnimator.ofFloat(event.getRawX(),
                                mScreenWidth - toucherLayout.getWidth());
                    }
                    va.setDuration(100);
                    va.setInterpolator(new AccelerateInterpolator());
                    va.addUpdateListener(updateXListener);

                    ValueAnimator vaY = null;
                    if (event.getRawY() < minLine) {
                        vaY = ValueAnimator.ofFloat(event.getRawY(), minLine);
                    } else if (event.getRawY() > maxLine) {
                        vaY = ValueAnimator.ofFloat(event.getRawY(), maxLine);
                    }
                    if (vaY != null) {
                        vaY.setDuration(100);
                        vaY.setInterpolator(new AccelerateInterpolator());
                        vaY.addUpdateListener(updateYListener);
                        as.playTogether(vaY, va);
                    } else {
                        as.play(va);
                    }
                    as.start();
                }
                return false;
            }
        });

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);
        //设置窗口初始停靠位置.
        params.x = mScreenWidth / 2 - Utils.dip2px(this, 60) / 2;
        params.y = mScreenHeight / 2 - Utils.dip2px(this, 60) / 2 - statusBarHeight;
        point.x = mScreenWidth / 2 - Utils.dip2px(this, 60) / 2;
        point.y = mScreenHeight / 2 - Utils.dip2px(this, 60) / 2 - statusBarHeight;

        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        //add height line
        minLine = Utils.dip2px(this, 100) + statusBarHeight;
        maxLine = mScreenHeight - Utils.dip2px(this, 160);
        View minLineView = inflater.inflate(R.layout.red_line_view, null);
        View maxLineView = inflater.inflate(R.layout.red_line_view, null);

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 1;
        params.y = minLine;
        windowManager.addView(minLineView, params);
        params.y = maxLine;
        windowManager.addView(maxLineView, params);

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_ctr:
                break;
        }
    }
}
