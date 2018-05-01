package com.eli.vidRecoder.camera;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.eli.vidRecoder.local.HomeActivity;
import com.eli.vidRecoder.R;
import com.eli.vidRecoder.ThreadManager;
import com.eli.vidRecoder.widget.FloatMenuManager;


/**
 * Created by chenjunheng on 2018/4/29.
 */
public class VidService extends Service {
    //Log用的TAG
    private static final String TAG = VidService.class.getSimpleName();

    private Camera mCamera;
    private CameraPreview mPreview;
    private VideoManager videoManager;
    private FloatMenuManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        initView();
        initRecorder();
    }

    private void initView() {
        manager = new FloatMenuManager(this);
        manager.showFloatView();
        manager.setHomeClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VidService.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        manager.setExitClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process.killProcess(Process.myPid());
//                manager.removeFloatView();
//                stopSelf();
            }
        });

        manager.setPlayClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.isPlaying()) {
                    ThreadManager.getInstance().runInNewThread(new Runnable() {
                        @Override
                        public void run() {
                            videoManager.startRecording();
                        }
                    });
                    Toast.makeText(VidService.this, R.string.playing_tips, Toast.LENGTH_LONG)
                            .show();
                } else {
                    ThreadManager.getInstance().runInNewThread(new Runnable() {
                        @Override
                        public void run() {
                            videoManager.stopRecording();
                        }
                    });
                    Toast.makeText(VidService.this, R.string.stop_tips, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }

    private void initRecorder() {
        // 创建Camera实例
        ThreadManager.getInstance().runInNewThread(new Runnable() {
            @Override
            public void run() {
                mCamera = getCameraInstance();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCamera != null) {
                            // 创建Preview view并将其设为activity中的内容
                            mPreview = new CameraPreview(VidService.this, mCamera);
                            //设置浑浊
                            mPreview.setAlpha(0.5f);
                            // preview.setAlpha(0.0f);
                            manager.getCamContaner().addView(mPreview);
                            videoManager = new VideoManager(mPreview);
                        }
                    }
                });
            }
        });

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getCameraInstance().release();
        manager.removeFloatView();
    }
}
