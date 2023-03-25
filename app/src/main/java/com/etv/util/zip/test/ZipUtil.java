package com.etv.util.zip.test;

import android.os.Handler;
import android.os.Message;

import com.etv.util.MyLog;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;

public class ZipUtil {

    private final String password = "d2x1z0s6";
    public ZipFileListener listenerBack;
    boolean isZipFile = false;

    /**
     * 停止解压
     */
    public void stopZipFile() {
        isZipFile = false;
        isTrue = false;

        if (handler != null) {
            handler.removeMessages(MESSAGE_CHECK_FILE_EXICT);
            handler.removeMessages(MESSAGE_ZIP_PROGRESS);
        }
    }

    /**
     * 解压文件========================================================================================================================
     *
     * @param zipPath
     * @param filePath
     * @param listener
     */
    public void unZipFileWithProgress(final String zipPath, final String filePath, final ZipFileListener listener) {
        isZipFile = true;
        try {
            listenerBack = listener;
            File zipFile = new File(zipPath);
            ZipFile zFile = new ZipFile(zipFile);
//            zFile.setFileNameCharset("UTF-8");
            zFile.setFileNameCharset("GBK");
            if (!zFile.isValidZipFile()) {
                backSuccessFailed(false, "不是标准压缩文件", listener);
                return;
            }
            File destDir = new File(filePath);
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            if (zFile.isEncrypted()) {
                zFile.setPassword(password);
            }
            final ProgressMonitor progressMonitor = zFile.getProgressMonitor();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int precentDone = 0;
                        while (isZipFile) {
                            Thread.sleep(200);
                            precentDone = progressMonitor.getPercentDone();
                            Message message = new Message();
                            message.what = MESSAGE_ZIP_PROGRESS;
                            message.obj = precentDone;
                            handler.sendMessage(message);
                            if (precentDone > 100 || precentDone == 100) {
                                break;
                            }
                        }
                        backSuccessFailed(true, filePath, listener);
                    } catch (InterruptedException e) {
                        backSuccessFailed(false, e.getMessage(), listener);
                        e.printStackTrace();
                    } finally {
//                    zipFile.delete();
                    }
                }
            });
            thread.start();
            zFile.setRunInThread(true);
            zFile.extractAll(filePath);
        } catch (Exception e) {
        }
    }

    private void backSuccessFailed(final boolean isTrue, final String desc, final ZipFileListener listener) {
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                MyLog.cdl("解压完毕: " + isTrue + " / " + desc);
                if (!isTrue) {
                    listener.zipError(desc);
                } else {
                    listener.checkFileStatues();
                    if (isCheckFileExict) { //如果检查文件的完整性，就去检查，
                        checkFileExict(desc);
                    } else {
//                        如果不检查完整性，直接回调播放结束
                        listener.zipComplet(desc);
                    }
                }
            }
        });
    }


    private boolean isCheckFileExict = true;

    public void ifNeedCheckFileExict(boolean isCheckFileExict) {
        this.isCheckFileExict = isCheckFileExict;
    }

    private void checkFileExict(final String filePath) {
        MyLog.cdl("去检查文件是否存在");
        isTrue = true;
        checkNum = 0;
        //这里起线程，去判断文件是否存在，如果存在就直接返回，
        //不存在，5秒后返回
        new Thread() {
            @Override
            public void run() {
                while (isTrue) {
                    super.run();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = MESSAGE_CHECK_FILE_EXICT;
                    message.obj = filePath;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    private final int MESSAGE_CHECK_FILE_EXICT = 564;
    private final int MESSAGE_ZIP_PROGRESS = 565;
    private int checkNum = 0;
    private boolean isTrue = true;
    private static final int CHECK_MAX_NUM = 30;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_ZIP_PROGRESS:
                    int progress = (int) msg.obj;
                    listenerBack.zipProgress(progress);
                    break;
                case MESSAGE_CHECK_FILE_EXICT:
                    if (checkNum == CHECK_MAX_NUM) {
                        MyLog.cdl("=====检测文件完整性，超过次数了");
                        isTrue = false;
                        listenerBack.zipError("检测文件不存在");
                    } else if (checkNum < CHECK_MAX_NUM) {
                        checkNum++;
                        String fileBasePath = (String) msg.obj;
                        String filePath = fileBasePath + "/task.txt";
                        File file = new File(filePath);
                        MyLog.cdl("=====检测文件完整性：" + filePath + " / " + file.exists());
                        if (file.exists()) {
                            checkNum = CHECK_MAX_NUM + 1;
                            listenerBack.zipComplet(fileBasePath);
                            isTrue = false;
                        }
                    }
                    break;
            }
        }
    };
}