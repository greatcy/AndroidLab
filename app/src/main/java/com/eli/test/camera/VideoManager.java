package com.eli.test.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by chenjunheng on 2018/4/20.
 */

public class VideoManager {
    private static final String TAG = VideoManager.class.getSimpleName();
    private CameraPreview mPreview;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    public VideoManager(CameraPreview preview) {
        this.mPreview = preview;
        this.mCamera = this.mPreview.getCamera();
    }

    public boolean startRecording() {
        if (prepareMediaRecorder()) {
            mMediaRecorder.start();
            return true;
        } else {
            releaseMediaRecorder();
        }
        return false;
    }

    public void stopRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
        releaseMediaRecorder();
    }

    private boolean prepareMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        String path = getSDPath();
        if (path != null) {

            File dir = new File(path + "/VideoRecorderALab");
            if (!dir.exists()) {
                dir.mkdir();
            }
            path = dir + "/" + getDate() + ".mp4";
            mMediaRecorder.setOutputFile(path);
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                releaseMediaRecorder();
                e.printStackTrace();
            }
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    /**
     * 获取系统时间
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }

    /**
     * 获取SD path
     */
    public String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外部存储的根目录
            return sdDir.toString();
        }

        return null;
    }
}
