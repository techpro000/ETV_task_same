package com.etv.http.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.etv.config.AppInfo;
import com.etv.util.MyLog;
import com.etv.util.image.CaptureImageListener;
import com.etv.util.image.zip.Luban;
import com.etv.util.image.zip.OnCompressListener;

import java.io.File;
import java.io.OutputStream;

/**
 * 系统截图压缩
 */
public class ZipImageRunnable implements Runnable {

    Context context;
    CaptureImageListener listener;
    String filePath;

    /**
     * 截图工具
     *
     * @param context
     * @param filePath 图片文件保存路径
     * @param listener 图片上传监听
     */
    public ZipImageRunnable(Context context, String filePath, CaptureImageListener listener) {
        this.context = context;
        this.listener = listener;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        MyLog.cdl("ZipImageRunnable利用系统截图，速度快", true);
        zipImagePath(filePath);
    }

    private void zipImagePath(String savePath) {
        MyLog.update("===================ZipImageRunnable开始压缩==" + System.currentTimeMillis() + " / imagePath==" + savePath);
        File file = new File(savePath);
        if (!file.exists()) {
            MyLog.update("===================ZipImageRunnable 文件不存在==");
            return;
        }
        MyLog.update("====ZipImageRunnable压缩前文件的大小==" + file.length());
        if (file.length() < (1024 * 60)) {  //如果图片<400KB,不用压缩，直接返回
            MyLog.update("===================ZipImageRunnable不用压缩==" + System.currentTimeMillis());
            Message message = new Message();
            message.what = MESSAGE_SUCCESS;
            message.obj = savePath;
            handler.sendMessage(message);
            return;
        }
        Luban.with(context)
                .load(savePath)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                     // 忽略不压缩图片的大小
                .setTargetDir(AppInfo.BASE_CACHE())               // 设置压缩后文件存储位置，文件夹路径
                .setCompressListener(new OnCompressListener() {   //设置回调
                    @Override
                    public void onStart() {
                        MyLog.update("========ZipImageRunnable图片开始压缩=====");
                    }

                    @Override
                    public void onSuccess(String savePath) {
                        Message message = new Message();
                        message.what = MESSAGE_SUCCESS;
                        message.obj = savePath;
                        handler.sendMessage(message);
                        MyLog.update("===================ZipImageRunnable压缩success==" + System.currentTimeMillis());
                    }

                    @Override
                    public void onError(Throwable e) {
                        handler.sendEmptyMessage(MESSAGE_FAILED);
                        MyLog.update("===================ZipImageRunnable图片压缩failed==" + System.currentTimeMillis());
                    }
                }).launch();    //启动压缩
    }


    private static final int MESSAGE_SUCCESS = 786;
    private static final int MESSAGE_FAILED = 787;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener == null) {
                return;
            }
            if (msg.what == MESSAGE_FAILED) {
                listener.getCaptureImagePath(false, "");
            } else if (msg.what == MESSAGE_SUCCESS) {
                String savePath = (String) msg.obj;
                listener.getCaptureImagePath(true, savePath);
            }
        }
    };


}
