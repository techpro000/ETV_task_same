package com.etv.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.etv.http.util.CompressImageRunnable;
import com.etv.listener.CompressImageListener;
import com.etv.service.EtvService;
import com.ys.model.entity.FileEntity;
import com.ys.model.util.FileMatch;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

import java.io.File;

/**
 * 图片压缩工具类
 */
public class CompressImageUtil {

    Context context;

    public CompressImageUtil(Context context) {
        this.context = context;
    }

    public void compressPic(String path, CompressImageListener listener) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                listener.backErrorDesc("检测文件不存在");
                return;
            }
            String filePath = file.getPath();
            int fileType = FileMatch.fileMatch(filePath);
            if (fileType != FileEntity.STYLE_FILE_IMAGE) {
                listener.backErrorDesc("该文件不是图片");
                return;
            }
            float width = SharedPerManager.getScreenWidth();
            float height = SharedPerManager.getScreenHeight();
            Bitmap mapChange = BitmapFactory.decodeFile(path);
            float imageWidth = mapChange.getWidth();
            float imageHeight = mapChange.getHeight();
            if (imageWidth < width && imageHeight < height) { //不需要转码直接返回
                listener.backImageSuccess(null, path);
                return;
            }
            while (imageWidth > width || imageHeight > height) {
                imageWidth = imageWidth * 3 / 4;
                imageHeight = imageHeight * 3 / 4;
            }
            MyLog.cdl("===图片压缩的尺寸===" + imageWidth + " / " + imageHeight);
            CompressImageRunnable runnable = new CompressImageRunnable(file, imageWidth, imageHeight, 100, listener);
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
