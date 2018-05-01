package com.eli.test.local;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.eli.test.Configuration;
import com.eli.test.ThreadManager;
import com.eli.test.Utils;
import com.eli.test.bean.VideoBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjunheng on 2018/4/30.
 */
public class Scanner {
    private Context context;
    private Handler handler;
    private int videoCount;
    private ICallback callback;
    private List<VideoBean> datas;

    public Scanner(Context context) {
        this.context = context;
        handler = new Handler(context.getMainLooper());
        this.datas = new ArrayList<>();
    }

    public void scan(final ICallback callBack) {
        this.callback = callBack;
        final List<getVideoInfo> getVideoInfos = new ArrayList<>();

        ThreadManager.getInstance().runInNewThread(new Runnable() {
            @Override
            public void run() {
                File folder = new File(Configuration.getInstance(context).getStoreFolder());
                boolean result = false;
                if (folder.exists() && folder.isDirectory()) {
                    result = folder.mkdir();
                }

                if (!result) {
                    File files[] = folder.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            File f = files[i];
                            if (f != null && !f.isDirectory() && f.getName().endsWith(".mp4")) {
                                VideoBean bean = new VideoBean();
                                bean.setFileName(f.getName());
                                bean.setPath(f.getPath());
                                bean.setSize(Utils.formatSize(context, f.length()));

                                getVideoInfo gvi = new getVideoInfo(bean, f);
                                getVideoInfos.add(gvi);

                                datas.add(bean);
                            }
                        }
                    }

                    if (callBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onScanComplete(datas);
                                for (getVideoInfo gvi : getVideoInfos) {
                                    gvi.start();
                                }
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onScanFail();
                        }
                    });
                }
            }
        });
    }

    private synchronized void gainedVideoInfo() {
        videoCount--;
        if (callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onScanUpdate(datas);
                }
            });
        }
    }

    private class getVideoInfo implements Runnable {
        private VideoBean bean;
        private File f;
        private MediaPlayer player;

        getVideoInfo(VideoBean bean, File file) {
            this.bean = bean;
            this.f = file;
            player = new MediaPlayer();
        }

        void start() {
            ThreadManager.getInstance().runInNewThread(this);
        }

        @Override
        public void run() {
            try {
                player.reset();
                player.setDataSource(f.getPath());
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int duration = player.getDuration();//获取音频的时间
            bean.setDuration(Utils.transferLongToFormat(duration));
            VidImageLoader.getInstance().preLoadBitmap(f.getPath());

            gainedVideoInfo();
            player.release();
        }
    }

    interface ICallback {
        void onScanComplete(List<VideoBean> datas);

        void onScanFail();

        void onScanUpdate(List<VideoBean> datas);
    }
}
