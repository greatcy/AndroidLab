package com.eli.vidRecoder.local;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.eli.fileselector.OpenFileDialog;
import com.eli.vidRecoder.Configuration;
import com.eli.vidRecoder.R;

import java.io.File;

/**
 * Created by eli on 18-4-30.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = SettingActivity.class.getSimpleName();
    private TextView folderTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_setting);
        }

        //init set location path
        ViewGroup vg = findViewById(R.id.setting_location_path);
        TextView tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        TextView tvSubtitle =  vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_location_title));
        folderTextView=tvSubtitle;
        tvSubtitle.setText(Configuration.getInstance(this).getStoreFolder());
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
                            folderTextView.setText(Configuration.getInstance(SettingActivity.this).getStoreFolder());
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
