package com.eli.vidRecoder;

import java.lang.ref.WeakReference;

/**
 * Created by chenjunheng on 2018/4/30.
 * TODO Thread pool
 */
public class ThreadManager {
    private static ThreadManager mInstance;

    private ThreadManager() {
    }

    public static synchronized ThreadManager getInstance() {
        if (mInstance == null) {
            mInstance = new ThreadManager();
        }
        return mInstance;
    }

    public void runInNewThread(final Runnable runnable) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }.start();
    }
}
