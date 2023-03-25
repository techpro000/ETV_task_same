package com.etv.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etv.entity.PoOnOffLogEntity;
import com.ys.etv.R;

import java.util.List;

/***
 * 用来展示保存的定时开关机数据
 */
public class PowerSaveAdapter extends BaseAdapter {
    List<PoOnOffLogEntity> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<PoOnOffLogEntity> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public PowerSaveAdapter(Context paramContext, List<PoOnOffLogEntity> paramList) {
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
            convertView = inflater.inflate(R.layout.item_power_info, null);
            viewHolder.tv_time_off = ((TextView) convertView.findViewById(R.id.tv_time_off));
            viewHolder.tv_time_on = ((TextView) convertView.findViewById(R.id.tv_time_on));
            viewHolder.tv_time_save = ((TextView) convertView.findViewById(R.id.tv_time_save));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PoOnOffLogEntity entty = appInfos.get(position);
        viewHolder.tv_time_off.setText(entty.getOffTime());
        viewHolder.tv_time_on.setText(entty.getOnTime());
        viewHolder.tv_time_save.setText(entty.getCreateTime());
        return convertView;
    }

    private class ViewHolder {
        TextView tv_time_off;
        TextView tv_time_on;
        TextView tv_time_save;
    }
}
