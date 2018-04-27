package com.eli.test;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.eli.test.bean.VideoBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eli on 18-4-22.
 */

public class HomeActivity extends AppCompatActivity {
    private GridView mGridView;
    private PreviewAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefresh;
    private boolean mIsRefreshing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.mGridView = findViewById(R.id.grid_view);
        this.mAdapter = new PreviewAdapter(this);
        this.mGridView.setAdapter(this.mAdapter);

        mSwipeRefresh = findViewById(R.id.srl_container);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mIsRefreshing)
                    scanVidFolder();
            }
        });
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setRefreshing(true);
        scanVidFolder();
    }

    private void scanVidFolder() {
        mIsRefreshing = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                File folder = new File(Configuration.getInstance(HomeActivity.this).getStoreFolder());
                if (folder.exists() && folder.isDirectory()) {
                    final List<VideoBean> datas = new ArrayList<>();
                    File files[] = folder.listFiles();

                    MediaPlayer player = new MediaPlayer();
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        if (f != null && !f.isDirectory() && f.getName().endsWith(".mp4")) {
                            VideoBean bean = new VideoBean();
                            bean.setFileName(f.getName());
                            bean.setPath(f.getPath());
                            bean.setSize(Utils.formatSize(HomeActivity.this, f.length()));
                            try {
                                player.setDataSource(f.getPath());  //recordingFilePath（）为音频文件的路径
                                player.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int duration = player.getDuration();//获取音频的时间
                            bean.setDuration(Utils.transferLongToFormat(duration));
                            VidImageLoader.getInstance().preLoadBitmap(f.getPath());
                            datas.add(bean);
                        }
                    }
                    player.release();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI(datas);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleError();
                        }
                    });
                }
            }
        }.start();
    }

    private void handleError() {
        this.mSwipeRefresh.setRefreshing(false);
        mIsRefreshing = false;
    }

    private void refreshUI(List<VideoBean> data) {
        if (this.mAdapter != null) {
            this.mAdapter.setDatas(data);
            this.mAdapter.notifyDataSetChanged();
        }
        this.mSwipeRefresh.setRefreshing(false);
        mIsRefreshing = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.normal_menu, menu);
        return true;
    }
}
