package com.ys.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ys.model.R;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.AdapterItemClickListener;

import java.util.List;

public class RadioItemAdapter extends BaseAdapter {

    private Context context;
    private List<RedioEntity> list;
    private LayoutInflater layoutInflater;
    private int checkPosition;

    public void setRadioList(List<RedioEntity> listRadio, int checkPosition) {
        this.list = listRadio;
        this.checkPosition = checkPosition;
        notifyDataSetChanged();
    }


    public RadioItemAdapter(Context context, List<RedioEntity> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list == null || list.size() < 1) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.item_radio_dialog, null);
            viewHolder.iv_chooice = view.findViewById(R.id.iv_chooice);
            viewHolder.tv_radio_desc = view.findViewById(R.id.tv_radio_desc);
            viewHolder.view_click = view.findViewById(R.id.view_click);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RedioEntity redioEntity = list.get(position);
        String radioText = redioEntity.getRadioText();
        if (position == checkPosition) {
            viewHolder.iv_chooice.setBackgroundResource(R.mipmap.radio_chooice);
        } else {
            viewHolder.iv_chooice.setBackgroundResource(R.mipmap.radio_default);
        }
        viewHolder.tv_radio_desc.setText(radioText);
        viewHolder.view_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterItemClickListener == null) {
                    return;
                }
                adapterItemClickListener.adapterItemClick(redioEntity, position);
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView iv_chooice;
        TextView tv_radio_desc;
        View view_click;
    }

    AdapterItemClickListener adapterItemClickListener;

    public void setAdapterItemClickListener(AdapterItemClickListener adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

}
