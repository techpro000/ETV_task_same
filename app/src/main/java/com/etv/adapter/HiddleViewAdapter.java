package com.etv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etv.entity.HiddleBeanEntity;
import com.ys.etv.R;

import java.util.List;

/***
 * 通用List Grid界面显示adaper
 */
public class HiddleViewAdapter extends BaseAdapter {
    List<HiddleBeanEntity> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<HiddleBeanEntity> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public HiddleViewAdapter(Context paramContext, List<HiddleBeanEntity> paramList) {
        this.context = paramContext;
        this.appInfos = paramList;
        inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return this.context;
    }

    public int getCount() {
        return appInfos.size();
    }

    public Object getItem(int paramInt) {
        return this.appInfos.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_bean_hiddle, null);
            viewHolder.iv_bean_item = ((ImageView) convertView.findViewById(R.id.iv_bean_item));
            viewHolder.tv_bean_item = ((TextView) convertView.findViewById(R.id.tv_bean_item));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final HiddleBeanEntity entty = appInfos.get(position);
        viewHolder.iv_bean_item.setBackgroundResource(entty.getImageId());
        viewHolder.tv_bean_item.setText(entty.getTitle());
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_bean_item;
        TextView tv_bean_item;
    }
}
