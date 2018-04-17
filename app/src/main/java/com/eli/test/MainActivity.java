package com.eli.test;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.tv_start:

                    break;
                case R.id.tv_stop:

                    break;
            }
        }
    }

    private class CameraOperator extends Thread {
        private Camera mCamera;
        @Override
        public void run() {
            super.run();
            mCamera = Camera.open();

            mCamera.startPreview();
        }

        private void stopAndRelease(){
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }
}
