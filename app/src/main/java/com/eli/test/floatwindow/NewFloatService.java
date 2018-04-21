package com.eli.test.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eli.test.R;
import com.eli.test.Utils;
import com.eli.test.camera.CameraPreview;
import com.eli.test.camera.VideoManager;

/**
 * Created by chenjunheng on 2018/4/19.
 */

public class NewFloatService extends Service {
    //Log用的TAG
    private static final String TAG = "NewMainService";

    //要引用的布局文件.
    private FrameLayout toucherLayout;
    //布局参数.
    private WindowManager.LayoutParams params;
    //实例化的WindowManager.
    private WindowManager windowManager;

    private ImageView ivAddMenu, ivPlayStop, ivExit, ivHome;

    private Camera mCamera;
    private CameraPreview mPreview;

    private int mScreenWidth;

    private boolean mIsMenuOpened;
    private boolean mIsRightMode;//left or right

    private VideoManager videoManager;

    //状态栏高度.（接下来会用到）
    int statusBarHeight = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MainService Created");
        //OnCreate中来生成悬浮窗.
        createToucher();
    }

    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        //注意，这里的width和height均使用px而非dp.这里我偷了个懒
        //如果你想完全对应布局设置，需要先获取到机器的dpi
        //px与dp的换算为px = dp * (dpi / 160).
        params.width = Utils.dip2px(this,300);
        params.height = Utils.dip2px(this,300);

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (FrameLayout) inflater.inflate(R.layout.float_view, null);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        Log.i(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);

        //浮动窗口按钮.
        ivAddMenu = toucherLayout.findViewById(R.id.iv_add);

        ivHome = toucherLayout.findViewById(R.id.iv_home);
        ivExit = toucherLayout.findViewById(R.id.iv_exit);
        ivPlayStop = toucherLayout.findViewById(R.id.iv_play_stop);

        ivAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了");
                mIsMenuOpened = !mIsMenuOpened;
                if (mIsMenuOpened) {
                    showMenu();
                } else {
                    closeMenu();
                }
            }
        });

        ivAddMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //这就是状态栏偏移量用的地方
                params.x = (int) event.getRawX() - toucherLayout.getWidth() / 2;
                params.y = (int) event.getRawY() - toucherLayout.getHeight() / 2 - statusBarHeight;

                mIsRightMode = params.x >= mScreenWidth / 2;

                windowManager.updateViewLayout(toucherLayout, params);
                return false;
            }
        });
    }

    private void showMenu() {
        ivAddMenu.setImageDrawable(getResources().
                getDrawable(R.drawable.cancel_bg_selector));
        playToShow(ivHome);
        playToShow(ivExit);
        playToShow(ivPlayStop);
        initExitPos();
        playToShow(ivExit);
    }

    private void closeMenu() {
        ivAddMenu.setImageDrawable(getResources().
                getDrawable(R.drawable.add_bg_selector));
        playToHide(ivHome);
        playToHide(ivExit);
        playToHide(ivPlayStop);
        playToHide(ivExit);
    }

    private void initExitPos() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivExit.getLayoutParams();
        if (mIsRightMode) {
            params.setMargins(0, 0,
                    Utils.dip2px(this, 20), 0);
            params.gravity = Gravity.END;
        } else {
            params.setMargins(Utils.dip2px(this, 20), 0,
                    0, 0);
            params.gravity = Gravity.START;
        }
        ivExit.setLayoutParams(params);
    }

    private void playToShow(final View view) {
        AnimatorSet animatorSet = getAnimSet(view, ivAddMenu.getTranslationX(), 0,
                ivAddMenu.getTranslationY(), 0);  //组合动画
        animatorSet.start(); //启动
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    private void playToHide(final View view) {
        AnimatorSet animatorSet = getAnimSet(view, 0, ivAddMenu.getTranslationX(),
                0, ivAddMenu.getTranslationY());  //组合动画
        animatorSet.start(); //启动
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    private AnimatorSet getAnimSet(View view, float sx, float tx, float sy, float ty) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view,
                "translationX", sx, tx);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view,
                "translationY", sy, ty);

        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationX, translationY); //设置动画
        animatorSet.setDuration(1000);  //设置动画时间

        return animatorSet;
    }

    private void startRecordVid() {
        // 创建Camera实例
        mCamera = getCameraInstance();
        // 创建Preview view并将其设为activity中的内容
        mPreview = new CameraPreview(NewFloatService.this, mCamera);
        mPreview.setSurfaceTextureListener(mPreview);
        //设置浑浊
        mPreview.setAlpha(0.5f);
        // preview.setAlpha(0.0f);
        toucherLayout.addView(mPreview);
        videoManager = new VideoManager(mPreview);
    }

    /**
     * 安全获取Camera对象实例的方法
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // 试图获取Camera实例
        } catch (Exception e) {
            // 摄像头不可用（正被占用或不存在）
        }
        return c; // 不可用则返回null
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(toucherLayout);
        getCameraInstance().release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
