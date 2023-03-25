package com.etv.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ys.etv.R;
import com.etv.task.entity.TimerDbEntity;

import java.util.List;

public class PowerOnOffAdapter extends BaseAdapter {


    Context context;
    List<TimerDbEntity> list;
    LayoutInflater inflater;

    public PowerOnOffAdapter(Context context, List<TimerDbEntity> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<TimerDbEntity> list) {
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
            view = inflater.inflate(R.layout.item_power_on_off, null);
            viewHolder.tv_time_on = (TextView) view.findViewById(R.id.tv_time_on);
            viewHolder.tv_time_off = (TextView) view.findViewById(R.id.tv_time_off);
            viewHolder.ck_mon = (ImageView) view.findViewById(R.id.ck_mon);
            viewHolder.ck_tue = (ImageView) view.findViewById(R.id.ck_tue);
            viewHolder.ck_wed = (ImageView) view.findViewById(R.id.ck_wed);
            viewHolder.ck_thu = (ImageView) view.findViewById(R.id.ck_thu);
            viewHolder.ck_fri = (ImageView) view.findViewById(R.id.ck_fri);
            viewHolder.ck_sta = (ImageView) view.findViewById(R.id.ck_sta);
            viewHolder.ck_sun = (ImageView) view.findViewById(R.id.ck_sun);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TimerDbEntity entity = list.get(i);
        viewHolder.tv_time_on.setText(entity.getTtOnTime());
        viewHolder.tv_time_off.setText(entity.getTtOffTime());
        boolean ttMon = Boolean.parseBoolean(entity.getTtMon());
        boolean ttTue = Boolean.parseBoolean(entity.getTtTue());
        boolean ttWed = Boolean.parseBoolean(entity.getTtWed());
        boolean ttThu = Boolean.parseBoolean(entity.getTtThu());
        boolean ttFri = Boolean.parseBoolean(entity.getTtFri());
        boolean ttSat = Boolean.parseBoolean(entity.getTtSat());
        boolean ttSun = Boolean.parseBoolean(entity.getTtSun());
        viewHolder.ck_mon.setBackgroundResource(ttMon ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_tue.setBackgroundResource(ttTue ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_wed.setBackgroundResource(ttWed ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_thu.setBackgroundResource(ttThu ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_fri.setBackgroundResource(ttFri ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_sta.setBackgroundResource(ttSat ? R.mipmap.check_yes : R.mipmap.check_no);
        viewHolder.ck_sun.setBackgroundResource(ttSun ? R.mipmap.check_yes : R.mipmap.check_no);
        return view;
    }


    class ViewHolder {
        TextView tv_time_on;
        TextView tv_time_off;
        ImageView ck_mon;
        ImageView ck_tue;
        ImageView ck_wed;
        ImageView ck_thu;
        ImageView ck_fri;
        ImageView ck_sta;
        ImageView ck_sun;
    }
}
