package com.eli.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by eli on 18-4-21.
 */

public class TestAnimActivity extends AppCompatActivity {
    private final String TAG = TestAnimActivity.class.getSimpleName();
    private ImageView ivAddMenu, ivPlayStop, ivExit, ivHome;
    private boolean mIsMenuOpened;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_view);

        ivAddMenu = findViewById(R.id.iv_add);

        ivHome = findViewById(R.id.iv_home);
        ivExit = findViewById(R.id.iv_exit);
        ivPlayStop = findViewById(R.id.iv_play_stop);

        ivAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsMenuOpened = !mIsMenuOpened;
                if (mIsMenuOpened) {
                    showMenu();
                } else {
                    closeMenu();
                }
            }
        });
    }

    private void showMenu() {
        ivAddMenu.setImageDrawable(getResources().
                getDrawable(R.drawable.cancel_bg_selector));
        playToShow(ivHome, ivAddMenu.getX() - ivHome.getX(), 0,
                ivAddMenu.getY() - ivHome.getY(), 0);
        playToShow(ivPlayStop, ivAddMenu.getX() - ivPlayStop.getX(),
                0, ivAddMenu.getY() - ivPlayStop.getY(), 0);
        playToShow(ivExit, ivAddMenu.getX() - ivExit.getX(), 0,
                ivAddMenu.getY() - ivHome.getY(), 0);
    }

    private void closeMenu() {
        ivAddMenu.setImageDrawable(getResources().
                getDrawable(R.drawable.add_bg_selector));
        playToHide(ivHome, 0, ivAddMenu.getX() - ivHome.getX(),
                0, ivAddMenu.getY() - ivHome.getY());
        playToHide(ivPlayStop, 0, ivAddMenu.getX() - ivPlayStop.getX(),
                0, ivAddMenu.getY() - ivPlayStop.getY());
        playToHide(ivExit, 0, ivAddMenu.getX() - ivExit.getX(),
                0, ivAddMenu.getY() - ivHome.getY());
    }

    private void playToShow(final View view, float sx, float tx,
                            float sy, float ty) {
        TranslateAnimation translateAnimation = new TranslateAnimation(sx, tx, sy, ty);
        translateAnimation.setDuration(200);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(translateAnimation);
    }

    private void playToHide(final View view, float sx, float tx,
                            float sy, float ty) {
        TranslateAnimation translateAnimation = new TranslateAnimation(sx, tx, sy, ty);
        translateAnimation.setDuration(300);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(translateAnimation);
    }
}
