package com.eli.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eli.test.bean.VideoBean;

import java.util.List;

/**
 * Created by eli on 18-4-22.
 */

public class PreviewAdapter extends BaseAdapter {
    private List<VideoBean> datas;

    public void setDatas(List<VideoBean> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return datas != null ? datas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    private class Holder {
        ImageView ivPoster;
        TextView tvFileName, tvSize, tvDuration;
    }
}
