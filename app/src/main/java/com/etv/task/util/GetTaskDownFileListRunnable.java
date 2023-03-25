package com.etv.task.util;

import android.os.Handler;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.etv.task.db.DBTaskUtil;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskDownEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.task.model.TaskGetDownListListener;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;

import java.util.ArrayList;
import java.util.List;

public class GetTaskDownFileListRunnable implements Runnable {

    private TaskGetDownListListener listListener;
    private Handler handler = new Handler();

    //用来遍历数据得
    private List<TaskWorkEntity> taskWorkEntities;

    public GetTaskDownFileListRunnable(List<TaskWorkEntity> taskWorkEntities, TaskGetDownListListener listListener) {
        MyLog.taskDown("====000开始检索需要下载得素材====");
        this.taskWorkEntities = taskWorkEntities;
        this.listListener = listListener;
    }

    @Override
    public void run() {
        if (taskWorkEntities == null || taskWorkEntities.size() < 1) {
            MyLog.taskDown("====000没有需要下载得资源，直接返回==taskWorkEntities==00==");
            backTaskFileListToView(-1);
            return;
        }
        downUrlList.clear();
        //添加素材到集合
        List<MpListEntity> mpListEntities = DBTaskUtil.getMpListInfoAll();
        if (mpListEntities != null || mpListEntities.size() > 0) {
            addMpEntityToList(mpListEntities);
        }
        //添加背景图
        List<SceneEntity> sceneEntityList = DBTaskUtil.getSencenListInfoAll();
        if (sceneEntityList != null || sceneEntityList.size() > 0) {
            addBackImageToDownList(sceneEntityList);
        }
        //添加button背景图
        List<TextInfo> textInfoList = DBTaskUtil.getTxtListInfoAll();
        if (textInfoList != null || textInfoList.size() > 0) {
            addFontDownInfoToList(textInfoList);
        }
        backTaskFileListToView(1);
    }

    /***
     * 增加需要下载得字体
     * @param textInfoList
     */
    private void addFontDownInfoToList(List<TextInfo> textInfoList) {
        if (textInfoList == null || textInfoList.size() < 1) {
            return;
        }
        try {
            for (int i = 0; i < textInfoList.size(); i++) {
                TextInfo textInfo = textInfoList.get(i);
                String taskId = textInfo.getTaskId();
                String taBgImagePath = textInfo.getTaBgImage().toString();
                if (taBgImagePath != null && taBgImagePath.length() > 2) {
                    String downPath = ApiInfo.getIpHostWebPort() + "/" + taBgImagePath;
                    String saveFilePath = TaskDealUtil.getSavePath(taBgImagePath);
                    String fileLength = textInfo.getTaBgimageSize();
                    boolean isFileExice = FileUtil.ifFileHasExict(saveFilePath, fileLength);
                    MyLog.cdl("======buttom==文件师傅存在===" + isFileExice);
                    TaskDownEntity entitySave = new TaskDownEntity(taskId, downPath, saveFilePath, fileLength, isFileExice);
                    addDownTaskFileToList(entitySave);
                }
                String fontType = textInfo.getTaFonType();
                if (fontType == null || fontType.length() < 1) {
                    continue;
                }
                int fontTypeNum = Integer.parseInt(fontType);
                if (fontTypeNum == 1) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMpEntityToList(List<MpListEntity> mpListEntities) {
        if (mpListEntities == null || mpListEntities.size() < 1) {
            return;
        }
        for (int i = 0; i < mpListEntities.size(); i++) {
            MpListEntity mpListEntity = mpListEntities.get(i);
            if (mpListEntity == null) {
                continue;
            }
            String downUrl = mpListEntity.getUrl();
            String fileLength = mpListEntity.getSize();
            String downName = downUrl.substring(downUrl.lastIndexOf("/") + 1, downUrl.length());
            String saveFilePath = AppInfo.BASE_TASK_URL() + "/" + downName;
            boolean isFileExice = FileUtil.ifFileHasExict(saveFilePath, fileLength);
            String taskId = mpListEntity.getTaskId();
//            MyLog.e("downFile", "========素材文件是否存在==" + isFileExice + " / " + saveFilePath);
            TaskDownEntity entitySave = new TaskDownEntity(taskId, downUrl, saveFilePath, fileLength, isFileExice);
            addDownTaskFileToList(entitySave);
        }
    }

    private void addBackImageToDownList(List<SceneEntity> sceneEntityList) {
        if (sceneEntityList == null || sceneEntityList.size() < 1) {
            return;
        }
        for (int i = 0; i < sceneEntityList.size(); i++) {
            SceneEntity senceEntity = sceneEntityList.get(i);
            if (senceEntity == null) {
                continue;
            }
            String backFilePath = senceEntity.getScBackImg();
            if (backFilePath == null || backFilePath.length() < 5) {
                continue;
            }
            //拼接背景得下载地址
            if (!backFilePath.startsWith("http")) {
                backFilePath = ApiInfo.getFileDownUrl() + "/" + backFilePath;
            }
            String downName = backFilePath.substring(backFilePath.lastIndexOf("/") + 1, backFilePath.length());
            String saveFilePath = AppInfo.BASE_TASK_URL() + "/" + downName;
            String fileLength = senceEntity.getScBackimgSize();
            boolean isFileExice = FileUtil.ifFileHasExict(saveFilePath, fileLength);
            String taskId = senceEntity.getTaskid();
            MyLog.e("downFile", "=====图片的存储地主===背景文件不存在==" + isFileExice + " / " + saveFilePath);
            TaskDownEntity entitySave = new TaskDownEntity(taskId, backFilePath, saveFilePath, fileLength, isFileExice);
            addDownTaskFileToList(entitySave);
        }
    }

    /**
     * 添加下载数据到集合中
     * 防止重复添加
     *
     * @param entity
     * @return
     */

    //用来封装下载列表的，返回主界面得
    List<TaskDownEntity> downUrlList = new ArrayList<TaskDownEntity>();

    private void addDownTaskFileToList(TaskDownEntity entity) {
        if (entity == null) {
            return;
        }
        if (downUrlList == null) {
            downUrlList = new ArrayList<>();
        }
        if (downUrlList.size() < 1) {
            downUrlList.add(entity);
            return;
        }
        String addDownUrl = entity.getDownUrl().trim();
        boolean isHasSave = false;
        for (int i = 0; i < downUrlList.size(); i++) {
            String downUrl = downUrlList.get(i).getDownUrl().trim();
            if (downUrl.equals(addDownUrl)) {
                isHasSave = true;
            }
        }
        if (!isHasSave && !entity.isDownOver()) {
            MyLog.task("==========需要下载的文件这里统计===" + entity.toString());
            downUrlList.add(entity);
        }
    }

    private void backTaskFileListToView(final int position) {
        if (handler == null) {
            return;
        }
        if (listListener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (position < 0) {  //处理异常了，这里直接返回
                    listListener.getTaskDownFileListFromDb(null);
                    return;
                }
                boolean ifHasDownFile = false;  //默认没有下载文件
                for (int i = 0; i < downUrlList.size(); i++) {
                    boolean isDownOver = downUrlList.get(i).isDownOver();
                    if (!isDownOver) {
                        ifHasDownFile = true;
                        break;
                    }
                }
                if (ifHasDownFile) { //有需要下载得文件
                    MyLog.taskDown("====backTaskList====需要下载得文件个数===" + downUrlList.size());
                    listListener.getTaskDownFileListFromDb(downUrlList);
                } else {
                    listListener.getTaskDownFileListFromDb(null);
                }
            }
        });
    }

//    /***
//     * 文件保存路径
//     * @param saveFilePath
//     * 下载文件路径
//     * @param fileLength
//     * @return
//     */
//    private boolean ifFileHasExict(String saveFilePath, String fileLength) {
//        fileLength = fileLength.trim();
//        if (fileLength == null || fileLength.length() < 1) {
//            fileLength = "1024";
//        }
//        File fileSave = new File(saveFilePath);
//        if (!fileSave.exists()) {
//            return false;
//        } else {  //文件存在去比对文件大小
//            long fileDownLength = fileSave.length();
//            long downFileLengthLong = Long.parseLong(fileLength);
//            long distanceSize = Math.abs(downFileLengthLong - fileDownLength);
//            if (distanceSize > (10 * 1024)) {
//                //下载的文件和保存的文件内存不对称 ,需要重新下载
//                return false;
//            }
//        }
//        return true;
//    }
}
