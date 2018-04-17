package com.eli.test.floatwindow;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.eli.test.R;

/**
 * Created by chenjunheng on 2018/4/19.
 */

public class FloatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_activity);

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
                Intent intent = new Intent(FloatActivity.this, FloatService.class);
                Toast.makeText(FloatActivity.this, "已开启Toucher", Toast.LENGTH_SHORT).show();
                startService(intent);
                finish();
            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(FloatActivity.this, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        } else {
            //SDK在23以下，不用管.
            Intent intent = new Intent(FloatActivity.this, FloatService.class);
            startService(intent);
            finish();
        }
    }

}
