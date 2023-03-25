package com.etv.http.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 将log信息写入到SD设备中
 */
public class FileWriteSdRunnable implements Runnable {

    String txtInfo;
    String filePath;
    String fileName;

    long maxSize = 3 * 1000 * 1000;

    public FileWriteSdRunnable(String txtInfo, String filePath, String fileName) {
        this.txtInfo = txtInfo;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            if (txtInfo == null || fileName == null || filePath == null) {
                return;
            }
            File dir = new File(filePath);
            if (!dir.isDirectory()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
            File file = new File(dir, fileName);
            if (file.length() > maxSize) {
                return;
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(txtInfo.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
