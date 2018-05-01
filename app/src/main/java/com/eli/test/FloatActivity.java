package com.eli.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.eli.test.camera.VidService;

/**
 * Created by chenjunheng on 2018/4/19.
 */

public class FloatActivity extends AppCompatActivity {
    private Runnable mGrantedPermissionHolder = new Runnable() {
        @Override
        public void run() {
            requestFloatWindows.run();
        }
    };

    private Runnable requestFloatWindows = new Runnable() {
        @Override
        public void run() {
            //当AndroidSDK>=23及Android版本6.0及以上时，需要获取OVERLAY_PERMISSION.
            //使用canDrawOverlays用于检查，下面为其源码。其中也提醒了需要在manifest文件中添加权限.
            /**
             * Checks if the specified context can draw on top of other apps. As of API
             * level 23, an app cannot draw on top of other apps unless it declares the
             * {@link android.Manifest.permission#SYSTEM_ALERT_WINDOW} permission in its
             * manifest, <em>and</em> the user specifically grants the app this
             * capability. To prompt the user to grant this approval, the app must send an
             * intent with the action
             * {@link android.provider.Settings#ACTION_MANAGE_OVERLAY_PERMISSION}, which
             * causes the system to display a permission management screen.
             *
             */
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(FloatActivity.this)) {
                    Intent intent = new Intent(FloatActivity.this, VidService.class);
                    Toast.makeText(FloatActivity.this,
                            getString(R.string.app_name) +
                                    getString(R.string.start), Toast.LENGTH_SHORT).show();
                    startService(intent);
//                    finish();
                    moveTaskToBack(false);
                } else {
                    //若没有权限，提示获取.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    Toast.makeText(FloatActivity.this, R.string.need_float_tips, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            } else {
                //SDK在23以下，不用管.
                Intent intent = new Intent(FloatActivity.this, VidService.class);
                startService(intent);
//                finish();
                moveTaskToBack(false);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermissioin = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int recordAudioPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);

            if (readPermissioin != PackageManager.PERMISSION_GRANTED ||
                    writePermission != PackageManager.PERMISSION_GRANTED ||
                    cameraPermission != PackageManager.PERMISSION_GRANTED ||
                    recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
                int REQUEST_PERMISSION_CODE = 100;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
            } else {
                mGrantedPermissionHolder.run();
            }
        } else {
            mGrantedPermissionHolder.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean getPermission = true;

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                getPermission = false;
            }
        }

        if (getPermission && mGrantedPermissionHolder != null) {
            mGrantedPermissionHolder.run();
        }
    }

}
