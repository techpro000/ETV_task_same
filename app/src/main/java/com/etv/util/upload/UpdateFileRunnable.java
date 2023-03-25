package com.etv.util.upload;

import com.etv.util.CodeUtil;
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

/***
 * 文件上传线程
 */
public class UpdateFileRunnable implements Runnable {

    String requestUtl;
    String filePath;
    UpdateImageListener listener;

    public static final int TAG_FILE = 1;
    public static final int TAG_IMAGE = 2;
    int updateTag = TAG_FILE;
    int disPlayPosition = 1;

    public UpdateFileRunnable(String requestUtl, String filePath) {
        this.requestUtl = requestUtl;
        this.filePath = filePath;
    }

    public UpdateFileRunnable(String requestUtl, String filePath, int updateTag, UpdateImageListener listener) {
        this.requestUtl = requestUtl;
        this.filePath = filePath;
        this.listener = listener;
        this.updateTag = updateTag;
    }

    /**
     * 设置屏幕的位置
     *
     * @param position
     */
    public void setImagePosition(int position) {
        this.disPlayPosition = position;
    }

    @Override
    public void run() {
        photoUpload();
    }

    public void photoUpload() {
        HttpUtils utils = new HttpUtils(50000); // 设置连接超时
        String fileName = CodeUtil.getUniquePsuedoID();
        MyLog.cdl("=====fineName = " + fileName);
        RequestParams params = new RequestParams();
        String token = SimpleDateUtil.formatBig(System.currentTimeMillis()) + "";
        params.addHeader("token", token);
        if (updateTag == TAG_FILE) {  //文件
            params.addBodyParameter("fileType", "7");
            params.addBodyParameter("fileName", fileName);
            params.addBodyParameter("isUpload", "1");   //   isUpload 1上传 0不上传
        } else if (updateTag == TAG_IMAGE) { //图片
            MyLog.update("=====图片参数==" + disPlayPosition);
            params.addBodyParameter("displayPos", disPlayPosition + "");
        }
        File file = new File(filePath);
        params.addBodyParameter("files", file, MIME.ENC_BINARY);

        utils.send(HttpMethod.POST, requestUtl, params,
                new RequestCallBack() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        MyLog.update("===上传进度===" + current + " / " + total);
                        if (total < 1) {
                            total = 1;
                        }
                        if (current < 1) {
                            current = 1;
                        }
                        int progress = (int) (current * 100 / total);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageProgress(progress);
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        MyLog.update("上传成功==000=" + arg0.result.toString());
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传成功");
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        MyLog.update("上传日志失败==" + arg0 + ":" + arg1);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传失败:" + arg0.toString());
                    }
                });
    }

}
