package com.etv.http.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.etv.listener.WriteSdListener;
import com.etv.util.FileUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


/***
 * 将文件写入SD卡中
 */
public class FileRawWriteRunnable implements Runnable {

    int rawId;
    String savePath;
    long fileLength;
    WriteSdListener listener;
    Context context;
    Handler handler = new Handler();
    boolean isDelOldFile = false;

    public void setIdDelOldFile(boolean isDelOldFile) {
        this.isDelOldFile = isDelOldFile;
    }

    public FileRawWriteRunnable(Context context, int rawId, String savePath, long fileLength, WriteSdListener listener) {
        this.context = context;
        this.rawId = rawId;
        this.savePath = savePath;
        this.fileLength = fileLength;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            FileUtil.creatPathNotExcit("raw文件添加到 sdcard ");
            File fileSave = new File(savePath);
            if (fileSave.exists()) {
                if (isDelOldFile) {
                    fileSave.delete();
                } else {
                    long fileSaveLength = fileSave.length();
                    if (Math.abs(fileLength - fileSaveLength) < 10240) {  //如果文件内存满足调教的话
                        Log.i("write", "=========文件合法 ，直接去播放就好了===================");
                        backSuccess(savePath);
                        return;
                    }
                    Log.i("write", "=========文件不合法 删除文件===================");
                    fileSave.delete();
                }
            }
            fileSave.createNewFile();
            Log.i("write", "=========开始写入===================");
            InputStream inputStream = context.getResources().openRawResource(rawId);
            // 1.建立通道对象
            FileOutputStream fos = new FileOutputStream(fileSave);
            // 2.定义存储空间
            byte[] buffer = new byte[inputStream.available()];
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
                listener.writeProgress(prgress);
            }
        });
    }


}
