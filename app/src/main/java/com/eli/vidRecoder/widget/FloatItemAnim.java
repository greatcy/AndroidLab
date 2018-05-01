package com.eli.vidRecoder.widget;

import android.view.View;
import android.view.WindowManager;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class FloatItemAnim extends BaseFloatAnim {
    private float x, y;

    public FloatItemAnim(View mTarget, WindowManager.LayoutParams params, WindowManager windowManager) {
        super(mTarget, params, windowManager);
    }

    public void setTargetXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    float getTargetX(float curX) {
        return x;
    }

    @Override
    float getTargetY(float curY) {
        return y;
    }

    @Override
    int getDuration() {
        return 200;
    }
}
