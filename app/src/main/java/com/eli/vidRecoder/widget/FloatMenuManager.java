package com.eli.vidRecoder.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.eli.vidRecoder.R;

/**
 * Created by chenjunheng on 2018/4/29.
 */
public class FloatMenuManager {
    private final float RADIUS = 150.0f;
    private boolean isMenuOpen;

    private FloatItemBtn homeBtn, exitBtn, playBtn;
    private FloatDragBtn menuBtn;
    private PreviewFloatView previewFloatView;

    private boolean isPlaying;

    public FloatMenuManager(Context context) {
        homeBtn = new FloatItemBtn(context);
        menuBtn = new FloatDragBtn(context);
        exitBtn = new FloatItemBtn(context);
        playBtn = new FloatItemBtn(context);
        previewFloatView = new PreviewFloatView(context);
        previewFloatView.addFloatView(0, 0);
    }

    public void showFloatView() {
        menuBtn.addFloatView(0, 0);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(FloatDragBtn.TAG, "float anim click!");
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) menuBtn.getLayoutParams();
                Log.d(FloatDragBtn.TAG, "params PivotX:" + menuBtn.getPivotX() +
                        " PivotY:" + menuBtn.getPivotY() + " w:" + menuBtn.getWidth() +
                        " h:" + menuBtn.getHeight() + " x:" + menuBtn.getX() + " y:" + menuBtn.getY() +
                        " tx:" + menuBtn.getTranslationX() + " ty:" + menuBtn.getTranslationY() +
                        " paramX:" + params.x + " paramY:" + params.y);

                if (!isMenuOpen) {
                    isMenuOpen = true;
                    menuBtn.setDragEnable(false);

                    homeBtn.setIcon(R.drawable.home);
                    homeBtn.setAnimXY(menuBtn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI / 4) :
                                    menuBtn.getScreenWidth() - RADIUS * Math.sin(Math.PI / 4) -
                                            menuBtn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI / 4));
                    homeBtn.addFloatView(params.x, params.y);

                    if (!isPlaying)
                        playBtn.setIcon(R.drawable.play);
                    else {
                        playBtn.setIcon(R.drawable.stop);
                    }
                    playBtn.setAnimXY(menuBtn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI / 2) :
                                    menuBtn.getScreenWidth() - RADIUS * Math.sin(Math.PI / 2) -
                                            menuBtn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI / 2));
                    playBtn.addFloatView(params.x, params.y);

                    exitBtn.setIcon(R.drawable.exit);
                    exitBtn.setAnimXY(menuBtn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI * 3 / 4) :
                                    menuBtn.getScreenWidth() - RADIUS * Math.sin(Math.PI * 3 / 4) -
                                            menuBtn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI * 3 / 4));
                    exitBtn.addFloatView(params.x, params.y);
                } else {
                    closeMenu();
                }
            }
        });
    }

    private void closeMenu() {
        homeBtn.removeFloatView();
        exitBtn.removeFloatView();
        playBtn.removeFloatView();
        menuBtn.setDragEnable(true);
        isMenuOpen = false;
    }

    public void setHomeClick(final View.OnClickListener listener) {
        this.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    public void setPlayClick(final View.OnClickListener listener) {
        this.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    playBtn.setIcon(R.drawable.stop);
                } else {
                    playBtn.setIcon(R.drawable.play);
                }

                closeMenu();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setExitClick(View.OnClickListener listener) {
        this.exitBtn.setOnClickListener(listener);
    }

    public ViewGroup getCamContaner() {
        return this.previewFloatView;
    }

    public void removeFloatView() {
        if (homeBtn != null)
            homeBtn.removeFloatView();
        if (exitBtn != null)
            exitBtn.removeFloatView();
        if (playBtn != null)
            playBtn.removeFloatView();
        if (previewFloatView != null)
            previewFloatView.removeFloatView();
        if (menuBtn != null) {
            menuBtn.removeFloatView();
        }
    }


}
