package com.etv.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.etv.config.AppInfo;
import com.etv.entity.BeanEntity;
import com.etv.util.SharedPerManager;
import com.ys.etv.R;

import java.util.List;

public class SingleShowAdapter extends BaseAdapter {

    Context context;
    List<BeanEntity> list;
    LayoutInflater inflater;
    String screenPosition = AppInfo.PROGRAM_POSITION_MAIN;
    boolean isHroVer;

    public SingleShowAdapter(Context context, List<BeanEntity> list, String screenPosition, boolean isHroVer) {
        this.context = context;
        this.list = list;
        this.screenPosition = screenPosition;
        this.isHroVer = isHroVer;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<BeanEntity> list) {
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
            if (isHroVer) {
                view = inflater.inflate(R.layout.item_single_setting, null);
            } else {
                view = inflater.inflate(R.layout.item_single_setting_ver, null);
            }
            viewHolder.iv_country_item = (ImageView) view.findViewById(R.id.iv_country_item);
            viewHolder.iv_check = (ImageView) view.findViewById(R.id.iv_check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        BeanEntity entity = list.get(i);
        int idTag = entity.getTagId();
        int saveLayoutId = -1;
        if (screenPosition.contains(AppInfo.PROGRAM_POSITION_MAIN)) { //主屏
            saveLayoutId = SharedPerManager.getSingleLayoutTag();
        } else if (screenPosition.contains(AppInfo.PROGRAM_POSITION_SECOND)) {
            saveLayoutId = SharedPerManager.getSingleSecondLayoutTag();
        }
        if (idTag == saveLayoutId) {
            viewHolder.iv_check.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_check.setVisibility(View.INVISIBLE);
        }
        viewHolder.iv_country_item.setBackgroundResource(entity.getImageId());
        return view;
    }

    class ViewHolder {
        ImageView iv_country_item;
        ImageView iv_check;
    }
}
