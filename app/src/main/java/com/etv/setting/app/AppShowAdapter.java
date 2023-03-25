package com.etv.setting.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.etv.entity.AppInfomation;
import com.etv.util.FileUtil;
import com.ys.etv.R;

import java.util.List;

public class AppShowAdapter extends BaseAdapter {

    List<AppInfomation> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<AppInfomation> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public AppShowAdapter(Context paramContext, List<AppInfomation> paramList) {
        this.context = paramContext;
        this.appInfos = paramList;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.item_app_show, null);
            viewHolder.iv_app_icon = ((ImageView) convertView.findViewById(R.id.iv_app_icon));
            viewHolder.tv_package_name = ((TextView) convertView.findViewById(R.id.tv_package_name));
            viewHolder.tv_name = ((TextView) convertView.findViewById(R.id.tv_name));
            viewHolder.tv_version_code = ((TextView) convertView.findViewById(R.id.tv_version_code));
            viewHolder.tv_install_position = ((TextView) convertView.findViewById(R.id.tv_install_position));

            viewHolder.btn_open_apk = ((Button) convertView.findViewById(R.id.btn_open_apk));
            viewHolder.btn_uninstall_apk = ((Button) convertView.findViewById(R.id.btn_uninstall_apk));
            viewHolder.btn_back_apk = ((Button) convertView.findViewById(R.id.btn_back_apk));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AppInfomation entty = appInfos.get(position);
        Drawable drawable = entty.getIcon();
        if (drawable != null) {
            viewHolder.iv_app_icon.setBackgroundDrawable(drawable);
        } else {
            viewHolder.iv_app_icon.setBackgroundResource(R.mipmap.ic_launcher);
        }
        Log.e("cdl", "====" + entty.toString());
        String packageName = entty.getPackageName();
        viewHolder.tv_package_name.setText("Package: " + packageName);
        viewHolder.tv_name.setText("Name: " + entty.getName());

        long fileSize = entty.getApkSize();
        String fileSizeShow = FileUtil.getPrintSize(fileSize);

        viewHolder.tv_version_code.setText("Version: " + fileSizeShow + " | " + entty.getVersionCode() + "_" + entty.getVersionName());
        String dataPath = entty.getInstallPath();
        viewHolder.tv_install_position.setText("Position: " + dataPath);

        viewHolder.btn_open_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lisenter == null) {
                    return;
                }
                lisenter.clickSure(entty, v);
            }
        });
        viewHolder.btn_uninstall_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lisenter == null) {
                    return;
                }
                lisenter.clickSure(entty, v);
            }
        });
        viewHolder.btn_back_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lisenter == null) {
                    return;
                }
                lisenter.clickSure(entty, v);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        Button btn_open_apk;
        Button btn_uninstall_apk;
        Button btn_back_apk;
        ImageView iv_app_icon;
        TextView tv_package_name;
        TextView tv_name;
        TextView tv_version_code;
        TextView tv_install_position;
    }

    AdapterAppManagerClickListener lisenter;

    public void setAdapterClickLisenter(AdapterAppManagerClickListener lisenter) {
        this.lisenter = lisenter;
    }

    public interface AdapterAppManagerClickListener {
        void clickSure(Object object, View view);
    }

}
