package com.etv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ys.etv.R;

import java.util.List;

public class PopStringAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    List<String> list = null;

    public PopStringAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<String> list_show) {
        this.list = list_show;
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
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_spinner, null);
            viewHolder.tv_show = (TextView) view.findViewById(R.id.tv_show);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String desc = list.get(i).toString();
        viewHolder.tv_show.setText(desc);
        return view;
    }


    class ViewHolder {
        TextView tv_show;
    }

}
