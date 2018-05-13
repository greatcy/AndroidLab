package com.eli.vidRecoder.local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eli.vidRecoder.widget.dialog.DialogUtils;
import com.eli.vidRecoder.R;
import com.eli.vidRecoder.Utils;
import com.eli.vidRecoder.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eli on 18-4-22.
 */

public class PreviewAdapter extends BaseAdapter {
    private List<VideoBean> datas;
    private Context mContext;

    private boolean mIsEditMode;

    private PopupWindow mOperatorMenu;

    private class MenuListener implements View.OnClickListener {
        private VideoBean bean;

        MenuListener(VideoBean bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.action_delete:
                    DialogUtils.showConfirmDeleteDLG(mContext, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.removeFile(bean.getPath());
                            if (datas != null) {
                                datas.remove(bean);
                                notifyDataSetChanged();
                            }
                        }
                    }, null);
                    break;
                case R.id.tv_open:
                    open(bean);
                    break;
            }
            mOperatorMenu.dismiss();
        }
    }

    PreviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void removeSelectedBeans() {
        if (datas != null) {
            List<VideoBean> beans = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).isSelected()) {
                    Utils.removeFile(datas.get(i).getPath());
                    beans.add(datas.get(i));
                }
            }
            datas.removeAll(beans);
        }
    }

    void setDatas(List<VideoBean> datas) {
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
        final Holder holder;
        final VideoBean videoBean = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(this.mContext).
                    inflate(R.layout.video_item_layout, viewGroup, false);
            holder = new Holder();
            holder.mask = view.findViewById(R.id.mask);
            holder.ivPoster = view.findViewById(R.id.iv_poster);
            holder.tvDuration = view.findViewById(R.id.tv_duration);
            holder.tvFileName = view.findViewById(R.id.tv_title);
            holder.tvSize = view.findViewById(R.id.tv_size);
            holder.ivPlayBtn = view.findViewById(R.id.iv_play);
            holder.checkBox = view.findViewById(R.id.checkBox);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (holder != null) {
            Bitmap bitmap = VidImageLoader.getInstance().getBitmap(videoBean.getPath());
            if (bitmap != null) {
                holder.ivPoster.setImageBitmap(bitmap);
            }

            if (mIsEditMode) {
                holder.ivPlayBtn.setEnabled(false);
                holder.mask.setEnabled(false);
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(videoBean.isSelected());
                holder.checkBox.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                videoBean.setSelected(isChecked);
                            }
                        });
            } else {
                holder.ivPlayBtn.setEnabled(true);
                holder.mask.setEnabled(true);
                holder.checkBox.setVisibility(View.GONE);

                holder.ivPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open(videoBean);
                    }
                });


                View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenu(v, videoBean);
                        holder.mask.setPressed(false);
                        holder.ivPlayBtn.setPressed(false);
                        return false;
                    }
                };
                holder.ivPlayBtn.setOnLongClickListener(longClickListener);

                holder.mask.setOnLongClickListener(longClickListener);
            }

            holder.tvSize.setText(videoBean.getSize());
            holder.tvDuration.setText(videoBean.getDuration());
            holder.tvFileName.setText(videoBean.getFileName());
        }

        return view;
    }

    public void setEditMode(boolean mIsEditMode) {
        this.mIsEditMode = mIsEditMode;
        notifyDataSetChanged();
    }

    private void showMenu(View anchor, VideoBean videoBean) {
//        // 用于PopupWindow的View
//        View contentView = LayoutInflater.from(mContext).inflate(R.layout.vid_menu_item,
//                null, false);
//        // 创建PopupWindow对象，其中：
//        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
//        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
//        mOperatorMenu = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 设置PopupWindow的背景
//        mOperatorMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        // 设置PopupWindow是否能响应外部点击事件
//        mOperatorMenu.setOutsideTouchable(true);
//        // 设置PopupWindow是否能响应点击事件
//        mOperatorMenu.setTouchable(true);
//        // 显示PopupWindow，其中：
//        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
//        mOperatorMenu.showAsDropDown(anchor);
//
//        MenuListener listener = new MenuListener(videoBean);
//        contentView.findViewById(R.id.action_delete).setOnClickListener(listener);
//        contentView.findViewById(R.id.tv_open).setOnClickListener(listener);
        videoBean.setSelected(true);
        ((HomeActivity) mContext).enterActionMode();
    }

    private void open(VideoBean videoBean) {
        if (videoBean != null) {
            Uri uri = Uri.parse("file://" + videoBean.getPath());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            mContext.startActivity(intent);
        }
    }

    private class Holder {
        ImageView ivPoster, ivPlayBtn;
        TextView tvFileName, tvSize, tvDuration;
        View mask;
        CheckBox checkBox;
    }


}
