package com.eli.vidRecoder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.eli.vidRecoder.local.HomeActivity;

/**
 * Created by chenjunheng on 2018/5/1.
 */
public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getSimpleName();

    private boolean hasGantedPermission, canFloat;

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
                if (Settings.canDrawOverlays(SplashActivity.this)) {
                    canFloat = true;
                } else {
                    //若没有权限，提示获取.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    Toast.makeText(SplashActivity.this, R.string.need_float_tips, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            } else {
                //SDK在23以下，不用管.
                canFloat = true;
            }
        }
    };

    /**
     * 创建快捷方式
     *
     * @param name
     * @param icon
     */
    public void installShortCut(String name, int icon) {
        Intent shortcut = new Intent("com.eli.vidRecoder.FloatActivity");

        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        shortcut.putExtra("duplicate", false); // 不允许重复创建

        // 快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
        intent.addCategory("android.intent.category.LAUNCHER");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        sendBroadcast(shortcut);
    }

    private void enterHome() {
//        installShortCut(getString(R.string.quick_start),R.drawable.play);
        Log.d(TAG, "enterHome");
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (Configuration.getInstance(SplashActivity.this).isUserQuickMode()) {
                    intent = new Intent(SplashActivity.this, FloatActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                }

                Configuration.getInstance(SplashActivity.this).setUserQuickMode(true);
                startActivity(intent);
                finish();
            }
        }, 500);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(SplashActivity.this)) {
                canFloat = true;
            }
        } else {
            //SDK在23以下，不用管.
            canFloat = true;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermissioin = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int recordAudioPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);

            if (readPermissioin == PackageManager.PERMISSION_GRANTED &&
                    writePermission == PackageManager.PERMISSION_GRANTED &&
                    cameraPermission == PackageManager.PERMISSION_GRANTED &&
                    recordAudioPermission == PackageManager.PERMISSION_GRANTED) {
                hasGantedPermission = true;
            }
        } else {
            hasGantedPermission = true;
        }

        if (canFloat && hasGantedPermission) {
            enterHome();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasGantedPermission) {
            requestPermission();
        }

        if (!canFloat) {
            requestFloatWindows.run();
        }

        if (canFloat && hasGantedPermission) {
            enterHome();
        }
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
                hasGantedPermission = true;
            }
        } else {
            hasGantedPermission = true;
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

        if (getPermission) {
            hasGantedPermission = true;
        }
    }

}
