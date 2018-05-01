package com.eli.test;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.eli.fileselector.OpenFileDialog;

import java.io.File;

/**
 * Created by eli on 18-4-30.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_setting);
        }

        //init set location path
        ViewGroup vg = (ViewGroup) findViewById(R.id.setting_location_path);
        TextView tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        TextView tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_location_title));
        tvSubtitle.setText(getString(R.string.setting_location));
        vg.setOnClickListener(this);

        //init force process
        vg = (ViewGroup) findViewById(R.id.setting_keep_process);
        tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_keep_process_mode_title));
        tvSubtitle.setText(getString(R.string.setting_keep_process_mode));
        Switch switchWidget = (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(Configuration.getInstance(this).isIsForceMode());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Configuration.getInstance(SettingActivity.this).setIsForceMode(isChecked);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_location_path:
                final OpenFileDialog dialog = new OpenFileDialog(SettingActivity.this,
                        OpenFileDialog.DIALOG_TYPE.FOLDER_DIALOG, false);
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        File f = dialog.getCurrentPath();
                        if (f != null && f.exists()) {
                            Log.d(TAG, "select file path:" + f.getAbsolutePath());
                            Configuration.getInstance
                                    (SettingActivity.this).setStoreFolder(f.getAbsolutePath());
                        }
                    }
                });
                dialog.show();
                break;
        }
    }
}
