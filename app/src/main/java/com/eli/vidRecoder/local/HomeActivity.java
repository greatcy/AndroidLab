package com.eli.vidRecoder.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.eli.vidRecoder.FloatActivity;
import com.eli.vidRecoder.R;
import com.eli.vidRecoder.VidApplication;
import com.eli.vidRecoder.bean.VideoBean;

import java.util.List;

/**
 * Created by eli on 18-4-22.
 */

public class HomeActivity extends AppCompatActivity {
    private GridView mGridView;
    private PreviewAdapter mAdapter;

    private View mPlayBtn;
    private View mEmpty;
    private SwipeRefreshLayout mSwipeRefresh;
    private boolean mIsRefreshing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.mGridView = findViewById(R.id.grid_view);
        this.mAdapter = new PreviewAdapter(this);
        this.mGridView.setAdapter(this.mAdapter);
        this.mEmpty = findViewById(R.id.iv_empty);
        this.mEmpty.setVisibility(View.VISIBLE);

        mPlayBtn = findViewById(R.id.iv_play);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((VidApplication) getApplication()).isFloatActive()) {
                    Intent intent = new Intent(HomeActivity.this,
                            FloatActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

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
        Scanner scanner = new Scanner(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleError() {
        this.mSwipeRefresh.setRefreshing(false);
        mIsRefreshing = false;
        mEmpty.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
    }

    private void refreshUI(List<VideoBean> data) {
        if (data != null && data.size() != 0) {
            mEmpty.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            if (this.mAdapter != null) {
                this.mAdapter.setDatas(data);
                this.mAdapter.notifyDataSetChanged();
            }
        } else {
            mGridView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
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
