package com.etv.util.location.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ys.etv.R;

import java.util.List;

/***
 * 通用List Grid界面显示adaper
 */
public class ProcityAdapter extends BaseAdapter {
    List<String> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<String> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public ProcityAdapter(Context paramContext, List<String> paramList) {
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
            convertView = inflater.inflate(R.layout.item_pro_city, null);
            viewHolder.tv_pro_city = ((TextView) convertView.findViewById(R.id.tv_pro_city));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String entty = appInfos.get(position).toString();
        viewHolder.tv_pro_city.setText(entty);
        return convertView;
    }

    private class ViewHolder {
        TextView tv_pro_city;
    }
}
