package com.eli.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.GridView;

import com.eli.test.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eli on 18-4-22.
 */

public class HomeActivity extends AppCompatActivity {
    private GridView mGridView;
    private PreviewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.mGridView = findViewById(R.id.grid_view);
        this.mAdapter = new PreviewAdapter(this);
        this.mGridView.setAdapter(this.mAdapter);

        List<VideoBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            VideoBean vb = new VideoBean();
            list.add(vb);
        }

        this.mAdapter.setDatas(list);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.normal_menu, menu);
        return true;
    }
}
