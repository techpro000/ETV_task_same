package com.etv.util.sdcard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.etv.util.MyLog;
import com.etv.util.system.CpuModel;
import com.ys.rkapi.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class MySDCard {
    private Context context;
    public static String path = null;// SD卡的路径

//    public List<String> getSDCardPaths() {
//        switch (Build.VERSION.SDK_INT) {
//            case 25:
//            case 24:
//            case 23:
//            case 22:
//            case 21:
//            case 20:
//                return getSDCardPaths_20();
//            case 19:
//            case 18:
//            case 17:
//            case 16:
//            case 15:
//            case 14:
//                return getSDCardPaths_16();
//            default:
//                return getSDCardPaths_20();
//        }
//    }
//
//    public List<String> getSDCardPaths_20() {
//        List<String> fileList = new ArrayList<String>();
//        try {
//            Class class_StorageManager = StorageManager.class;
//            Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
//            Method method_getVolumeState = class_StorageManager.getMethod("getVolumeState", String.class);
//            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//            Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
//            Method method_isRemovable = class_StorageVolume.getMethod("isRemovable");
//            Method method_getPath = class_StorageVolume.getMethod("getPath");
//            Method method_getId = class_StorageVolume.getMethod("getUserLabel");
//            Method method_getPathFile = class_StorageVolume.getMethod("getPathFile");
//            Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);
//            //objArray.length==2时说明插上了sd卡
//
//            for (Object value : objArray) {
//                path = (String) method_getPath.invoke(value);
//                Boolean isRemovable = (Boolean) method_isRemovable.invoke(value);
//                String id = (String) method_getId.invoke(value);
//                getVolumeState = (String) method_getVolumeState.invoke(sm, path);// 获取挂载状态。
//                if (Environment.MEDIA_MOUNTED.equals(getVolumeState)) {
//                    File file = (File) method_getPathFile.invoke(value);
//                    fileList.add(file.getPath());
//                }
//            }
//            return fileList;
//        } catch (Exception e) {
//            MyLog.cdl("====获取path  error==" + e.toString());
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    @TargetApi(16)
//    public List<String> getSDCardPaths_16() {
//        List<String> fileList = new ArrayList<String>();
//        try {
//            Class class_StorageManager = StorageManager.class;
//            Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
//            Method method_getVolumeState = class_StorageManager.getMethod("getVolumeState", String.class);
//            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//            Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
//            Method method_isRemovable = class_StorageVolume.getMethod("isRemovable");
//            Method method_getPath = class_StorageVolume.getMethod("getPath");
//            Method method_getId = class_StorageVolume.getMethod("getUserLabel");
//            Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);
//            for (Object value : objArray) {
//                String path = (String) method_getPath.invoke(value);
//                String getVolumeState = (String) method_getVolumeState.invoke(sm, path);// 获取挂载状态。
//                String id = (String) method_getId.invoke(value);
//                if (Environment.MEDIA_MOUNTED.equals(getVolumeState)) {
//                    File file = new File((String) method_getPath.invoke(value));
//                    fileList.add(file.getPath());
//                }
//            }
//            return fileList;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /***
     * 获取所有的存储设备
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    public List<String> getAllExternalStorage() {
        List<String> storagePath = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] storageVolumes;
        try {
            Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
            storageVolumes = (StorageVolume[]) getVolumeList.invoke(storageManager);
            Method getVolumeState = StorageManager.class.getDeclaredMethod("getVolumeState", String.class);
            for (StorageVolume storageVolume : storageVolumes) {
                Method getPath = null;
                getPath = StorageVolume.class.getMethod("getPath");
                String path = (String) getPath.invoke(storageVolume);
                MyLog.e("sdcard", "====path==" + path);
                String state = (String) getVolumeState.invoke(storageManager, path);
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    storagePath.add(path);
                }
            }
        } catch (Exception e) {
            Log.e("cdl", e.getMessage());
        }
        return storagePath;
    }


    /**
     * 构造方法
     */
    public MySDCard(Context context) {
        this.context = context;
    }

    /***
     * 获取可用内存
     * 1024*1024  M
     * 1024*1024*1024  G
     * @param path1
     * @param changeSie
     * @return
     */
    public long getAvailableExternalMemorySize(String path1, long changeSie) {
        long backSize = 0;
        try {
            File path = new File(path1);
            if (path1 == null || !path.exists()) {
                return 0;
            }
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            backSize = (availableBlocks * blockSize) / changeSie;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backSize;
    }

    /***
     * 获取内存卡的总内存
     * 1024*1024  M
     * 1024*1024*1024  G
     * @param path2
     * @param changeSie
     * @return
     */
    public long getTotalExternalMemorySize(String path2, long changeSie) {
        long backSize = 0;
        try {
            File path = new File(path2);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            MyLog.cdl("======" + (blockSize * totalBlocks));
            backSize = (totalBlocks * blockSize) / changeSie;
        } catch (Exception e) {
        }
        return backSize;
    }

    public static final String TAG = "MySDCard";

    //====================================================================================
    private static long readBlockSize(String path, int flag) {
        StatFs sf = new StatFs(path);
        long blockSize = (long) sf.getBlockSize();
        long blockCount = (long) sf.getBlockCount();
        long availCount = (long) sf.getAvailableBlocks();
        return flag == 0 ? blockSize * blockCount / 1024L : (flag == 1 ? blockSize * availCount / 1024L : blockSize * blockCount / 1024L - blockSize * availCount / 1024L);
    }

    /***
     * 获取内存
     * @return
     */
    public static long getRealSizeOfNand() {
        long size = 8;
        long readBlockSize = readBlockSize(Constant.NAND_PATH, 0) / 1048576L;
        if (readBlockSize < 3L) {
            size = 4;
        } else if (readBlockSize >= 3L && readBlockSize < 7L) {
            size = 8;
        } else if (readBlockSize >= 7L && readBlockSize < 15L) {
            size = 16;
        } else if (readBlockSize >= 15L && readBlockSize < 31L) {
            size = 32;
        } else if (readBlockSize >= 31L && readBlockSize < 63L) {
            size = 64;
        } else if (readBlockSize >= 63L && readBlockSize < 127L) {
            size = 128;
        } else {
            size = 8;
        }
        return size;
    }

    /**
     * 获取USB设备列表数据
     *
     * @return
     */
    public static List<String> getAllUSBStorageLocations() {
        ArrayList mMounts = new ArrayList();
        try {
            File e = new File("/proc/mounts");
            if (e.exists()) {
                Scanner scanner = new Scanner(e);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];
                        if (element.contains("USB")) {
                            mMounts.add(element);
                        }
                        MyLog.e("CDL", "=====获取的存储设备==" + element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMounts;
    }

    private static long getTotalMemorySize() {
        String dir = "/proc/meminfo";
        try {
            FileReader e = new FileReader(dir);
            BufferedReader br = new BufferedReader(e, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return (long) Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) / 1024L;
        } catch (IOException var5) {
            var5.printStackTrace();
            return 0L;
        }
    }

    /***
     * 运行内存
     * @return
     */
    public static String getRealMeoSize() {
        String size = "0";
        if (getTotalMemorySize() <= 512L) {
            size = "512M";
        } else if (getTotalMemorySize() > 512L && getTotalMemorySize() <= 1024L) {
            size = "1G";
        } else if (getTotalMemorySize() > 1024L && getTotalMemorySize() <= 2048L) {
            size = "2G";
        } else if (getTotalMemorySize() > 2048L && getTotalMemorySize() <= 6114L) {
            size = "4G";
        } else if (getTotalMemorySize() > 6114L) {
            size = "6G";
        }

        return size;
    }

    public String getSDCardPath() {
        try {
            Class class_StorageManager = StorageManager.class;
            Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
            Method method_getVolumeState = class_StorageManager.getMethod("getVolumeState", String.class);
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
            Method method_getPath = class_StorageVolume.getMethod("getPath");
            Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);
            if ((method_getVolumeState.invoke(sm, method_getPath.invoke(objArray[1]))).equals("mounted")) {
                path = (String) method_getPath.invoke(objArray[1]);
            } else if ((method_getVolumeState.invoke(sm, method_getPath.invoke(objArray[2]))).equals("mounted")) {
                path = (String) method_getPath.invoke(objArray[2]);
                File file = new File(path);
                File[] list = file.listFiles();
                path = path + "/" + list[0].getName();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLastSpace(int type, String path2) {
        String space_desc = "";
        File path = new File(path2);
        long usableSpace;
        if (type == 1) {
            usableSpace = path.getUsableSpace();
        } else {
            usableSpace = path.getTotalSpace();
        }
        space_desc = String.valueOf(usableSpace);
        return space_desc;
    }

    /**
     * 获取内置存储路径
     *
     * @return
     */
    public static String getNandPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取USB路径
     *
     * @param context
     * @return
     */
    public static String getUSBPath(Context context) {
        String usbPath = null;
        if (Build.VERSION.SDK_INT < 23) { // Code<6.0
            usbPath = "/mnt/usb_storage/USB_DISK1/udisk0";
            File file = new File(usbPath);
            if (file.exists()) {
                MyLog.cdl("===cdl===USB路径存在直接返回==" + usbPath);
                return usbPath;
            }
            return "/mnt/usb_storage/USB_DISK0/udisk0";
        } else if (Build.VERSION.SDK_INT > 24) {
            usbPath = getUsbDir(context);
        }
        if (usbPath == null || usbPath.contains("null") || usbPath.length() < 6) {
            return null;
        }
        if (usbPath.endsWith("/")) {
            usbPath = usbPath.substring(0, usbPath.length() - 1);
        }
        return usbPath;
    }

    /**
     * 获取SD卡路径
     *
     * @param context
     * @return
     */
    public static String getSDcardPath(Context context) {
        String pathBack = null;
        if (Build.VERSION.SDK_INT < 23) {
            pathBack = "/mnt/external_sd/";
            File file = new File(pathBack);
            if (!file.exists()) {
                pathBack = null;
            }
        } else {
            pathBack = getSDcardDir(context);
        }
        if (pathBack == null || pathBack.contains("null") || pathBack.length() < 6) {
            return null;
        }
        if (pathBack.endsWith("/")) {
            pathBack = pathBack.substring(0, pathBack.length() - 1);
        }
        return pathBack;
    }

    /**
     * 获取USB根目录
     *
     * @param context
     * @return
     */
    private static String getUsbDir(Context context) {
        String usbDir = null;
        StorageManager storageManager = getStorageManager(context);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isUsb = diskInfoClazz.getMethod("isUsb");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isUsb.invoke(disk)) {
                            usbDir = (String) path.get(volumeInfo);
                            MyLog.cdl("=======usb路径==usbDir==" + usbDir);
                            break;
                        }
                    }
                }
            }
            return usbDir + File.separator;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外置SD卡根目录
     *
     * @param context
     * @return
     */
    private static String getSDcardDir(Context context) {
        String sdcardDir = null;
        StorageManager storageManager = getStorageManager(context);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isSd.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            return sdcardDir + File.separator;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMountSD(Context context, String path) {
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_3568_11)) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return path.toLowerCase().contains("external_sd");
        } else {
            return isMountSD(context);
        }
    }

    /**
     * 当前挂载是否为SD
     *
     * @param context
     * @return
     */
    private static boolean isMountSD(Context context) {
        StorageManager storageManager = getStorageManager(context);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Method getState = volumeInfoClazz.getMethod("getState");
            Method getDescriptionComparator = volumeInfoClazz.getMethod("getDescriptionComparator");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            Collections.sort(result, (Comparator<? super Class<?>>) getDescriptionComparator.invoke(volumeInfoClazz));
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isSd.invoke(disk)) {
                            int status = (int) getState.invoke(volumeInfo);
                            Log.d(TAG, "isMountSD()--status-->" + status);
                            if (status == 2) {
                                return true;
                            }
                            return false;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMountUSB(Context context, String path) {
        if (Build.VERSION.SDK_INT < 23) {
            return path.toLowerCase().contains("usb");
        } else {
            return isMountUSB(context);
        }
    }

    /**
     * 当前挂载是否为USB
     *
     * @param context
     * @return
     */
    private static boolean isMountUSB(Context context) {
        StorageManager storageManager = getStorageManager(context);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isUsb = diskInfoClazz.getMethod("isUsb");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Method getState = volumeInfoClazz.getMethod("getState");
            Method getDescriptionComparator = volumeInfoClazz.getMethod("getDescriptionComparator");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            Collections.sort(result, (Comparator<? super Class<?>>) getDescriptionComparator.invoke(volumeInfoClazz));
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isUsb.invoke(disk)) {
                            int status = (int) getState.invoke(volumeInfo);
                            Log.d(TAG, "isMountUSB()--status-->" + status);
                            if (status == 2) {
                                return true;
                            }
                            return false;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static StorageManager getStorageManager(Context context) {
        return (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    }

    public static boolean isSDMounted(Context context) {
        boolean isMounted = false;
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method getVolumList = StorageManager.class.getMethod("getVolumeList");
            getVolumList.setAccessible(true);
            Object[] results = (Object[]) getVolumList.invoke(sm);
            if (results != null) {
                for (Object result : results) {
                    Method mRemoveable = result.getClass().getMethod("isRemovable");
                    Boolean isRemovable = (Boolean) mRemoveable.invoke(result);
                    if (isRemovable) {
                        Method getPath = result.getClass().getMethod("getPath");
                        String path = (String) mRemoveable.invoke(result);
                        Method getState = sm.getClass().getMethod("getVolumeState", String.class);
                        String state = (String) getState.invoke(sm, path);
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            isMounted = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMounted;

    }
}


//    public static void setValueToEEPROM(String val) {
//        try {
//            Class classType = Class.forName("android.os.Custom");
//            Method e = classType.getDeclaredMethod("setUsrbuf", new Class[]{String.class});
//            e.invoke(classType, new Object[]{val});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String getValueFromEEPROM() {
//        String str = "";
//        File file = new File("/sys/devices/ff140000.i2c/i2c-1/1-0050/usrbuf");
//        long fileSize = file.length();
//        Log.d("EEPROM读取长度", "值 = " + fileSize);
//
//        try {
//            FileInputStream e = new FileInputStream(file);
//            byte[] buffer = new byte[(int) fileSize];
//            e.read(buffer);
//            e.close();
//            str = new String(buffer);
//            Log.d("EEPROM读取", "值 = " + str);
//        } catch (FileNotFoundException var6) {
//            var6.printStackTrace();
//        } catch (UnsupportedEncodingException var7) {
//            var7.printStackTrace();
//        } catch (IOException var8) {
//            var8.printStackTrace();
//        }
//
//        return str;
//    }
