package com.etv.http.util;

import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetFileFromPathForRunnable implements Runnable {

    String filePath;

    private Handler handler = new Handler();
    QueryFileFromPathListener listener;

    public GetFileFromPathForRunnable(String path, QueryFileFromPathListener listener) {
        this.filePath = path;
        this.listener = listener;
    }

    @Override
    public void run() {
        File file = new File(filePath);
        if (!file.exists()) {
            backFileListToView(false, null, "File is not exists");
            return;
        }
        List<File> listFileSearch = new ArrayList<>();
        getFilesFromPath(filePath, listFileSearch);
        if (listFileSearch == null || listFileSearch.size() < 1) {
            backFileListToView(false, null, "File is not exists");
        } else {
            backFileListToView(true, listFileSearch, "Successful retrieval of documents");
        }
    }

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
            backFileListToView(false, null, "ERROR :" + e.toString());
            e.printStackTrace();
        }
    }

    private void backFileListToView(boolean b, List<File> listFileSearch, String errorDesc) {
        if (listener == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.backFileList(b, listFileSearch, errorDesc);
            }
        });


    }

    public interface QueryFileFromPathListener {
        void backFileList(boolean isSuccess, List<File> listFileSearch, String errorDesc);
    }

}
