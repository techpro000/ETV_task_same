package com.etv.http.util;

import java.io.File;

/***
 * 文件删除方法
 */
public class FileDelRunnable implements Runnable {

    File filePath;

    public FileDelRunnable(File filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run() {
        deleteDirOrFile(filePath);
    }

    private void deleteDirOrFile(File file) {
        try {
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                }
                for (File f : childFile) {
                    deleteDirOrFile(f);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
