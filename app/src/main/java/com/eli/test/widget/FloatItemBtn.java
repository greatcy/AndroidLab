package com.eli.test.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.eli.test.R;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class FloatItemBtn extends BaseFloatBtn {
    private float showTargetX, showTargetY;
    private float hideX, hideY;


    public FloatItemBtn(Context context) {
        super(context);
    }

    public FloatItemBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatItemBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setXY(double animTargetX, double animTargetY) {
        this.showTargetX = (float) animTargetX;
        this.showTargetY = (float) animTargetY;
    }

    @Override
    public void addFloatView(int x, int y) {
        super.addFloatView(x, y);
        hideX = x;
        hideY = y;

        FloatItemAnim itemAnim = new FloatItemAnim(this, layoutParams, windowManager);
        itemAnim.setTargetXY(showTargetX, showTargetY);
        itemAnim.startAnim(x, y);
    }


    public void setIcon(int resId) {
        ImageView iv = findViewById(R.id.iv_ctr);
        iv.setImageResource(resId);
    }

    @Override
    public void removeFloatView() {
        FloatItemAnim itemAnim = new FloatItemAnim(this, layoutParams, windowManager);
        itemAnim.setTargetXY(hideX, hideY);
        itemAnim.startAnim(hideX, hideY);
        itemAnim.setAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                FloatItemBtn.super.removeFloatView();
            }
        });
        itemAnim.startAnim(layoutParams.x, layoutParams.y);
    }
}
