package com.eli.vidRecoder;

import android.app.Application;

/**
 * Created by chenjunheng on 2018/5/1.
 */
public class VidApplication extends Application {
    private boolean isFloatActive;

    public boolean isFloatActive() {
        return isFloatActive;
    }

    public void setFloatActive(boolean floatActive) {
        isFloatActive = floatActive;
    }
}
