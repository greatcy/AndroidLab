package com.eli.test.local;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.GridView;

import com.eli.test.Configuration;
import com.eli.test.R;
import com.eli.test.Utils;
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
        Scanner scanner=new Scanner(this);
        scanner.scan(new Scanner.ICallback() {
            @Override
            public void onScanComplete(List<VideoBean> datas) {
                refreshUI(datas);
            }

            @Override
            public void onScanFail() {
                handleError();
            }

            @Override
            public void onScanUpdate(List<VideoBean> datas) {
                refreshUI(datas);
            }
        });
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
