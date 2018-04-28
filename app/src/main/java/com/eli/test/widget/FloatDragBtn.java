package com.eli.test.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.eli.test.R;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class FloatDragBtn extends BaseFloatBtn {
    public static final String TAG = FloatDragBtn.class.getSimpleName();

    private int statusBarHeight;
    private boolean isDragging;
    private FloatEdgeAnim floatEdgeAnimation;
    private boolean dragEnable;

    public FloatDragBtn(Context context) {
        super(context);
    }

    public FloatDragBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatDragBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init() {
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        //赋值WindowManager&LayoutParam.
        layoutParams = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        layoutParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.gravity = Gravity.START | Gravity.TOP;

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.anim_float_view, null);
        this.addView(view);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        floatEdgeAnimation = new FloatEdgeAnim(this, layoutParams, windowManager, statusBarHeight);
        floatEdgeAnimation.showDebugLine();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int masked = event.getActionMasked();
        switch (masked) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                final int historySize = event.getHistorySize();
                if (historySize != 0) {
                    float hisX = event.getHistoricalX(historySize - 1);
                    float hisY = event.getHistoricalY(historySize - 1);

                    float gapX = event.getX() - hisX;
                    float gapY = event.getY() - hisY;
                    Log.d(TAG, "ACTION_MOVE gapX:" + gapX + " gapY:" + gapY);

                    if (!dragEnable && (gapX != 0 || gapY != 0)) {
                        layoutParams.x = (int) event.getRawX() - getWidth() / 2;
                        layoutParams.y = (int) event.getRawY() - getHeight() / 2 - statusBarHeight;
                        windowManager.updateViewLayout(this, layoutParams);
                        isDragging = true;
                    } else {
                        isDragging = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                if (!isDragging) {
                    performClick();
                }
                if (dragEnable)
                    floatEdgeAnimation.startAnim(event.getRawX(), event.getRawY());
                break;
        }

        return true;
    }

    public void setDragEnable(boolean dragEnable) {
        this.dragEnable = dragEnable;
    }
}
