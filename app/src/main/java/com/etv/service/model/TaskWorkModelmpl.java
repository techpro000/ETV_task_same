package com.etv.service.model;

import android.content.Context;
import android.content.Intent;

import com.etv.config.ApiInfo;
import com.etv.db.DbBggImageUtil;
import com.etv.entity.BggImageEntity;
import com.etv.service.EtvService;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.upload.UpdateImageListener;
import com.etv.util.upload.UpdateWearVideoRunnbale;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class TaskWorkModelmpl implements TaskWorkModel {

    UpdateWearVideoRunnbale updateWearVideoRunnbale;
    List<File> updateVideoCacheList = null;

    private void updateVideoOneByOne(File file) {
        if (file == null) {
            updateNextVideoFile();
            return;
        }
        if (updateVideoCacheList != null && updateVideoCacheList.size() > 0) {
            MyLog.phone("文件上传数量：  " + updateVideoCacheList.size());
        }
        String filePath = file.getPath();
        String fileName = file.getName();
        if (updateWearVideoRunnbale == null) {
            updateWearVideoRunnbale = new UpdateWearVideoRunnbale();
        }
        String recorderTime = getCacheTime(fileName);
        updateWearVideoRunnbale.setVideoPath(filePath, fileName, recorderTime, new UpdateImageListener() {
            @Override
            public void updateImageFailed(String errorDesc) {
                updateNextVideoFile();
            }

            @Override
            public void updateImageProgress(int progress) {

            }

            @Override
            public void updateImageSuccess(String desc) {
                updateNextVideoFile();
            }
        });
        EtvService.getInstance().executor(updateWearVideoRunnbale);
    }

    private String getCacheTime(String fileName) {
        long recorderTime = System.currentTimeMillis();
        if (fileName == null || fileName.length() < 3) {
            return SimpleDateUtil.formatTaskTimeShow(recorderTime);
        }
        String timeCache = fileName.substring(0, fileName.indexOf("."));
        try {
            recorderTime = Long.parseLong(timeCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SimpleDateUtil.formatTaskTimeShow(recorderTime);
    }

    /***
     * 上传下一个视频文件
     */
    private void updateNextVideoFile() {
        if (updateVideoCacheList == null || updateVideoCacheList.size() < 1) {
            updateVideoOver();
            return;
        }
        updateVideoCacheList.remove(0);
        if (updateVideoCacheList == null || updateVideoCacheList.size() < 1) {
            updateVideoOver();
            return;
        }
        File file = updateVideoCacheList.get(0);
        updateVideoOneByOne(file);
    }

    private void updateVideoOver() {
        MyLog.phone("===文件上传完毕========happy");
    }

    /**
     * 检测背景图需不需要下载
     */
    List<BggImageEntity> bggImageEntities = null;
    private boolean isDownBggImage = false;

    @Override
    public void startToCheckBggImage(Context context) {
        FileUtil.creatPathNotExcit("检查背景图");
        if (isDownBggImage) {
            MyLog.bgg("=====下载背景图===目前还在下载，中断操作");
            return;
        }
        if (bggImageEntities != null && bggImageEntities.size() > 0) {
            bggImageEntities.clear();
        }
        bggImageEntities = DbBggImageUtil.getBggImageDownListFromDb();
        if (bggImageEntities == null || bggImageEntities.size() < 1) {
            gotoRefreshView("=====下载背景图===不需要下载=刷新界面", context);
            return;
        }
        MyLog.bgg("=====下载背景图===需要下载得图片得个数==" + bggImageEntities.size());
        startToDownFile(context);
    }


    private void startToDownFile(Context context) {
        if (bggImageEntities == null || bggImageEntities.size() < 1) {
            gotoRefreshView("========全部下载完毕了=刷新界面", context);
            return;
        }
        isDownBggImage = true;
        String downUrl = ApiInfo.getIpHostWebPort() + "/" + bggImageEntities.get(0).getImagePath();
        String saveUrl = bggImageEntities.get(0).getSavePath();
        String fileName = bggImageEntities.get(0).getImageName();
        MyLog.bgg("====下载背景图==开始下载==" + saveUrl + " / " + fileName);

        OkHttpUtils
            .get()
            .url(downUrl)
            .build()
            .execute(new FileCallBack(saveUrl, fileName) {

                @Override
                public void onBefore(Request request, int id) {
                    MyLog.bgg("====下载背景图==开始下载");
                }

                @Override
                public void inProgress(int progress, long total, int id) {
                    MyLog.bgg("====下载背景图进度==" + progress);
                }

                @Override
                public void onError(Call call, String errorMessage, int id) {
                    MyLog.bgg("====下载背景图==onError:  " + errorMessage);
                }

                @Override
                public void onResponse(File file, int id) {
                    MyLog.bgg("====下载背景图==完成:  " + file.getAbsolutePath());
                    if (bggImageEntities == null || bggImageEntities.size() < 1) {
                        gotoRefreshView("========全部下载完毕了=刷新界面", context);
                        return;
                    }
                    bggImageEntities.remove(0);
                    startToDownFile(context);
                }
            });
    }

    private void gotoRefreshView(String tag, Context context) {
        isDownBggImage = false;
        MyLog.down("=====下载背景图===" + tag);
        AppStatuesListener.getInstance().UpdateMainBggEvent.postValue("下载完毕，这里刷新界面");
    }

    public void sendBroadCastToView(String action, Context context) {
        if (context == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
