package com.eli.vidRecoder.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.eli.vidRecoder.R;

/**
 * Created by chenjunheng on 2018/4/28.
 */
class BaseFloatBtn extends FrameLayout {

    protected WindowManager windowManager;
    protected WindowManager.LayoutParams layoutParams;

    public BaseFloatBtn(Context context) {
        super(context);
        init();
    }

    public BaseFloatBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseFloatBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        //赋值WindowManager&LayoutParam.
        layoutParams = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        layoutParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.gravity = Gravity.START | Gravity.TOP;

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.anim_float_view, null);
        this.addView(view);
    }

    public void addFloatView(int x, int y) {
        layoutParams.x = x;
        layoutParams.y = y;
        windowManager.addView(this, layoutParams);
    }

    public void removeFloatView() {
        windowManager.removeView(this);
    }

    public void forceRemove(){
        windowManager.removeView(this);
    }
}
