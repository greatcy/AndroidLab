package com.eli.vidRecoder;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.eli.vidRecoder.widget.dialog.DialogUtils;

/**
 * Created by eli on 18-5-6.
 */

public class TestLoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_loading_activity);

        final Dialog dialog = DialogUtils.createLoadingDialog(this,
                getString(R.string.deleting));
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 5000);
    }
}
