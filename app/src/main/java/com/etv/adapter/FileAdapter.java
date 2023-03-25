package com.etv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.etv.util.image.glide.GlideImageUtil;
import com.ys.model.entity.FileEntity;
import com.ys.etv.R;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileEntity> items;
    private List<String> mSelectedList;
    private boolean isMultiple;
    LayoutInflater mInflater;

    public FileAdapter(Context context, List<FileEntity> items) {
        this.items = items;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mSelectedList = new ArrayList<>();
    }

    public void setItems(List<FileEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items != null && items.size() > 0) {
            return items.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (items != null && items.size() > 0) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        recive(holder);
        FileEntity info = items.get(position);

        String fileName = info.getFileName();
        if (fileName.length() > 15) {
            fileName = "..." + fileName.substring(fileName.length() - 10, fileName.length());
        }
        holder.textview.setText(fileName);
        if (info.getFileStyle() == FileEntity.FILE_STYLE_DIR) {  //文件夹
            uopdateImage(R.mipmap.icon_file, holder.imageview);
            holder.tv_file_length.setText("");
        } else if (info.getFileStyle() == FileEntity.FILE_STYLE_FILE) {  //文件
            setBackGround(info, holder.imageview);
            setFileSize(info, holder.tv_file_length);
        }
        holder.checkBox.setChecked(mSelectedList.contains(position + ""));
        return convertView;
    }

    private void recive(ViewHolder holder) {
        if (isMultiple) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.tv_file_length.setText("");
        holder.imageview.setBackgroundResource(R.color.transparent);
        holder.textview.setText("");
    }

    private void setFileSize(FileEntity info, TextView tv_file_length) {
        long fileSize = info.getFileSize() / 1000;
        String sizeName = fileSize + " KB";
        if (fileSize < 1) {
            sizeName = info.getFileSize() + " B";
        }
        if (fileSize > 1000) {
            fileSize = fileSize / 1000;
            sizeName = fileSize + " M";
        }
        tv_file_length.setText(sizeName);
    }

    public void setBackGround(FileEntity info, final ImageView imageView) {
        int fileStyle = info.getSTYLE_FILE();
        if (fileStyle == FileEntity.STYLE_FILE_IMAGE) {
            String filePath = "file://" + info.getFilePath();
            uopdateImage(filePath, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_MUSIC) {
            uopdateImage(R.mipmap.file_icon_music, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_VIDEO) {
            //这里改成默认的图标，不获取缩略图，因为rmvb格式的获取不到图片，会显示为空
            uopdateImage(R.mipmap.file_icon_video, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_ZIP) {  //zip格式
            uopdateImage(R.mipmap.file_icon_zip, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_APK) {  //apk格式
            uopdateImage(R.mipmap.icon_file_apk, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_PPT) {  //ppt格式
            uopdateImage(R.mipmap.file_icon_ppt, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_DOC) {  //word格式
            uopdateImage(R.mipmap.file_icon_word, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_EXCEL) {  //excel格式
            uopdateImage(R.mipmap.file_icon_excel, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_PDF) {  //pdf格式
            uopdateImage(R.mipmap.file_icon_pdf, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_TXT) {  //txt格式
            uopdateImage(R.mipmap.file_icon_txt, imageView);
        } else if (fileStyle == FileEntity.STYLE_FILE_OTHER) {  //其他类型格式
            uopdateImage(R.mipmap.file_icon_default, imageView);
        } else {
            uopdateImage(R.mipmap.file_icon_default, imageView);
        }
    }

    public void uopdateImage(String imageId, ImageView imageView) {
        GlideImageUtil.loadImageByPath(mContext, imageId, imageView);
    }

    public void uopdateImage(int imageId, ImageView imageView) {
        GlideImageUtil.loadImageById(mContext, imageId, imageView);
    }

    private static class ViewHolder {
        private final TextView tv_file_length;
        private ImageView imageview;
        private TextView textview;
        private CheckBox checkBox;

        public ViewHolder(View v) {
            imageview = (ImageView) v.findViewById(R.id.iv_icon_main);
            textview = (TextView) v.findViewById(R.id.tv_file_name);
            tv_file_length = (TextView) v.findViewById(R.id.tv_file_length);
            checkBox = (CheckBox) v.findViewById(R.id.if_checkbox);
        }
    }
}
