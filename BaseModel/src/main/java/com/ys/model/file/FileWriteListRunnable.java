package com.ys.model.file;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/****
 * 文件夹拷贝，调用方法
 *   WriteFilesDialog filesDialog;
 *     FileWriteListRunnable fileWriteListRunnable;
 *
 *     private void initView() {
 *         filesDialog = new WriteFilesDialog(MainActivity.this);
 *         filesDialog.setWriteFileChangeLitener(new WriteFileListener() {
 *             @Override
 *             public void closeWriteFile() {
 *                 if (fileWriteListRunnable != null) {
 *                     fileWriteListRunnable.stopWriteFile();
 *                 }
 *             }
 *         });
 *         String path = "/sdcard/etv";
 *         String savePath = "/sdcard/crashlog";
 *         fileWriteListRunnable = new FileWriteListRunnable(path, savePath, new WriteFilesListener() {
 *             @Override
 *             public void writeStart(String copyPath, String pastePath) {
 *                 filesDialog.show(copyPath, pastePath);
 *             }
 *
 *             @Override
 *             public void writeProgress(int currentPosition, int totalPosition, int writeProgress) {
 *                 filesDialog.updateWriteFileProgress(currentPosition, totalPosition, writeProgress);
 *             }
 *
 *             @Override
 *             public void writeSuccess() {
 *                 filesDialog.udateTitleStatues("拷贝完成");
 *             }
 *
 *             @Override
 *             public void writrFailed(String errorrDesc) {
 *                 filesDialog.udateTitleStatues("拷贝失败:" + errorrDesc);
 *             }
 *         });
 *         Thread thread = new Thread(fileWriteListRunnable);
 *         thread.start();
 *     }
 */
public class FileWriteListRunnable implements Runnable {
    String copyPath;
    String pastPath;
    WriteFilesListener listener;
    int totalFileSize = 0;   //拷贝文件得总数
    int addFileSize = 0;      //拷贝文件得jindu
    private Handler handler = new Handler();

    /**
     * 复制文件夹及其中的文件
     *
     * @param copyPath String 原文件夹路径 如：data/user/1
     * @param pastPath String 复制后的路径 如：data/user/2
     */
    public FileWriteListRunnable(String copyPath, String pastPath, WriteFilesListener listener) {
        this.copyPath = copyPath;
        this.listener = listener;
        createnewFile(pastPath);
        File fileCopy = new File(copyPath);
        String fileName = fileCopy.getName();
        String newPath = pastPath + "/" + fileName;
        this.pastPath = newPath;
        createNewFolder(newPath);
    }

    @Override
    public void run() {
        logInfo("======totalFileSize===" + totalFileSize);
        writeFileDirToNewPath();
    }

    private void writeFileDirToNewPath() {
        File oldFile = new File(copyPath);
        File newFile = new File(pastPath);
        if (!oldFile.exists()) {
            backFailedToView("File Dir Is Not Exict ! " + copyPath);
            return;
        }
        if (!newFile.exists()) {
            newFile.mkdirs();
            backFailedToView("File Dir Is Not Exict ! " + pastPath);
            return;
        }
        totalFileSize = getwriteFileNum(copyPath);
        if (totalFileSize < 1) {
            backFailedToView("File No Need Copy !");
            return;
        }
        backCopyStart(copyPath, pastPath);
        isWriteFile = true;
        copyFolder(copyPath, pastPath);
    }

    boolean isWriteFile = false;

    public void stopWriteFile() {
        isWriteFile = false;
    }

    public void copyFolder(String oldPath, String newPath) {
        if (!isWriteFile) {
            return;
        }
        try {
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }
                if (temp.isDirectory()) {   //如果是子文件夹
                    String newPathDir = newPath + "/" + file;
                    createNewFolder(newPathDir);
                    copyFolder(oldPath + "/" + file, newPathDir);
                } else {
                    String pastFilePath = newPath + "/" + temp.getName();
                    File fileJujle = new File(pastFilePath);
                    if (fileJujle.exists()) {
                        int randon = new Random().nextInt(900000);
                        pastFilePath = newPath + "/copy_" + randon + temp.getName();
                    }
                    createnewFile(pastFilePath);
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(pastFilePath);
                    byte[] buffer = new byte[1024];

                    int length;
                    long sum = 0;
                    long fileLength = temp.length();
                    while (((length = fileInputStream.read(buffer)) != -1) && isWriteFile) {
                        fileOutputStream.write(buffer, 0, length);
                        sum += length;
                        int progress = (int) (sum * 100 / fileLength);
                        backWriteProgress(addFileSize, totalFileSize, progress);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    addFileSize++;
                    backWriteProgress(addFileSize, totalFileSize, 100);
                }
            }
//             全部写完
            backSuccessWriteList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logInfo(String message) {
        Log.e("write", message);
    }

    private void createnewFile(String pastFilePath) {
        File file1 = new File(pastFilePath);
        try {
            if (!file1.exists()) {
                boolean isCreate = file1.createNewFile();
                Log.e("cdl", "======创建文件夹===" + isCreate + " / " + pastFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 创建新的文件夹
     * @param newPathDir
     */
    private void createNewFolder(String newPathDir) {
        File file = new File(newPathDir);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }

    private void backSuccessWriteList() {
        logInfo("=============backSuccessWriteList=====");
        if (handler == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writeSuccess();
            }
        });
    }

    private void backWriteProgress(int addFileSize, int totalFileSize, int progress) {
        logInfo("=============backWriteProgress=====" + addFileSize + " / " + totalFileSize + " / " + progress);
        if (handler == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writeProgress(addFileSize, totalFileSize, progress);
            }
        });
    }

    private void backCopyStart(String copyPath, String pastPath) {
        logInfo("=============backFailedToView=====");
        if (handler == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writeStart(copyPath, pastPath);
            }
        });
    }

    private void backFailedToView(String s) {
        logInfo("=============backFailedToView=====" + s);
        if (handler == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.writrFailed(s);
            }
        });
    }

    public int getwriteFileNum(String filePath) {
        List<File> listFileSearch = new ArrayList<>();
        getFilesFromPath(filePath, listFileSearch);
        if (listFileSearch == null || listFileSearch.size() < 1) {
            return 0;
        }
        return listFileSearch.size();
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
            e.printStackTrace();
        }
    }


}
