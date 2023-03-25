package com.etv.adapter;

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
public class TextAdapter extends BaseAdapter {
    List<String> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<String> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public TextAdapter(Context paramContext, List<String> paramList) {
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
            convertView = inflater.inflate(R.layout.item_bean_text, null);
            viewHolder.tv_time = ((TextView) convertView.findViewById(R.id.tv_time));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_time.setText(appInfos.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView tv_time;
    }
}
