package com.ys.model.file;

import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

/***
 * 将文件写入SD卡中
 */
public class FileWriteRunnable implements Runnable {

    List<String> fileList;
    String savePathBase;
    WriteSdListener listener;
    Handler handler = new Handler();

    /***
     * 文件复制方法
     * @param fileList
     * 文件得全路径
     * @param savePathBase
     * /sdcard/cache/test
     * 文件保存得新路径-全路径
     * @param listener
     * 文件读写回调接口
     */
    public FileWriteRunnable(List<String> fileList, String savePathBase, WriteSdListener listener) {
        this.fileList = fileList;
        this.savePathBase = savePathBase;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (fileList == null || fileList.size() < 1) {
            backSuccess("No File Need To Copy !");
            return;
        }
        totalFileNum = fileList.size();
        File fileSave = new File(savePathBase);
        if (!fileSave.exists()) {
            backFailed("The Target Folder Not Exict !");
            return;
        }
        String filePath = fileList.get(0);
        File file = new File(filePath);
        writeFileToSdPath(file);
    }

    /***
     * 拷贝下一个
     */
    private void parpreToWriteFilenext() {
        if (fileList == null || fileList.size() < 1) {
            backSuccess("Copy File Over !");
            return;
        }
        fileList.remove(0);
        if (fileList == null || fileList.size() < 1) {
            backSuccess("Copy File Over !");
            return;
        }
        String filePath = fileList.get(0);
        File file = new File(filePath);
        writeFileToSdPath(file);
    }


    int totalFileNum = 0;   //拷贝文件得总数
    int currentPosition = 0;  //当前拷贝文件得位置

    private void writeFileToSdPath(File fileCopy) {
        currentPosition++;
        try {
            if (!fileCopy.exists()) {
                parpreToWriteFilenext();
                return;
            }
            String fileName = fileCopy.getName();
            String fileSavePath = savePathBase + "/" + fileName;
            File fileSave = new File(fileSavePath);
            if (fileSave.exists()) {
                int randomNum = new Random().nextInt(90000);
                fileSavePath = savePathBase + "/copy_" + randomNum + fileName;
            } else {
                fileSave.createNewFile();
            }
            InputStream inputStream = new FileInputStream(fileSavePath);
            // 1.建立通道对象
            FileOutputStream fos = new FileOutputStream(fileSave);
            // 2.定义存储空间
            byte[] buffer = new byte[8192];
            // 3.开始读文件
            int lenght = 0;
            long sum = 0;
            long fileLength = fileCopy.length();
            while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                // 将Buffer中的数据写到outputStream对象中
                fos.write(buffer, 0, lenght);
                sum += lenght;
                int progress = (int) (sum * 100 / fileLength);
                backProgress(currentPosition, totalFileNum, progress);
            }
            fos.flush();// 刷新缓冲区
            fos.close();
            inputStream.close();
            backProgress(currentPosition, totalFileNum, 100);
            parpreToWriteFilenext();
        } catch (Exception e) {
            backFailed(e.toString());
        }
    }

    public void backFailed(final String rrorDesc) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writrFailed(rrorDesc);
            }
        });
    }

    public void backSuccess(final String desc) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writeSuccess(desc);
            }
        });
    }

    public void backProgress(int currentPosition, int totalNum, final int prgress) {
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("write", "=========写入中===================" + prgress);
                listener.writeProgress(currentPosition, totalNum, prgress);
            }
        });
    }

}
