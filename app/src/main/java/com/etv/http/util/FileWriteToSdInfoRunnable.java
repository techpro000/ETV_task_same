package com.etv.http.util;

import android.text.TextUtils;

import com.etv.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * 将log信息写入到SD设备中
 */
public class FileWriteToSdInfoRunnable implements Runnable {

    String devInfo;
    String filePath;

    public FileWriteToSdInfoRunnable(String devInfo, String filePath) {
        this.devInfo = devInfo;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            FileUtil.MKDIRSfILE(filePath);
            if (devInfo.length() < 5 || TextUtils.isEmpty(devInfo)) {
                return;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            devInfo = "\n" + devInfo;
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(devInfo.getBytes());
            raf.close();
//===================================================================================================
//            FileOutputStream writerStream = new FileOutputStream(file);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
//            writer.write(devInfo);
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
