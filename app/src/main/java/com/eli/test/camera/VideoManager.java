package com.eli.test.camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

import com.eli.test.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by chenjunheng on 2018/4/20.
 */

public class VideoManager implements MediaRecorder.OnErrorListener {
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
            enableMute();
            return true;
        } else {
            releaseMediaRecorder();
        }
        return false;
    }


    private int oldStreamVolume;

    private void enableMute() {
        AudioManager audioManager = (AudioManager)
                mPreview.getContext().getApplicationContext().
                        getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null)
            return;
        oldStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    private void disableMute() {
        AudioManager audioManager = (AudioManager)
                mPreview.getContext().getApplicationContext().
                        getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null)
            return;
        audioManager.setStreamVolume(AudioManager.STREAM_RING, oldStreamVolume, 0);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    public void stopRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            disableMute();
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
        mMediaRecorder.setOnErrorListener(this);
        String path = Configuration.getInstance(this.mPreview.getContext()).getStoreFolder();
        if (path != null) {
            File dir = new File(path);
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

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        Log.d(TAG, "record video error i:" + i + " i1:" + i1);
    }
}
