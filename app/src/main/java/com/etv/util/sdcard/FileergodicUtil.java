package com.etv.util.sdcard;

import android.util.Log;


import com.ys.model.entity.FileEntity;
import com.ys.model.util.FileMatch;
import com.ys.etv.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileergodicUtil {

    private static final String TAG = "FileergodicUtil";

    public static List<FileEntity> getFileList(String path) {
        if (path == null) {
            return null;
        }
        File root = new File(path);
        if (!root.exists()) {
            return null;
        }
        List<FileEntity> list = new ArrayList<FileEntity>();
        FileEntity entity;
        File[] files = root.listFiles();
        if (files.length < 1) {
            return null;
        }
        long fileSize;
        for (File file : files) {
            String fileName = file.getName();
            String filePath = file.getAbsolutePath();
            if (file.isDirectory()) { //文件夹判断
                int imageId = R.mipmap.icon_file;
                fileSize = file.length();
                Log.i(TAG, "====文件夹======>>" + fileName + "//" + fileSize);
                entity = new FileEntity(imageId, fileName, filePath, FileEntity.FILE_STYLE_DIR);
            } else {  //文件
                fileSize = file.length();
                Log.e(TAG, "====文件 ======>>" + fileName + "===" + fileSize);
                entity = new FileEntity();
                entity.setFileStyle(FileEntity.FILE_STYLE_FILE);
                entity.setFileName(fileName);
                entity.setFileSise(fileSize);
                entity.setFilePath(filePath);
                int style_file = FileMatch.fileMatch(fileName);
                if (style_file == FileEntity.STYLE_FILE_IMAGE) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_IMAGE);
                } else if (style_file == FileEntity.STYLE_FILE_MUSIC) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_MUSIC);
                } else if (style_file == FileEntity.STYLE_FILE_VIDEO) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_VIDEO);
                } else if (style_file == FileEntity.STYLE_FILE_ZIP) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_ZIP);
                } else if (style_file == FileEntity.STYLE_FILE_APK) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_APK);
                } else if (style_file == FileEntity.STYLE_FILE_PPT) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_PPT);
                } else if (style_file == FileEntity.STYLE_FILE_DOC) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_DOC);
                } else if (style_file == FileEntity.STYLE_FILE_EXCEL) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_EXCEL);
                } else if (style_file == FileEntity.STYLE_FILE_PDF) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_PDF);
                } else if (style_file == FileEntity.STYLE_FILE_TXT) {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_TXT);
                } else {
                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_OTHER);
                }
            }
            if (fileSize > 5) {  //文件夹.或者文件太小不予显示
                list.add(entity);
            }
        }
        return list;
    }

//    public static List<FileEntity> getMediaFileList(String path) {
//        List<FileEntity> list = new ArrayList<FileEntity>();
//        FileEntity entity;
//        File root = new File(path);
//        if (!root.exists()) {
//            return null;
//        }
//        File[] files = root.listFiles();
//        if (files == null) {
//            return null;
//        }
//        if (files.length < 1) {
//            return null;
//        }
//        long fileSize;
//        for (File file : files) {
//            String fileName = file.getName();
//            String filePath = file.getAbsolutePath();
//            if (file.isDirectory()) { //文件夹判断
//                int imageId = R.mipmap.icon_file;
//                fileSize = file.length();
//                Log.i(TAG, "====文件夹======>>" + fileName + "//" + fileSize);
//                entity = new FileEntity(imageId, fileName, filePath, FileEntity.FILE_STYLE_DIR);
//            } else {  //文件
//                fileSize = file.length();
//                Log.e(TAG, "====文件 ======>>" + fileName + "===" + fileSize);
//                entity = new FileEntity();
//                entity.setFileStyle(FileEntity.FILE_STYLE_FILE);
//                entity.setFileName(fileName);
//                entity.setFileSise(fileSize);
//                entity.setFilePath(filePath);
//                int style_file = FileMatch.fileMatch(fileName);
//                if (style_file == FileEntity.STYLE_FILE_IMAGE) {
//                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_IMAGE);
//                } else if (style_file == FileEntity.STYLE_FILE_VIDEO) {
//                    entity.setSTYLE_FILE(FileEntity.STYLE_FILE_VIDEO);
//                }
//            }
//            if (fileSize > 5) {  //文件夹.或者文件太小不予显示
//                list.add(entity);
//            }
//        }
//        return list;
//    }
}