package com.eli.vidRecoder.widget;

import android.content.Context;

/**
 * Created by chenjunheng on 2018/4/29.
 */
class PreviewFloatView extends BaseFloatBtn {
    public PreviewFloatView(Context context) {
        super(context);
        setAlpha(0);
        layoutParams.width = 10;
        layoutParams.height = 10;
    }
}
