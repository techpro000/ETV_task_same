package com.etv.util.sdcard;

import android.content.Context;
import android.os.Build;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.StorageInfo;
import com.etv.http.util.FileDelRunnable;
import com.etv.service.EtvService;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.system.CpuModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * 文件过滤器
 */
public class FileFilter {

    /**
     * 判断文件是否存在
     *
     * @param context
     * @param sdUsbPath
     * @return
     */
    public static StorageInfo jujleFileIsExict(Context context, String sdUsbPath) {
        boolean isOrSd = MySDCard.isMountSD(context, sdUsbPath);
        MyLog.usb("=======USB path===0000=" + isOrSd + " / " + Build.VERSION.SDK_INT);
        if (!isOrSd) { //USB设备
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {  // 7.0
                sdUsbPath = sdUsbPath + "/udisk0";
            }
        }
        MyLog.usb("====USB path====1111==" + sdUsbPath);
        if (sdUsbPath == null || sdUsbPath.contains("null") || sdUsbPath.length() < 5) {
            MyLog.usb("====USB path===1111=== null");
            return null;
        }
        String path = getFileFormSave(sdUsbPath);
        MyLog.usb("====USB path==22222===" + path);
        if (path != null && path.length() > 2) {
            StorageInfo storageInfo = new StorageInfo(path, StorageInfo.ACTION_NOTHING, StorageInfo.TYPE_USB);
            MyLog.usb("====USB path==3333===" + storageInfo.toString());
            return storageInfo;
        }
        return null;
    }

    /***
     * 判断该路径下有没有想要的文件
     * @param sdPath
     * @return
     */
    private static String getFileFormSave(String sdPath) {
        String StringBack = null;
        try {
            File file = new File(sdPath);
            if (!file.exists()) {
                MyLog.cdl("====USB path==1111000===文件不存在");
                return null;
            }
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                MyLog.cdl("====USB path==1111000===files==null");
                return null;
            }
            for (File fileSave : files) {
                String filePath = fileSave.getPath();
                String fileName = fileSave.getName();
                MyLog.usb("======便利数据路径====" + filePath);
                if (filePath.contains(AppInfo.IP_FILE_NAME) ||
                        filePath.contains(AppInfo.DISONLINE_TASK_DIR_ZIP) ||
                        filePath.contains(AppInfo.SINGLE_TASK_DIR_ZIP) ||
                        filePath.contains(AppInfo.VOICE_MEDIA)) {
                    StringBack = filePath;
                    break;
                }
                if (filePath.contains(".apk")) {
                    if (fileName.startsWith("ETV") || fileName.startsWith("etv")) {
                        StringBack = filePath;
                        break;
                    }
                }
            }
            return StringBack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringBack;
    }

    //================================================================
    private static final int DEL_FILE_NOT_TASK = 1;  //删除文件保留任务
    private static final int DEL_FILE_SAVE_CURRENT_TASK = 2;  //删除所有的任务，仅仅保留当前正在播放的任务
    private static final int DEL_FILE_CONTAIN_TASK = 3;  //删除所有的文件

    public static void clearSDcardForCache() {
        int sdcardAuthor = SharedPerManager.getSdcardManagerAuthor();
        String videoPath = AppInfo.APP_VIDEO_PATH();
        FileUtil.deleteDirOrFilePath(videoPath, "检测内存，内存不足，删除录制视频文件");
        switch (sdcardAuthor) {
            case 1:  //低级
                delFileFromPath(AppInfo.BASE_SD_PATH(), DEL_FILE_NOT_TASK);
                break;
            case 2:  //中级
                delFileFromPath(AppInfo.BASE_SD_PATH(), DEL_FILE_CONTAIN_TASK);
                break;
            case 3:  //高级
                delFileFromPath(AppInfo.BASE_SD_PATH(), DEL_FILE_CONTAIN_TASK);
                break;
        }
    }

    /***
     * 删除项目路径
     * sdcard/etv
     */
    public static void delFileFromPath(String path) {
        delFileFromPath(path, DEL_FILE_NOT_TASK);
    }


    /***
     *
     * @param path
     * @param tag
     * tag  <0 的时候，表示删除所有的文件
     * tag  >0 保留etv
     */
    public static void delFileFromPath(String path, int tag) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            File[] listFile = file.listFiles();
            if (listFile == null || listFile.length < 1) {
                return;
            }
            for (int i = 0; i < listFile.length; i++) {
                File fileiten = listFile[i];
                boolean isDelFile = isFileterFile(fileiten, tag);
                if (isDelFile) {
                    FileDelRunnable runnable = new FileDelRunnable(fileiten);
                    EtvService.getInstance().executor(runnable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtil.creatPathNotExcit("清理笨死素材信息");
    }

    /***
     * 判断文件是否可以删除
     * @param file
     * true 可以删除，  false 不可以删除
     * @return
     */
    private static boolean isFileterFile(File file, int tag) {
        if (file.isFile()) {
            return true;
        }
        //文件夹
        String fileName = file.getName();
        List<String> listFileName = getList(tag);
        for (String fileS : listFileName) {
            if (fileName.contains(fileS)) {
                return false;
            }
        }
        return true;
    }


    /***
     *
     * @param tag
     * DEL_FILE_NOT_TASK == 的时候保护ETV
     * DEL_FILE_CONTAIN_TASK ==，删除所有的数据
     * @return
     */
    private static List<String> getList(int tag) {
        List<String> listName = new ArrayList<String>();
        listName.add("Podcasts");
        listName.add("DCIM");
        listName.add("crashlog");
        listName.add("Android");
        if (tag == DEL_FILE_NOT_TASK) {
            listName.add("etv");
        } else if (tag == DEL_FILE_CONTAIN_TASK) {
            //不做任何保护，直接删除
        }
        return listName;
    }
}
