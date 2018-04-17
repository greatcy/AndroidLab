package com.eli.test;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by chenjunheng on 2018/4/16.
 */

public class SimplestActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = SimplestActivity.class.getSimpleName();
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private List<Camera.Size> mSupportedPreviewSizes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplest);
        mSurfaceView = findViewById(R.id.surfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private Camera.Size getPreview(int width, int height) {
        float target = (float) width / (float) height;
        float closeValue = Float.MAX_VALUE;
        Camera.Size result = null;
        if (mSupportedPreviewSizes != null && mSupportedPreviewSizes.size() != 0) {
            for (int i = 0; i < mSupportedPreviewSizes.size(); i++) {
                float f = (float) mSupportedPreviewSizes.get(i).width / (float) mSupportedPreviewSizes.get(i).height;
                float gap = Math.abs(f - target);
                Log.d(TAG, "get support size w:" +
                        mSupportedPreviewSizes.get(i).width +
                        " h:" + mSupportedPreviewSizes.get(i).height +
                        " f:" + f +
                        " gap:" + gap + " target:" + target);
                if (gap > closeValue) {
                    continue;
                }
                if (gap < closeValue) {
                    closeValue = gap;
                    result = mSupportedPreviewSizes.get(i);
                } else if (result == null || (result.width < mSupportedPreviewSizes.get(i).width ||
                        result.height < mSupportedPreviewSizes.get(i).height)) {
                    closeValue = gap;
                    result = mSupportedPreviewSizes.get(i);
                }
            }
        }

        if (result != null) {
            Log.d(TAG, "final size w:" + result.width + " h:" + result.height);
        }

        return result;
    }


    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            if (mCamera != null) {
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Size size = getPreview(width, height);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doIt:
                takePhoto();
                break;
            case R.id.crash:
                finish();
                break;
        }
    }

    private void takePhoto() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(final byte[] data, Camera camera) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "test.jpeg");
                            if (!file.exists()) {
                                try {
                                    file.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    fos.write(data);
                                    fos.flush();
                                    fos.close();

                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SimplestActivity.this, "done! " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SimplestActivity.this, "error !", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                        }
                    }.start();
                }
            });
        }
    }
}
