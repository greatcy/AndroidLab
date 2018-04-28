package com.eli.test.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.eli.test.R;

/**
 * Created by chenjunheng on 2018/4/28.
 */
public class TestFloatAnimActivity extends AppCompatActivity {
    private final float RADIUS = 150.0f;
    private boolean isMenuOpen;
    private FloatItemBtn[] items = new FloatItemBtn[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_float_anim_activity);

        final FloatDragBtn btn = new FloatDragBtn(this);
        btn.addFloatView(0, 0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(FloatDragBtn.TAG, "float anim click!");
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) btn.getLayoutParams();
                Log.d(FloatDragBtn.TAG, "params PivotX:" + btn.getPivotX() +
                        " PivotY:" + btn.getPivotY() + " w:" + btn.getWidth() +
                        " h:" + btn.getHeight() + " x:" + btn.getX() + " y:" + btn.getY() +
                        " tx:" + btn.getTranslationX() + " ty:" + btn.getTranslationY() +
                        " paramX:" + params.x + " paramY:" + params.y);

                if (!isMenuOpen) {
                    isMenuOpen = true;
                    btn.setDragEnable(false);
                    FloatItemBtn fib = new FloatItemBtn(TestFloatAnimActivity.this);

                    items[0] = fib;
                    fib.setIcon(R.drawable.home);
                    fib.setAnimXY(btn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI / 4) :
                                    btn.getScreenWidth() - RADIUS * Math.sin(Math.PI / 4)-
                                            btn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI / 4));
                    fib.addFloatView(params.x, params.y);

                    fib = new FloatItemBtn(TestFloatAnimActivity.this);
                    items[1] = fib;
                    fib.setIcon(R.drawable.play);
                    fib.setAnimXY(btn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI / 2) :
                                    btn.getScreenWidth() - RADIUS * Math.sin(Math.PI / 2)-
                                            btn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI / 2));
                    fib.addFloatView(params.x, params.y);

                    fib = new FloatItemBtn(TestFloatAnimActivity.this);

                    items[2] = fib;
                    fib.setIcon(R.drawable.exit);
                    fib.setAnimXY(btn.isInLefeEdge() ? RADIUS * Math.sin(Math.PI * 3 / 4) :
                                    btn.getScreenWidth() - RADIUS * Math.sin(Math.PI * 3 / 4)-
                                            btn.getWidth(),
                            params.y - RADIUS * Math.cos(Math.PI * 3 / 4));
                    fib.addFloatView(params.x, params.y);

                    for (FloatItemBtn item : items) {
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(FloatDragBtn.TAG, "item click!");
                            }
                        });
                    }
                } else {
                    for (FloatItemBtn item : items) {
                        item.removeFloatView();
                    }
                    btn.setDragEnable(true);
                    isMenuOpen = false;
                }

            }
        });
    }
}
