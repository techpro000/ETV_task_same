package com.etv.http.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.etv.listener.WriteSdListener;
import com.etv.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


/***
 * 将文件写入SD卡中
 */
public class FileWriteRunnable implements Runnable {

    String filePath;
    String savePath;
    WriteSdListener listener;
    Context context;
    long fileLength;
    Handler handler = new Handler();

    public FileWriteRunnable(Context context, String filePath, String savePath, WriteSdListener listener) {
        this.context = context;
        this.filePath = filePath;
        this.savePath = savePath;
        this.listener = listener;
        File file = new File(filePath);
        if (file.exists()) {
            fileLength = file.length();
        }
    }

    @Override
    public void run() {
        try {
            FileUtil.creatPathNotExcit("文件读写线程");
            File fileSave = new File(savePath);
            if (fileSave.exists()) {
                fileSave.delete();
            }
            fileSave.createNewFile();
            Log.i("write", "=========开始写入===================");
            InputStream inputStream = new FileInputStream(filePath);
            // 1.建立通道对象
            FileOutputStream fos = new FileOutputStream(fileSave);
            // 2.定义存储空间
            byte[] buffer = new byte[8192];
//            byte[] buffer = new byte[inputStream.available()];
            // 3.开始读文件
            int lenght = 0;
            long sum = 0;
            while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                // 将Buffer中的数据写到outputStream对象中
                fos.write(buffer, 0, lenght);
                sum += lenght;
                int progress = (int) (sum * 100 / fileLength);
                backProgress(progress);
            }
            fos.flush();// 刷新缓冲区
            fos.close();
            inputStream.close();
            backSuccess(savePath);
            Log.i("write", "=========写入完毕===================");
        } catch (Exception e) {
            backFailed(e.toString());
            Log.i("write", "=========写入异常===================" + e.toString());
        }
    }

    public void backFailed(final String rrorDesc) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writrFailed(rrorDesc);
            }
        });
    }


    public void backSuccess(final String filePath) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writeSuccess(filePath);
            }
        });
    }

    public void backProgress(final int prgress) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("write", "=========写入中===================" + prgress);
                listener.writeProgress(prgress);
            }
        });
    }


}
