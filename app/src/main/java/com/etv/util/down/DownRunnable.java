package com.etv.util.down;

import android.os.Handler;
import android.util.Log;

import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.xutil.HttpUtils;
import com.etv.util.xutil.exception.HttpException;
import com.etv.util.xutil.http.HttpHandler;
import com.etv.util.xutil.http.ResponseInfo;
import com.etv.util.xutil.http.callback.RequestCallBack;

import java.io.File;

public class DownRunnable implements Runnable {

    private HttpHandler<File> httHhandler = null;
    String downUrl;
    String saveUrl;
    DownStateListener listener;
    HttpUtils httpUtils;
    long downProNum = 0;  //上一次下载量，用来判断下载速度
    boolean isFalse = false;
    int LimitDownSpeed = -1;
    String taskId;

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setLimitDownSpeed(int LimitDownSpeed) {
        this.LimitDownSpeed = LimitDownSpeed;
    }

    public void setIsDelFile(boolean isDelFile) {
        this.isFalse = isDelFile;
    }

    public DownRunnable(String downUrl, String saveUrl, DownStateListener listener) {
        MyLog.down("======下载地址= " + downUrl + "\n保存的地址==" + saveUrl);
        this.downUrl = downUrl;
        this.saveUrl = saveUrl;
        this.listener = listener;
        httHhandler = null;
        FileUtil.creatPathNotExcit("下载线程");
    }

    public DownRunnable() {
    }

    public void setDownInfo(String downUrl, String saveUrl, DownStateListener listener) {
        MyLog.down("======下载地址= " + downUrl + "\n保存的地址==" + saveUrl);
        this.downUrl = downUrl;
        this.saveUrl = saveUrl;
        this.listener = listener;
        httHhandler = null;
        FileUtil.creatPathNotExcit("下载线程");
    }


    @Override
    public void run() {
        //第一个参数:下载地址
        //第二个参数:文件存储路径
        //第三个参数:是否断点续传
        //第四个参数:是否重命名
        //第五个参数:请求回调
        try {
            if (httpUtils == null) {
                httpUtils = new HttpUtils();
            }
            File fileDown = new File(saveUrl);
            //如果不需要断点续传，这里可以删除
            if (isFalse) {
                if (fileDown.exists()) {
                    MyLog.down("======文件存在，删除文件");
                    fileDown.delete();
                }
            }
            fileDown.createNewFile();
            httpUtils.configRequestThreadPoolSize(3);//设置由几条线程进行下载
            httpUtils.setLimitDownSpeed(LimitDownSpeed);
            httHhandler = httpUtils.download(downUrl, saveUrl, true, true, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();
                    MyLog.down("======开始下载");
                    downProNum = 0;
                    backState("开始下载", DownFileEntity.DOWN_STATE_START, 0, true, downUrl, saveUrl, 1000, taskId);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    int progress = (int) (current * 100 / total);
                    int speed = (int) ((current - downProNum) / 1024);
                    MyLog.test("======下载进度==progress=" + progress + "   current=" + current + "/" + total + "    /speed = " + speed + " / " + saveUrl);
                    backState("下载中", DownFileEntity.DOWN_STATE_PROGRESS, progress, true, downUrl, saveUrl, speed, taskId);
                    downProNum = current;
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    MyLog.down("===下载成功===" + saveUrl);
                    backState("下载成功", DownFileEntity.DOWN_STATE_SUCCESS, 100, false, downUrl, saveUrl, 0, taskId);

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    MyLog.down("===下载失败==" + s + "   /" + e.toString(), true);
                    backState(s, DownFileEntity.DOWN_STATE_FAIED, 0, false, downUrl, saveUrl, -1, taskId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("down", "下载异常==" + e.toString());
        }
    }

    public void stopDown() {
        try {
            if (httHhandler == null) {
                return;
            }
            httHhandler.cancel();
        } catch (Exception e) {
            MyLog.cdl("=====准备停止下载,执行异常==" + e.toString(), true);
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

    private void backState(final String state, final int downState, final int progress, final boolean b,
                           final String downUrl, final String saveUrl, final int speed, final String taskId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DownFileEntity entity = new DownFileEntity();
                entity.setDesc(state);
                entity.setDownState(downState);
                entity.setDownSpeed(speed);
                entity.setProgress(progress);
                entity.setTaskId(taskId);
                entity.setDown(b);
                entity.setDownPath(downUrl);
                entity.setSavePath(saveUrl);
                listener.downStateInfo(entity);
            }
        });

    }


}
