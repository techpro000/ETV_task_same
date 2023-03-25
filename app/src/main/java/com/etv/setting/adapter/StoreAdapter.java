package com.etv.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etv.util.MyLog;
import com.etv.view.MyProgressView;
import com.ys.etv.R;
import com.etv.setting.entity.StoreEntity;
import com.etv.util.sdcard.MySDCard;

import java.util.List;

public class StoreAdapter extends BaseAdapter {

    Context context;
    List<StoreEntity> list;
    LayoutInflater inflater;

    public StoreAdapter(Context context, List<StoreEntity> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<StoreEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_storage, null);
            viewHolder.tv_path = (TextView) view.findViewById(R.id.tv_path);
            viewHolder.tv_size_total = (TextView) view.findViewById(R.id.tv_size_total);
            viewHolder.tv_size_last = (TextView) view.findViewById(R.id.tv_size_last);
            viewHolder.pro_last = (MyProgressView) view.findViewById(R.id.pro_last);
            viewHolder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        StoreEntity entity = list.get(i);
        String path = entity.getPath();
        viewHolder.tv_path.setText("Path: " + path);
        long total = entity.getTotalSize();
        if (total < 1) {
            total = 1;
        }
        long lastSize = entity.getLastSize();
        viewHolder.tv_size_total.setText("  Total: " + total + " G");
        viewHolder.tv_size_last.setText("Available: " + lastSize + " M");
        int progressShow = (int) ((lastSize * 100) / (total * 1024));
        MyLog.cdl("===progressShow==" + progressShow);
        viewHolder.pro_last.setProgress(100 - progressShow);
        int type = entity.getType();
        if (type == StoreEntity.TYPE_INNER) {
            viewHolder.iv_app_icon.setBackgroundResource(R.mipmap.sd_inner);
        } else if (type == StoreEntity.TYPE_SD) {
            viewHolder.iv_app_icon.setBackgroundResource(R.mipmap.sd_sdcard);
        } else if (type == StoreEntity.TYPE_USB) {
            viewHolder.iv_app_icon.setBackgroundResource(R.mipmap.sd_u);
            viewHolder.tv_size_total.setText("  Total: " + " UnKnow");
            viewHolder.tv_size_last.setText("Available: " + " UnKnow");
            viewHolder.pro_last.setProgress(0);
        } else {
            viewHolder.iv_app_icon.setBackgroundResource(R.mipmap.sd_sdcard);
        }
        return view;
    }

    class ViewHolder {
        TextView tv_path, tv_size_total, tv_size_last;
        MyProgressView pro_last;
        ImageView iv_app_icon;
    }
}
