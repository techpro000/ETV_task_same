package com.etv.http.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.etv.config.AppInfo;
import com.etv.listener.WriteBitmapToLocalListener;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.image.CaptureImageListener;
import com.etv.util.image.ImageRotateUtil;
import com.etv.util.image.zip.Luban;
import com.etv.util.image.zip.OnCompressListener;

import java.io.File;
import java.io.OutputStream;

/**
 * 截图
 */
public class CaptureRunnable implements Runnable {

    Context context;
    CaptureImageListener listener;

    /**
     * 截图工具
     *
     * @param context
     * @param listener 图片上传监听
     */
    public CaptureRunnable(Context context, CaptureImageListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        MyLog.cdl("利用API截图，速度慢", true);
        captuenPic();
    }

    private void captuenPic() {
        String picPath = AppInfo.CAPTURE_SECOND;
        MyLog.update("===================开始截图==" + System.currentTimeMillis());
        try {
            File dirFile = new File(picPath);
            if (!dirFile.exists()) {
                dirFile.createNewFile();
            }
            //请求root权限
            Process su = Runtime.getRuntime().exec("su");
            //以下两句代表重启设备
            // String strcmd = "/system/bin/screencap -p " + picPath;
            //0 主屏  1 副屏
            String strcmd = "screencap -d 1 -p " + picPath;
            strcmd = strcmd + "\n exit\n";
            OutputStream os = su.getOutputStream();
            os.write(strcmd.getBytes());
            os.flush();
            os.close();
            if ((su.waitFor() != 0)) {
                throw new SecurityException();
            }
            MyLog.update("===================截图结束==" + System.currentTimeMillis());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    raateImageFilePath(picPath);
//                    zipImagePath(picPath);
                }
            }, 500);
        } catch (Exception e) {
            MyLog.update("===截图失败==" + e.getMessage());
            handler.sendEmptyMessage(MESSAGE_FAILED);
            e.printStackTrace();
        }
    }

    ImageRotateUtil imageRotateUtil;

    private void raateImageFilePath(String picPath) {
        if (imageRotateUtil == null) {
            imageRotateUtil = new ImageRotateUtil(context);
        }
        int roateNum = SharedPerManager.getDoubleScreenRoateImage();
        imageRotateUtil.rotateBitmapByRoate(picPath, roateNum, new WriteBitmapToLocalListener() {
            @Override
            public void writeStatues(boolean isSuccess, String path) {
                if (isSuccess) {
                    zipImagePath(path);
                }
                MyLog.update("=======旋转图片====" + isSuccess + " / " + path);
            }
        });
    }

    private void zipImagePath(String savePath) {
        MyLog.update("===================开始压缩==" + System.currentTimeMillis() + " / imagePath==" + savePath);
        File file = new File(savePath);
        MyLog.update("====压缩前文件的大小==" + file.length());
        if (file.length() < (1024 * 100)) {  //如果图片<400KB,不用压缩，直接返回
            MyLog.update("===================不用压缩==" + System.currentTimeMillis());
            Message message = new Message();
            message.what = MESSAGE_SUCCESS;
            message.obj = savePath;
            handler.sendMessage(message);
            return;
        }
        Luban.with(context)
                .load(savePath)                                   // 传人要压缩的图片列表
                .ignoreBy(30)                                     // 忽略不压缩图片的大小
                .setTargetDir(AppInfo.BASE_CACHE())               // 设置压缩后文件存储位置，文件夹路径
                .setCompressListener(new OnCompressListener() {   //设置回调
                    @Override
                    public void onStart() {
                        MyLog.update("========图片开始压缩=====");
                    }

                    @Override
                    public void onSuccess(String savePath) {
                        Message message = new Message();
                        message.what = MESSAGE_SUCCESS;
                        message.obj = savePath;
                        handler.sendMessage(message);
                        MyLog.update("===================压缩success==" + System.currentTimeMillis());
                    }

                    @Override
                    public void onError(Throwable e) {
                        handler.sendEmptyMessage(MESSAGE_FAILED);
                        MyLog.update("===================图片压缩failed==" + System.currentTimeMillis());
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
