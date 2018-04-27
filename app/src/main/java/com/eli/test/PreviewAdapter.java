package com.eli.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
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
    private Context mContext;

    public PreviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<VideoBean> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public VideoBean getItem(int i) {
        return datas != null ? datas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        final VideoBean videoBean = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(this.mContext).
                    inflate(R.layout.video_item_layout, viewGroup, false);
            holder = new Holder();
            holder.ivPoster = view.findViewById(R.id.iv_poster);
            holder.tvDuration = view.findViewById(R.id.tv_duration);
            holder.tvFileName = view.findViewById(R.id.tv_title);
            holder.tvSize = view.findViewById(R.id.tv_size);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (holder != null) {
            holder.ivPoster.setImageBitmap
                    (VidImageLoader.getInstance().getBitmap(videoBean.getPath()));

            holder.ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("file://" + videoBean.getPath());
                    Intent intent = new Intent();
                    intent.setDataAndType(uri, "video/mp4");
                    mContext.startActivity(intent);
                }
            });

            holder.tvSize.setText(videoBean.getSize());
            holder.tvDuration.setText(videoBean.getDuration());
            holder.tvFileName.setText(videoBean.getFileName());
        }

        return view;
    }

    private class Holder {
        ImageView ivPoster;
        TextView tvFileName, tvSize, tvDuration;
    }


}
