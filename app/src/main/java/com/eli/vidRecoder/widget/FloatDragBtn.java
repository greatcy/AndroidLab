package com.eli.vidRecoder.widget;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
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

import com.eli.vidRecoder.R;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class FloatDragBtn extends BaseFloatBtn {
    public static final String TAG = FloatDragBtn.class.getSimpleName();

    private int statusBarHeight;
    private boolean isDragging;
    private FloatEdgeAnim floatEdgeAnimation;
    private boolean dragEnable = true;

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
        super.init();

        this.setBackgroundResource(R.drawable.black_bg_selector);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        floatEdgeAnimation = new FloatEdgeAnim(this, layoutParams, windowManager, statusBarHeight);
//        floatEdgeAnimation.showDebugLine();
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
                isDragging = false;
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                final int historySize = event.getHistorySize();
                if (historySize != 0) {
                    float hisX = event.getHistoricalX(historySize - 1);
                    float hisY = event.getHistoricalY(historySize - 1);

                    float gapX = event.getX() - hisX;
                    float gapY = event.getY() - hisY;
                    Log.d(TAG, "ACTION_MOVE gapX:" + gapX + " gapY:" + gapY);

                    if (dragEnable && (gapX != 0 || gapY != 0)) {
                        layoutParams.x = (int) event.getRawX() - getWidth() / 2;
                        layoutParams.y = (int) event.getRawY() - getHeight() / 2
                                - statusBarHeight;
                        windowManager.updateViewLayout(this, layoutParams);
                        isDragging = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                setPressed(false);
                if (!isDragging) {
                    performClick();
                }
                if (dragEnable) {
                    floatEdgeAnimation.startAnim(event.getRawX(), event.getRawY());
                }
                break;
        }

        return true;
    }

    public void setDragEnable(boolean dragEnable) {
        this.dragEnable = dragEnable;
    }

    public boolean isInLefeEdge() {
        return floatEdgeAnimation.left;
    }

    public int getScreenWidth() {
        return floatEdgeAnimation.mScreenWidth;
    }

    @Override
    public void addFloatView(int x, int y) {
        if (y < floatEdgeAnimation.minLine) {
            y = floatEdgeAnimation.minLine;
        } else if (y > floatEdgeAnimation.maxLine) {
            y = floatEdgeAnimation.maxLine;
        }

        x = getScreenWidth();

        super.addFloatView(x, y);
    }
}
