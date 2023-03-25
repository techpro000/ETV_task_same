package com.etv.util.upload;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.etv.util.CodeUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;
import com.etv.util.xutil.HttpUtils;
import com.etv.util.xutil.exception.HttpException;
import com.etv.util.xutil.http.RequestParams;
import com.etv.util.xutil.http.ResponseInfo;
import com.etv.util.xutil.http.callback.RequestCallBack;
import com.etv.util.xutil.http.client.multipart.MIME;
import com.etv.util.xutil.http.client.util.HttpMethod;

import java.io.File;

public class UpdateWearVideoRunnbale implements Runnable {


    UpdateImageListener listener;
    String filePath;
    String fileName;
    String recorderTime;

    public UpdateWearVideoRunnbale() {

    }

    public UpdateWearVideoRunnbale(String filePath, String fileName, String recorderTime, UpdateImageListener listener) {
        setVideoPath(filePath, fileName, recorderTime, listener);
    }

    public void setVideoPath(String filePath, String fileName, String recorderTime, UpdateImageListener listener) {
        this.filePath = filePath;
        this.listener = listener;
        this.fileName = fileName;
        this.recorderTime = recorderTime;
    }


    @Override
    public void run() {
        photoUpload();
    }

    public void photoUpload() {
        HttpUtils utils = new HttpUtils(50000); // 设置连接超时
        String requestUrl = ApiInfo.getUpdateWearVideoUrl();
        String clNo = CodeUtil.getUniquePsuedoID();
        RequestParams params = new RequestParams();
        String token = SimpleDateUtil.formatBig(System.currentTimeMillis()) + "";
        params.addHeader("token", token);
        params.addBodyParameter("clNo", clNo);
        params.addBodyParameter("fileName", fileName);
        params.addBodyParameter("recordTime", recorderTime);
        File file = new File(filePath);
        params.addBodyParameter("files", file, MIME.ENC_BINARY);
        MyLog.phone("=====上传视频==" + fileName + " / " + recorderTime);
        utils.send(HttpMethod.POST, requestUrl, params,
                new RequestCallBack() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        if (total < 1) {
                            total = 1;
                        }
                        if (current < 1) {
                            current = 1;
                        }
                        int progress = (int) (current * 100 / total);
                        MyLog.phone("===上传视频进度===" + current + " / " + total + " / " + progress);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageProgress(progress);
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        FileUtil.deleteDirOrFilePath(filePath, "上传视频文件成功，删除原有得文件");
                        MyLog.phone("上传视频成功==000=" + arg0.result.toString());
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传视频成功");
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        MyLog.phone("上传视频失败==" + arg0 + ":" + arg1);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传视频失败:" + arg0.toString());
                    }
                });
    }


}
