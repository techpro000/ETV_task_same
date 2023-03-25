package com.etv.db;

import android.content.Context;

import com.EtvApplication;
import com.etv.config.AppInfo;
import com.etv.entity.BggImageEntity;
import com.etv.entity.ScreenEntity;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.ys.etv.R;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbBggImageUtil {

    /**
     * 保存数据到数据库
     *
     * @param entity
     * @return
     */
    public static boolean addBggInfoToDb(BggImageEntity entity) {
        boolean isOpenBgg = SharedPerManager.getBggImageFromWeb();
        if (!isOpenBgg) {
            clearBggImageInfo();
            String bggPath = AppInfo.BASE_BGG_IMAGE();
            FileUtil.deleteDirOrFilePath(bggPath, "开关没有打开，直接删除本地的数据");
            return false;
        }
        boolean isSave = false;
        if (entity == null) {
            return isSave;
        }
        try {
            isSave = entity.save();
            MyLog.bgg("===保存背景图logo到数据库==" + isSave + " / " + entity.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /**
     * 获取默认的显示i图片
     *
     * @return
     */
    public static int getDefaultBggImage() {
        int imageIBack = R.mipmap.play_bgg;
        return imageIBack;
    }

    /***
     * 获取Logo 路径
     * @return
     */
    public static BggImageEntity getLogoInfoFromDb() {
        List<BggImageEntity> bggImageEntityList = getBggImageListFromDb(1);
        if (bggImageEntityList == null || bggImageEntityList.size() < 1) {
            return null;
        }
        BggImageEntity bggImageEntity = bggImageEntityList.get(0);
        if (bggImageEntity == null) {
            return null;
        }
        return bggImageEntity;
    }


    /**
     * 获取加载得背景图图片
     *
     * @param context
     * @return
     */
    public static String getBggImagePath(Context context) {
        List<BggImageEntity> list = getBggImageListFromDb(0);
        if (list == null || list.size() < 1) {
            return null;
        }
        List<ScreenEntity> getListScreen = EtvApplication.getInstance().getListScreen();
        ScreenEntity screenEntity = getListScreen.get(0);
        int width = screenEntity.getScreenWidth();
        int height = screenEntity.getScreenHeight();
        String screenFroword = "l";
        if (width - height > 0) {
            // 横屏
            screenFroword = "horizontal";
        } else { // 竖屏
            screenFroword = "vertical";
        }
        String savePath = null;
        for (int i = 0; i < list.size(); i++) {
            BggImageEntity bggImageEntity = list.get(i);
            String type = bggImageEntity.getFileType().trim();
            MyLog.cdl("===========获取url=========" + screenFroword + " / " + type);
            if (screenFroword.contains(type)) {
                savePath = bggImageEntity.getSavePath() + "/" + bggImageEntity.getImageName();
                MyLog.cdl("===========获取url========返回=" + savePath);
                break;
            }
        }
        if (savePath == null || savePath.length() < 5) {
            MyLog.cdl("===========获取url========null=");
            return null;
        }
        File file = new File(savePath);
        if (file.exists()) {
            return savePath;
        } else {
            return null;
        }
    }

    /**
     * 获取下载得资源
     *
     * @return
     */
    public static List<BggImageEntity> getBggImageDownListFromDb() {
        List<BggImageEntity> bggImageEntityList = getBggImageListFromDb(-1);
        if (bggImageEntityList == null || bggImageEntityList.size() < 1) {
            MyLog.bgg("=========检测下载的背景 getBggImageDownListFromDb==00");
            return null;
        }
        MyLog.bgg("=========检测下载的背景 getBggImageDownListFromDb==" + bggImageEntityList.size());
        List<BggImageEntity> listBack = new ArrayList<BggImageEntity>();
        for (int i = 0; i < bggImageEntityList.size(); i++) {
            BggImageEntity bggImageEntity = bggImageEntityList.get(i);
            long fileSize = bggImageEntity.getFileSize();
            String basePath = bggImageEntity.getSavePath();
            String fileName = bggImageEntity.getImageName();
            String savePath = basePath + "/" + fileName;
            boolean isFileExict = jujleFileExict(savePath, fileSize);
            MyLog.bgg("=========检测下载的背景 getBggImageDownListFromDb==" + savePath + " / " + isFileExict);
            if (!isFileExict) {
                listBack.add(bggImageEntity);
            }
        }
        return listBack;
    }

    private static boolean jujleFileExict(String savePath, long imageSize) {
        File file = new File(savePath);
        if (file.exists()) {
            long fileSize = file.length();
            if (Math.abs(imageSize - fileSize) > 5 * 1024) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /***
     * 获取数据
     * @param fileType
     *     public static final String STYPE_BGG_IMAGE = "0";
     *     public static final String STYPE_LOGO_IMAGE = "1";
     * -1 获取全部
     * 0  获取背景
     * 1  获取logo
     * @return
     */
    public static List<BggImageEntity> getBggImageListFromDb(int fileType) {
        MyLog.bgg("=======查询得背景信息getBggImageListFromDb==" + fileType);
        List<BggImageEntity> txtList = null;
        try {
            switch (fileType) {
                case -1:
                    txtList = LitePal.findAll(BggImageEntity.class);
                    break;
                case 0:   //bgg
                    txtList = LitePal.where("filestyle=?", BggImageEntity.STYPE_BGG_IMAGE).find(BggImageEntity.class);
                    break;
                case 1:  //logo
                    txtList = LitePal.where("filestyle=?", BggImageEntity.STYPE_LOGO_IMAGE).find(BggImageEntity.class);
                    break;
            }
            if (txtList == null || txtList.size() < 1) {
                MyLog.bgg("=======查询得背景信息getBggImageListFromDb==null");
            } else {
                for (int i = 0; i < txtList.size(); i++) {
                    MyLog.bgg("=======查询得背景信息=getBggImageListFromDb=txtList=" + txtList.get(i).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }

    /***
     * 清除 logo 信息
     */
    public static void clearLogoImageInfo() {
        try {
            int delNum = LitePal.deleteAll(BggImageEntity.class, "filestyle=?", BggImageEntity.STYPE_LOGO_IMAGE);
            if (delNum > 0) {
                MyLog.bgg("==删除背景图logo成功==" + delNum);
            } else {
                MyLog.bgg("==删除背景图logo失败==");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 清除背景图信息
     */
    public static void clearBggImageInfo() {
        try {
            int delNum = LitePal.deleteAll(BggImageEntity.class, "filestyle=?", BggImageEntity.STYPE_BGG_IMAGE);
            if (delNum > 0) {
                MyLog.bgg("==删除背景图成功==" + delNum);
            } else {
                MyLog.bgg("==删除背景图失败==");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
