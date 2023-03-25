package com.etv.http.util;

import android.os.Handler;

import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SingleTaskEntity;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.ys.model.entity.FileEntity;
import com.ys.model.util.FileMatch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetMediaListFromPathNewRunnable implements Runnable {

    String filePath;
    GetSingleTaskEntityListener listener;
    private Handler handler = new Handler();

    public GetMediaListFromPathNewRunnable(String filePath, GetSingleTaskEntityListener listener) {
        this.filePath = filePath;
        this.listener = listener;
    }

    @Override
    public void run() {
        File file = new File(filePath);
        if (!file.exists()) {
            backFileListToViewNew(false, null, "File is not exists");
            return;
        }
        List<File> listFileSearch = new ArrayList<>();
        getFilesFromPath(filePath, listFileSearch);
        if (listFileSearch == null || listFileSearch.size() < 1) {
            backFileListToViewNew(false, null, "File is not exists");
            return;
        }
        //去分发遍历的文件
        List<MediAddEntity> mediAddEntityList = dispatchFileToList(listFileSearch);
        if (mediAddEntityList == null || mediAddEntityList.size() < 1) {
            backFileListToViewNew(false, null, "File is not exists");
            return;
        }
        chooiceFileToList(mediAddEntityList);
    }

    /**
     * 分配文件属性信息
     *
     * @param mediAddEntityList
     */
    private void chooiceFileToList(List<MediAddEntity> mediAddEntityList) {
        List<MediAddEntity> list_image = new ArrayList<>();   //包含所有的图片
        List<MediAddEntity> list_video = new ArrayList<>();   //包含所有的视频
        List<MediAddEntity> list_doc = new ArrayList<>();     //包含所有的文档
        List<MediAddEntity> list_music = new ArrayList<>();     //包含所有的文档
        List<MediAddEntity> listsEntity = new ArrayList<>();  //包含所有的信息


        List<MediAddEntity> list_image_double = new ArrayList<>();   //副屏包含所有的图片
        List<MediAddEntity> list_video_double = new ArrayList<>();   //副屏包含所有的视频
        List<MediAddEntity> list_doc_double = new ArrayList<>();     //包含所有的文档
        List<MediAddEntity> list_music_double = new ArrayList<>();     //包含所有的文档
        List<MediAddEntity> listsEntity_double = new ArrayList<>();  //副屏包含所有的信息

        for (int i = 0; i < mediAddEntityList.size(); i++) {
            MediAddEntity mediAddEntity = mediAddEntityList.get(i);
            String filePath = mediAddEntity.getUrl();
            int style_file = FileMatch.fileMatch(filePath);
            if (filePath.contains("/double/")) { //副屏
                listsEntity_double.add(mediAddEntity);
                if (style_file == FileEntity.STYLE_FILE_IMAGE) {
                    list_image_double.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_VIDEO) {
                    list_video_double.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_MUSIC) {
                    list_music_double.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_PDF) {
                    list_doc_double.add(mediAddEntity);
                }
            } else { //主屏
                listsEntity.add(mediAddEntity);
                if (style_file == FileEntity.STYLE_FILE_IMAGE) {
                    list_image.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_VIDEO) {
                    list_video.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_MUSIC) {
                    list_music.add(mediAddEntity);
                } else if (style_file == FileEntity.STYLE_FILE_PDF) {
                    list_doc.add(mediAddEntity);
                }
            }
        }
        SingleTaskEntity singleTaskEntity = new SingleTaskEntity();
        singleTaskEntity.setListsEntity(listsEntity);
        singleTaskEntity.setList_image(list_image);
        singleTaskEntity.setList_video(list_video);
        singleTaskEntity.setList_doc(list_doc);
        singleTaskEntity.setList_music(list_music);

        singleTaskEntity.setList_image_double(list_image_double);
        singleTaskEntity.setList_video_double(list_video_double);
        singleTaskEntity.setList_doc_double(list_doc_double);
        singleTaskEntity.setList_music_double(list_music_double);
        singleTaskEntity.setListsEntity_double(listsEntity_double);
        MyLog.cdl("==========检索完成后，准备回调====");
        backFileListToViewNew(true, singleTaskEntity, "检索设备成功");
    }

    private List<MediAddEntity> dispatchFileToList(List<File> listFileSearch) {
        List<MediAddEntity> mediAddEntityList = new ArrayList<MediAddEntity>();
        for (File file : listFileSearch) {
            if (file.isDirectory()) {
                continue;
            }
            String filePath = file.getAbsolutePath();
            long fileSize = file.length();
            MediAddEntity entity = new MediAddEntity();

            int cartonAnim = SharedPerManager.getSinglePicAnimiType();    //动画特效
            int videoNum = SharedPerManager.getSingleVideoVoiceNum();      //声音得大小
            int backMusicNum = SharedPerManager.getSingleBackVoiceNum();   //背景音的大小
            int picDistanceTime = SharedPerManager.getPicDistanceTime();   //图片切换得间隔时间
            int wpsDistanceTime = SharedPerManager.getWpsDistanceTime();  //文档切换得间隔时间
            int style_file = FileMatch.fileMatch(filePath);
            if (style_file == FileEntity.STYLE_FILE_IMAGE) {
                entity.setFileType(FileEntity.STYLE_FILE_IMAGE);
                entity.setPlayParam(picDistanceTime + "");
            } else if (style_file == FileEntity.STYLE_FILE_VIDEO) {
                entity.setFileType(FileEntity.STYLE_FILE_VIDEO);
                entity.setVolNum(videoNum + "");
                entity.setPlayParam(picDistanceTime + "");
            } else if (style_file == FileEntity.STYLE_FILE_MUSIC) {
                entity.setFileType(FileEntity.STYLE_FILE_MUSIC);
                entity.setVolNum(backMusicNum + "");
                entity.setPlayParam(picDistanceTime + "");
            } else if (style_file == FileEntity.STYLE_FILE_PDF) {
                entity.setFileType(FileEntity.STYLE_FILE_PDF);
                entity.setPlayParam(wpsDistanceTime + "");
            }
            entity.setFileSize(fileSize);
            entity.setUrl(filePath);
            entity.setCartoon(cartonAnim + "");
            mediAddEntityList.add(entity);
        }
        return mediAddEntityList;
    }


    /**
     * 从Single文件夹里面获取所有的文件
     *
     * @param path
     * @param liftFile
     */
    public void getFilesFromPath(String path, List<File> liftFile) {
        File file = new File(path);
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        getFilesFromPath(files[i].getPath(), liftFile);
                    } else {
                        liftFile.add(files[i]);
                    }
                }
            } else {
                liftFile.add(file);
            }
        } catch (Exception e) {
            backFileListToViewNew(false, null, "ERROR :" + e.toString());
            e.printStackTrace();
        }
    }


    private void backFileListToViewNew(final boolean isTrue, final SingleTaskEntity singleTaskEntity, final String errorDesc) {
        if (listener == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                MyLog.cdl("完成，回调回去==========");
                listener.backTaskEntity(isTrue, singleTaskEntity, errorDesc);
            }
        });
    }


    public interface GetSingleTaskEntityListener {

        void backTaskEntity(boolean isTrue, SingleTaskEntity singleTaskEntity, String errorDesc);

    }

}
