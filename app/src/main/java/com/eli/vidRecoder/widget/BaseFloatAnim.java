package com.eli.vidRecoder.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by chenjunheng on 2018/4/28.
 * TODO the params is redundant
 */
abstract class BaseFloatAnim {
    protected View mTarget;
    protected WindowManager.LayoutParams params;
    protected WindowManager windowManager;
    private AnimatorListenerAdapter adapter;

    private ValueAnimator.AnimatorUpdateListener updateYListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cur = (float) animation.getAnimatedValue();
                    params.y = (int) cur;
                    windowManager.updateViewLayout(mTarget, params);
                }
            };

    private ValueAnimator.AnimatorUpdateListener updateXListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cur = (float) animation.getAnimatedValue();
                    params.x = (int) cur;
                    windowManager.updateViewLayout(mTarget, params);
                }
            };

    public BaseFloatAnim(View mTarget, WindowManager.LayoutParams params,
                         WindowManager windowManager) {
        this.mTarget = mTarget;
        this.params = params;
        this.windowManager = windowManager;
    }

    public void startAnim(float curX, float curY) {
        AnimatorSet as = new AnimatorSet();
        ValueAnimator vaX = ValueAnimator.ofFloat(curX, getTargetX(curX));

        int DURATION = getDuration();
        vaX.setDuration(DURATION);
        vaX.setInterpolator(new AccelerateInterpolator());
        vaX.addUpdateListener(updateXListener);

        float ty = getTargetY(curY);

        if (ty != curY) {
            ValueAnimator vaY = ValueAnimator.ofFloat(curY, ty);
            vaY.setDuration(DURATION);
            vaY.setInterpolator(new AccelerateInterpolator());
            vaY.addUpdateListener(updateYListener);
            as.playTogether(vaY, vaX);
        } else {
            as.play(vaX);
        }

        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (adapter != null) {
                    adapter.onAnimationEnd(animation);
                }
            }
        });
        as.start();
    }

    abstract float getTargetX(float curX);

    abstract float getTargetY(float curY);

    abstract int getDuration();

    public void setAnimationListener(AnimatorListenerAdapter adapter) {
        this.adapter = adapter;
    }
}
