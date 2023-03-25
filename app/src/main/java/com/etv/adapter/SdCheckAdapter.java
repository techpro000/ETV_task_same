package com.etv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etv.entity.SdCheckEntity;
import com.ys.etv.R;

import java.util.List;

/***
 * 通用List Grid界面显示adaper
 */
public class SdCheckAdapter extends BaseAdapter {
    List<SdCheckEntity> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<SdCheckEntity> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public SdCheckAdapter(Context paramContext, List<SdCheckEntity> paramList) {
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

    public View getView(int position, View convertView, ViewGroup paramViewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_sdcard_check, null);
            viewHolder.tv_num = ((TextView) convertView.findViewById(R.id.tv_num));
            viewHolder.tv_content = ((TextView) convertView.findViewById(R.id.tv_content));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SdCheckEntity entty = appInfos.get(position);
        boolean isRed = entty.isRed();
        if (isRed) {
            viewHolder.tv_num.setTextColor(context.getResources().getColor(R.color.red));
            viewHolder.tv_content.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.tv_num.setTextColor(context.getResources().getColor(R.color.grey));
            viewHolder.tv_content.setTextColor(context.getResources().getColor(R.color.grey));
        }
        viewHolder.tv_num.setText(entty.getNumber() + ".");
        viewHolder.tv_content.setText(entty.getTitle());
        return convertView;
    }

    private class ViewHolder {
        TextView tv_num, tv_content;
    }
}
