package com.etv.http.util;

import android.graphics.Bitmap;
import android.os.Handler;

import com.etv.listener.WriteBitmapToLocalListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapWriteLocalRunnable implements Runnable {

    Bitmap bmp;
    String savePath;
    WriteBitmapToLocalListener listener;
    private Handler handler = new Handler();

    public BitmapWriteLocalRunnable(Bitmap bmp, String savePath, WriteBitmapToLocalListener listener) {
        this.bmp = bmp;
        this.savePath = savePath;
        this.listener = listener;
    }

    @Override
    public void run() {
        saveImageToGallery();
    }

    public void saveImageToGallery() {
        if (bmp == null) {
            backImageInfo(false, "bmp==null");
            return;
        }
        FileOutputStream fos = null;
        try {
            File file = new File(savePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            backImageInfo(true, savePath);
        } catch (Exception e) {
            backImageInfo(false, e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void backImageInfo(boolean isTrue, String desc) {
        if (listener == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.writeStatues(isTrue, desc);
                }
            }
        });
    }

}
