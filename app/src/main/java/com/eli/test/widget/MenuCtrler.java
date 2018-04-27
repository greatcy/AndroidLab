package com.eli.test.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.eli.test.R;
import com.eli.test.Utils;

/**
 * Created by eli on 18-4-22.
 */

public class MenuCtrler {
    private ImageView ivAddMenu, ivPlayStop, ivExit, ivHome;
    private boolean mIsMenuOpened;
    private ViewGroup mRoot;
    private Context mContext;
    private static final int ANIM_DURATION = 150;
    private boolean isPlaying;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    public MenuCtrler(final Context context, final WindowManager wm, @NonNull ViewGroup root,
                      final WindowManager.LayoutParams params) {
        this.mRoot = root;
        this.mContext = context;
        this.windowManager = wm;
        this.params = params;

        ivAddMenu = mRoot.findViewById(R.id.iv_add);

        ivHome = mRoot.findViewById(R.id.iv_home);
        ivExit = mRoot.findViewById(R.id.iv_exit);
        ivPlayStop = mRoot.findViewById(R.id.iv_play_stop);

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

    public void showMenu() {
        //TODO should be faster
        params.width = Utils.dip2px(mContext, 150);
        params.height = Utils.dip2px(mContext, 100);
//        params.x = params.x - Utils.dip2px(mContext, (150 - 42) / 2);
//        params.y = params.y - Utils.dip2px(mContext, 100);
        MenuCtrler.this.windowManager.updateViewLayout(mRoot, params);
        ivAddMenu.setImageDrawable(mContext.getResources().
                getDrawable(R.drawable.cancel_bg_selector));
        playToShow(ivHome, ivAddMenu.getX() - ivHome.getX(), 0,
                ivAddMenu.getY() - ivHome.getY(), 0);
        playToShow(ivPlayStop, ivAddMenu.getX() - ivPlayStop.getX(),
                0, ivAddMenu.getY() - ivPlayStop.getY(), 0);
        playToShow(ivExit, ivAddMenu.getX() - ivExit.getX(), 0,
                ivAddMenu.getY() - ivHome.getY(), 0);
    }

    private void closeMenu() {
        ivAddMenu.setImageDrawable(mContext.getResources().
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
        translateAnimation.setDuration(ANIM_DURATION);
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
        translateAnimation.setDuration(ANIM_DURATION);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);

                //TODO no!
                params.width = Utils.dip2px(mContext, 42);
                params.height = Utils.dip2px(mContext, 42);
                MenuCtrler.this.windowManager.updateViewLayout(mRoot, params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(translateAnimation);
    }

    public void setMenuClickListener(@NonNull final View.OnClickListener listener) {
        this.ivHome.setOnClickListener(listener);
        this.ivPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                isPlaying = !isPlaying;
                ivPlayStop.setImageDrawable(isPlaying ?
                        mContext.getResources().getDrawable(R.drawable.stop_bg_selector) :
                        mContext.getResources().getDrawable(R.drawable.play_bg_selector));
            }
        });
        this.ivExit.setOnClickListener(listener);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isMenuOpened() {
        return mIsMenuOpened;
    }

    public ImageView getIvAddMenu() {
        return ivAddMenu;
    }
}
