package com.eli.test.floatwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eli.test.HomeActivity;
import com.eli.test.R;
import com.eli.test.Utils;
import com.eli.test.camera.CameraPreview;
import com.eli.test.camera.VideoManager;
import com.eli.test.widget.MenuCtrler;

/**
 * Created by chenjunheng on 2018/4/19.
 */

public class NewFloatService extends Service implements View.OnClickListener {
    //Log用的TAG
    protected static final String TAG = "NewMainService";

    //要引用的布局文件.
    protected FrameLayout toucherLayout;
    //布局参数.
    protected WindowManager.LayoutParams params;
    //实例化的WindowManager.
    protected WindowManager windowManager;

    private Camera mCamera;
    private CameraPreview mPreview;

    protected int mScreenWidth;
    private int mScreenHeight;

    private MenuCtrler menuCtrler;

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

    protected void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = mScreenWidth / 2;
        params.y = mScreenWidth / 2;

        //设置悬浮窗口长宽数据.
        params.width = Utils.dip2px(this, 42);
        params.height = Utils.dip2px(this, 42);

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

        menuCtrler = new MenuCtrler(this, windowManager, toucherLayout, params);

        initRecorder();

        menuCtrler.getIvAddMenu().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch event.rawX:" + event.getRawX() +
//                        " event.rawY:" + event.getRawY() + " left bound:" +
//                        leftBound + " right bound:" + rightBound);
                //这就是状态栏偏移量用的地方
                params.x = (int) event.getRawX() - toucherLayout.getWidth() / 2;
                params.y = (int) event.getRawY() - toucherLayout.getHeight() - statusBarHeight;

                windowManager.updateViewLayout(toucherLayout, params);
                return false;
            }
        });

        menuCtrler.setMenuClickListener(this);
    }

    private void initRecorder() {
        // 创建Camera实例
        mCamera = getCameraInstance();
        // 创建Preview view并将其设为activity中的内容
        mPreview = new CameraPreview(NewFloatService.this, mCamera);
        mPreview.setSurfaceTextureListener(mPreview);
        //设置浑浊
//        mPreview.setAlpha(0.5f);
        mPreview.setAlpha(0.0f);
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
        Log.d(TAG, "service onDestroy");
        windowManager.removeView(toucherLayout);
        getCameraInstance().release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_exit:
                Log.d(TAG, "onClick exit");
                stopSelf();
                break;
            case R.id.iv_home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.iv_play_stop:
                if (!menuCtrler.isPlaying()) {
                    videoManager.startRecording();
                    Toast.makeText(this, "start recording...", Toast.LENGTH_LONG).show();
                } else {
                    videoManager.stopRecording();
                    Toast.makeText(this, "done!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
