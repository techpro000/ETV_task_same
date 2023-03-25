package com.etv.http.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.etv.listener.CompressImageListener;
import com.etv.util.image.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片压缩
 */
public class CompressImageRunnable implements Runnable {

    File fileCompress;
    CompressImageListener listener;
    float width;
    float height;
    int quality;

    public CompressImageRunnable(File fileCompress, float width, float height, int quality, CompressImageListener listener) {
        this.fileCompress = fileCompress;
        this.listener = listener;
        this.width = width;
        this.height = height;
        this.quality = quality;
    }

    @Override
    public void run() {
        compressImage();
    }

    public void compressImage() {
        if (!fileCompress.exists()) {
            backFailed("图片文件不存在");
            return;
        }
        String imagePath = fileCompress.getPath();
        String basePath = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
        String fileName = fileCompress.getName();
        Bitmap bitmap = bitmapFactory(imagePath);
        if (bitmap == null) {
            backFailed("解析图片失败");
            return;
        }
        String nameCache = fileName.substring(0, fileName.indexOf(".")) + "_compress.jpg";
        String newPath = basePath + nameCache;
        boolean isSave = saveBitmapToSdcard(newPath, bitmap);
        if (!isSave) {
            return;
        }
        backSuccess(newPath);
    }

    /**
     * 保存方法
     */
    public boolean saveBitmapToSdcard(String path, Bitmap bitmap) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            String messageError = e.toString();
            backFailed(messageError);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩图片使用,采用BitmapFactory.decodeFile。这里是尺寸压缩
     */
    private Bitmap bitmapFactory(String imagePath) {
        Bitmap bm = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; //获取当前图片的边界大小，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeFile(imagePath, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = ImageUtil.caculateSampleSize(options, width, height);
            bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
        } catch (Exception e) {
            listener.backErrorDesc(e.toString());
            e.printStackTrace();
        }
        return bm;
    }

    private void backSuccess(final String imagePath) {
        try {
            if (listener == null) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String oldPath = fileCompress.getPath();
                    listener.backImageSuccess(oldPath, imagePath);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

    private void backFailed(final String desc) {
        try {
            if (listener == null) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.backErrorDesc(desc);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
