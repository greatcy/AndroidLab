package com.eli.test.widget;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.eli.test.R;
import com.eli.test.Utils;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class FloatEdgeAnim extends BaseFloatAnim {
    private Point middlePoint = new Point();
    static int HEIGHT_LIMIT = 150;
    int mScreenWidth;
    int maxLine, minLine;
    boolean left = true;

    FloatEdgeAnim(View mTarget, WindowManager.LayoutParams params,
                  WindowManager windowManager, int statusBarHeight) {
        super(mTarget, params, windowManager);

        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        middlePoint.x = outMetrics.widthPixels / 2 - mTarget.getWidth() / 2;
        middlePoint.y = outMetrics.heightPixels / 2 - mTarget.getHeight() / 2 - statusBarHeight;

        minLine = Utils.dip2px(mTarget.getContext(), HEIGHT_LIMIT) - statusBarHeight;
        maxLine = outMetrics.heightPixels - Utils.dip2px(mTarget.getContext(), HEIGHT_LIMIT);
        mScreenWidth = outMetrics.widthPixels;
    }

    //for debug
    public void showDebugLine() {
        LayoutInflater inflater = LayoutInflater.from(mTarget.getContext());
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
    float getTargetX(float curX) {
        if (curX < middlePoint.x) {
            left = true;
            return 0;
        } else {
            left = false;
            return mScreenWidth - mTarget.getWidth();
        }
    }

    @Override
    float getTargetY(float curY) {
        if (curY < minLine) {
            return minLine;
        } else if (curY > maxLine) {
            return maxLine - mTarget.getHeight();
        } else {
            return curY;
        }
    }

    @Override
    int getDuration() {
        return 100;
    }
}
