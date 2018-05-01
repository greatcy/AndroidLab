package com.eli.vidRecoder.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.eli.vidRecoder.BuildConfig;
import com.eli.vidRecoder.R;

/**
 * Created by chenjunheng on 2018/5/1.
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionInfo = (TextView) findViewById(R.id.tv_about_v);
        String versionValue = String.format(getString(R.string.about_format), getString(R.string.app_name), BuildConfig.VERSION_NAME);

        versionInfo.setText(versionValue);
    }
}
