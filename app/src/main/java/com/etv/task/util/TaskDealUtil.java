package com.etv.task.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.EtvApplication;
import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.ScreenEntity;
import com.etv.entity.ShowTypeEntity;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.PositionEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.task.entity.ViewPosition;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.SimpleDateUtil;
import com.etv.util.system.CpuModel;
import com.etv.view.layout.Generator;
import com.etv.view.layout.image.ViewImageSingleGenertrator;
import com.etv.view.layout.video.media.ViewVideoGenertrator;
import com.etv.view.layout.video.surface.ViewVideoSurfaceGenertrator;
import com.ys.etv.R;
import com.ys.model.entity.RedioEntity;
import com.ys.model.util.FileMatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类11112222
 */
public class TaskDealUtil {

    /**
     * 根据多少行，多少列来计算view 的显示位置
     *
     * @param position 第几个要显示的位置
     * @param row      多少行
     * @param cow      多少列
     * @return
     */
    public static PositionEntity getViewSizeByRowCow(int position, int row, int cow) {
        MyLog.video("===getViewSizeByPosition===" + position);
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        int left = (position % cow) * screenWidth;
        int top = (position / cow) * screenHeight;
        int width = screenWidth * cow;
        int height = screenHeight * row;
        return new PositionEntity(-left, -top, width, height);
    }

    /***
     * 上下两分屏
     * @param showPosition
     * @return
     */
/*    private static PositionEntity getTopBottomSizeByPosition(int showPosition) {
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        int leftPosition = 0;
        int topPosition = 0;
        int width = 0;
        int height = 0;
        if (showPosition == 0) {
            leftPosition = 0;
            topPosition = 0;
            width = screenWidth;
            height = screenHeight * 2;
        }
        if (showPosition == 1) {
            leftPosition = 0;
            topPosition = -screenHeight;
            width = screenWidth;
            height = screenHeight * 2;
        }
        PositionEntity positionEntity = new PositionEntity(leftPosition, topPosition, width, height);
        return positionEntity;
    }*/


    /***
     * 左右两分屏
     * @param
     * @return
     */
/*    private static PositionEntity getLeftRightSizeByPosition(int showPosition) {
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        int leftPosition = 0;
        int topPosition = 0;
        int width = 0;
        int height = 0;
        if (showPosition == 0) {
            leftPosition = 0;
            topPosition = 0;
            width = screenWidth * 2;
            height = screenHeight;
        }
        if (showPosition == 1) {
            leftPosition = -screenWidth;
            topPosition = 0;
            width = screenWidth * 2;
            height = screenHeight;
        }
        PositionEntity positionEntity = new PositionEntity(leftPosition, topPosition, width, height);
        return positionEntity;
    }*/
    public static PositionEntity getCpListPosition(CpListEntity cpEntity) {
        int leftPosition = 0;
        int topPosition = 0;
        int width = 0;
        int height = 0;
        int pmResolutionType = cpEntity.getPmResolutionType();          // 屏幕类型  1：分辨率  2：自适应
        if (pmResolutionType == CpListEntity.SCREEN_TYPE_DPI) { //分辨率
            leftPosition = StringToFloat(cpEntity.getCoLeftPosition());
            topPosition = StringToFloat(cpEntity.getCoRightPosition());
            width = StringToFloat(cpEntity.getCoWidth());
            height = StringToFloat(cpEntity.getCoHeight());
        } else if (pmResolutionType == CpListEntity.SCREEN_TYPE_AUTO_SCREEN ||
                pmResolutionType == CpListEntity.SCREEN_TYPE_4K_SHOW) {
            //自适应  4k
            int pmFixedScreen = cpEntity.getPmFixedScreen(); // 1横屏 2 竖屏
            int pmWidth = 1920;
            int pmHeight = 1080;
            if (pmFixedScreen == CpListEntity.FIX_SCREEN_HRO) {
                pmWidth = 1920;
                pmHeight = 1080;
            } else if (pmFixedScreen == CpListEntity.FIX_SCREEN_VER) {
                pmWidth = 1080;
                pmHeight = 1920;
            }
            int devScreenWidth = SharedPerManager.getScreenWidth();
            int devScreenHeight = SharedPerManager.getScreenHeight();
            float jujleWidthSize = (float) (pmWidth * 1.0 / devScreenWidth * 1.0);
            float jujleWHeightSize = (float) (pmHeight * 1.0 / devScreenHeight * 1.0);
            leftPosition = StringToFloat(cpEntity.getCoLeftPosition(), jujleWidthSize);
            topPosition = StringToFloat(cpEntity.getCoRightPosition(), jujleWHeightSize);
            width = StringToFloat(cpEntity.getCoWidth(), jujleWidthSize);
            height = StringToFloat(cpEntity.getCoHeight(), jujleWHeightSize);
        }
        PositionEntity positionEntity = new PositionEntity(leftPosition, topPosition, width, height);
        return positionEntity;
    }

    public static float getTextSize(String taSzie) {
        float fontSzie = 16;
        if (taSzie == null || taSzie.length() < 1) {
            taSzie = "16";
        }
        try {
            fontSzie = Float.parseFloat(taSzie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.cdl("=========获取得文字大小===" + fontSzie);
        return fontSzie;
    }

    /**
     * 获取当前是否是双屏联动效果
     * * true  双屏联动
     * * false  不联动
     *
     * @return
     */
    public static boolean isLinkDoubleScreen(SceneEntity sceneEntity) {
        //如果用户设置了不联动，优先执行
        boolean isDoubleScreenLink = SharedPerManager.getScreenSame();
        if (!isDoubleScreenLink) {
            return false;
        }
        if (sceneEntity == null) {
            MyLog.playTask("====双屏联动===sceneEntity == null");
            return false;
        }
        int linkScreen = sceneEntity.getEtIsLinkScreeen();
        MyLog.playTask("====双屏联动===linkScreen==" + linkScreen);
        if (linkScreen < 0) {
            MyLog.playTask("====双屏联动===不是联动，直接中断==" + sceneEntity.toString());
            return false;
        }
        MyLog.playTask("====双屏联动===双屏联动==" + sceneEntity.toString());
        return true;
    }

    /**
     * 对控件的显示顺序进行排序
     *
     * @param cpList
     * @return
     */
    public static List<CpListEntity> mathCpListOrder(List<CpListEntity> cpList) {
        if (cpList == null || cpList.size() < 1) {
            MyLog.playTask("控件==null,直接返回，不排序");
            return null;
        }
        List<CpListEntity> cpCacheList = new ArrayList<>();
        List<String> cpTypeList = TaskDealUtil.getCpType();

        for (int k = 0; k < cpTypeList.size(); k++) {
            for (int i = 0; i < cpList.size(); i++) {
                String cacheType = cpList.get(i).getCoType();
                String staticCpTyle = cpTypeList.get(k);
                if (staticCpTyle.equals(cacheType)) {
                    cpCacheList.add(cpList.get(i));
                }
            }
        }
        return cpCacheList;
    }


    /***
     * 控件格式显示顺序
     */
    public static List<String> getCpType() {
        List<String> spLists = new ArrayList<>();
        spLists.add(AppInfo.VIEW_IMAGE);           //图片格式00
        spLists.add(AppInfo.VIEW_VIDEO);           //视屏格式
        spLists.add(AppInfo.VIEW_IMAGE_VIDEO);     //混播格式
        return spLists;
    }

    /***
     * 判断今天有没有勾选中
     * @param task
     * @return
     */
    public static boolean ifMathTodayTask(TaskWorkEntity task) {
        int weekToday = SimpleDateUtil.getCurrentWeekDay();
        MyLog.task("====今天星期==" + weekToday);
        String saveWeek = "false";
        switch (weekToday) {
            case 1:
                saveWeek = task.getEtMon();
                break;
            case 2:
                saveWeek = task.getEtTue();
                break;
            case 3:
                saveWeek = task.getEtWed();
                break;
            case 4:
                saveWeek = task.getEtThur();
                break;
            case 5:
                saveWeek = task.getEtFri();
                break;
            case 6:
                saveWeek = task.getEtSat();
                break;
            case 7:
                saveWeek = task.getEtSun();
                break;
        }
        if (saveWeek.contains("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从16进制中获取颜色值
     *
     * @return
     */
    public static int getColorFromInToSystem(String textColor) {
        int backColor = 0x00000000;  //默认透明
        if (textColor == null || textColor.length() < 2) {
            return backColor;
        }
        if (!textColor.contains("#")) {
            return backColor;
        }
        //这里防止颜色值 #fff 位数比较少的情况
        if (textColor.length() < 6) {
            textColor = textColor + "fff";
        }
        backColor = Color.parseColor(textColor);
        return backColor;
    }

    public static int StringToFloat(String size) {
        return StringToFloat(size, 1);
    }


    public static int StringToFloat(String size, float jujleSize) {
        float sizeFloat = Float.parseFloat(size);
        int i = 0;
        if (sizeFloat > 0) {
            i = (int) ((sizeFloat * 10 + 5) / 10);
        } else if (sizeFloat < 0) {
            //负数
            i = (int) ((sizeFloat * 10 - 5) / 10);
        } else {
            i = 0;
        }
        return (int) (i * 1.0 / jujleSize * 1.0);
    }

    /***
     * 根据资源列表，返回素材资源集合
     * @param mpList
     * @return
     */
    public static List<MediAddEntity> getResourceListPath(List<MpListEntity> mpList) {
        List<MediAddEntity> imageList = new ArrayList<>();
        try {
            if (mpList == null) {
                return imageList;
            }
            if (mpList.size() < 1) {
                return imageList;
            }
            for (int k = 0; k < mpList.size(); k++) {
                String downUrl = mpList.get(k).getUrl();
                String savePath = getSavePath(downUrl);
                String midId = mpList.get(k).getMid();
                String cartoon = mpList.get(k).getCartoon();
                String playParam = mpList.get(k).getPlayParam();
                String volNum = mpList.get(k).getVolume();
                int fileType = FileMatch.fileMatch(downUrl);   //文件类型
//                MyLog.playTask("==网络路径转换本地路径==" + savePath);
                String etTaskType = mpList.get(k).getPmType();
                String size = mpList.get(k).getSize().trim();
                long fileSize = -1;
                if (size != null || size.length() > 0) {
                    fileSize = Long.parseLong(size);
                }
                imageList.add(new MediAddEntity(savePath, midId, cartoon, playParam, volNum, fileType, etTaskType, fileSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageList;
    }

    /***
     * 获取本地的保存地址
     * @param dbSavePath
     * @return
     */
    public static String getSavePath(String dbSavePath) {
        dbSavePath = dbSavePath.trim();
        if (dbSavePath == null || dbSavePath.length() < 1) {
            return "";
        }
        String name = dbSavePath.substring(dbSavePath.lastIndexOf("/") + 1, dbSavePath.length());
        String savePath = AppInfo.BASE_TASK_URL() + "/" + name;
        return savePath;
    }

    /***
     * 判断是不是网址
     * @param urls
     * @return
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    /***
     * 判断是不是属性文件
     * @param coType
     * @return
     */
    public static boolean isResourceType(String coType) {
        if (coType.contains(AppInfo.VIEW_IMAGE)
                || coType.contains(AppInfo.VIEW_VIDEO)
                || coType.contains(AppInfo.VIEW_IMAGE_VIDEO)) {
            return true;
        }
        return false;
    }

    //    rgba转16进制颜色值
    public static String convertRGBAToHex(String colorRgba) {
        String taBgColor = colorRgba;
        try {
            if (taBgColor == null || taBgColor.length() < 3) {
                return taBgColor;
            }
            taBgColor = taBgColor.substring(taBgColor.indexOf("(") + 1, taBgColor.lastIndexOf(")"));
            String[] colors = taBgColor.split(",");
            if (colors == null || colors.length < 1) {
                return taBgColor;
            }
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            int alpha = Integer.parseInt(colors[3]);

            String rgbColor = convertRGBToHex(red, green, blue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taBgColor;
    }

    private static String covertAlphaToHex() {
//        将透明度转换成不透明度(转换方式参考“透明度”，第2条) 。 不透明度为60%
//        不透明度乘以255。 我们得到结果：153
//        将计算结果转换成16进制。得到最终的不透明度：99
//        将不透明度和颜色值拼接成ARGB格式。得到最终的颜色值： #99FFFFFF
        return "";
    }


    private static String convertRGBToHex(int r, int g, int b) {
        String rFString, rSString, gFString, gSString,
                bFString, bSString, result;
        int red, green, blue;
        int rred, rgreen, rblue;
        red = r / 16;
        rred = r % 16;
        if (red == 10) rFString = "A";
        else if (red == 11) rFString = "B";
        else if (red == 12) rFString = "C";
        else if (red == 13) rFString = "D";
        else if (red == 14) rFString = "E";
        else if (red == 15) rFString = "F";
        else rFString = String.valueOf(red);

        if (rred == 10) rSString = "A";
        else if (rred == 11) rSString = "B";
        else if (rred == 12) rSString = "C";
        else if (rred == 13) rSString = "D";
        else if (rred == 14) rSString = "E";
        else if (rred == 15) rSString = "F";
        else rSString = String.valueOf(rred);

        rFString = rFString + rSString;

        green = g / 16;
        rgreen = g % 16;

        if (green == 10) gFString = "A";
        else if (green == 11) gFString = "B";
        else if (green == 12) gFString = "C";
        else if (green == 13) gFString = "D";
        else if (green == 14) gFString = "E";
        else if (green == 15) gFString = "F";
        else gFString = String.valueOf(green);

        if (rgreen == 10) gSString = "A";
        else if (rgreen == 11) gSString = "B";
        else if (rgreen == 12) gSString = "C";
        else if (rgreen == 13) gSString = "D";
        else if (rgreen == 14) gSString = "E";
        else if (rgreen == 15) gSString = "F";
        else gSString = String.valueOf(rgreen);

        gFString = gFString + gSString;

        blue = b / 16;
        rblue = b % 16;

        if (blue == 10) bFString = "A";
        else if (blue == 11) bFString = "B";
        else if (blue == 12) bFString = "C";
        else if (blue == 13) bFString = "D";
        else if (blue == 14) bFString = "E";
        else if (blue == 15) bFString = "F";
        else bFString = String.valueOf(blue);

        if (rblue == 10) bSString = "A";
        else if (rblue == 11) bSString = "B";
        else if (rblue == 12) bSString = "C";
        else if (rblue == 13) bSString = "D";
        else if (rblue == 14) bSString = "E";
        else if (rblue == 15) bSString = "F";
        else bSString = String.valueOf(rblue);
        bFString = bFString + bSString;
        result = rFString + gFString + bFString;
        return result;
    }

    public static int getShowPosition(String taAlignment) {
        MyLog.cdl("=====显示得位置===" + taAlignment);
        int backPosition = Gravity.CENTER;
        taAlignment = taAlignment.trim();
        if (taAlignment == null || taAlignment.length() < 1) {
            return backPosition;
        }
        try {
            int defaultPoi = Integer.parseInt(taAlignment);
            switch (defaultPoi) {
                case TextInfo.POI_LEFT_TOP:
                    backPosition = Gravity.LEFT | Gravity.TOP;
                    break;
                case TextInfo.POI_TOP_CENTER:
                    backPosition = Gravity.CENTER | Gravity.TOP;
                    break;
                case TextInfo.POI_RIGHT_TOP:
                    backPosition = Gravity.RIGHT | Gravity.TOP;
                    break;
                case TextInfo.POI_LEFT_CENTER:
                    backPosition = Gravity.LEFT | Gravity.CENTER;
                    break;
                case TextInfo.POI_CENTER:
                    backPosition = Gravity.CENTER;
                    break;
                case TextInfo.POI_RIGHT_CENTER:
                    backPosition = Gravity.RIGHT | Gravity.CENTER;
                    break;
                case TextInfo.POI_LEFT_BOTTOM:
                    backPosition = Gravity.LEFT | Gravity.BOTTOM;
                    break;
                case TextInfo.POI_BOTTOM_CENTER:
                    backPosition = Gravity.BOTTOM | Gravity.CENTER;
                    break;
                case TextInfo.POI_RIGHT_BOTTOM:
                    backPosition = Gravity.RIGHT | Gravity.BOTTOM;
                    break;
                default:
                    backPosition = Gravity.CENTER;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backPosition;
    }

    /**
     * 判断两个数组是否是一致的
     *
     * @param taskWorkEntityList
     * @param taskListSave
     */
    public static boolean conpairListSame(List<TaskWorkEntity> taskWorkEntityList, List<TaskWorkEntity> taskListSave) {
        if (taskWorkEntityList == null || taskWorkEntityList.size() < 1) {
            return false;
        }
        if (taskListSave == null || taskListSave.size() < 1) {
            return false;
        }
        if (taskWorkEntityList.size() != taskListSave.size()) {
            return false;
        }

        Collections.sort(taskWorkEntityList, new Comparator<TaskWorkEntity>() {

            @Override
            public int compare(TaskWorkEntity taskWorkEntity1, TaskWorkEntity taskWorkEntity2) {
                long sendTime1 = taskWorkEntity1.getSendTime();
                long sendTime2 = taskWorkEntity2.getSendTime();
                return (int) (sendTime1 - sendTime2);
            }
        });
        Collections.sort(taskListSave, new Comparator<TaskWorkEntity>() {

            @Override
            public int compare(TaskWorkEntity taskWorkEntity1, TaskWorkEntity taskWorkEntity2) {
                long sendTime1 = taskWorkEntity1.getSendTime();
                long sendTime2 = taskWorkEntity2.getSendTime();
                return (int) (sendTime1 - sendTime2);
            }
        });
        boolean isSame = true;
        for (int i = 0; i < taskWorkEntityList.size(); i++) {
            String taskId = taskWorkEntityList.get(i).getTaskId();
            String taskIdSave = taskListSave.get(i).getTaskId();
            MyLog.task("========便利之后得任务======" + taskId + " / " + taskIdSave);
            if (!taskId.contains(taskIdSave)) {
                isSame = false;
            }
        }
        return isSame;
    }

    /***
     * 判断任务素材是否存在或者下载完毕
     * @param mpListEntities
     * @return
     */
    public static boolean compairMpListFileExict(List<MpListEntity> mpListEntities) {
        if (mpListEntities == null || mpListEntities.size() < 1) {
            return true;
        }
        boolean isBack = true;
        for (MpListEntity mpListEntity : mpListEntities) {
            if (mpListEntities == null) {
                isBack = false;
                break;
            }
            String backFilePath = mpListEntity.getUrl();
            if (backFilePath == null || backFilePath.length() < 3) {
                isBack = false;
                break;
            }
            String downName = backFilePath.substring(backFilePath.lastIndexOf("/") + 1, backFilePath.length());
            String saveFilePath = AppInfo.BASE_TASK_URL() + "/" + downName;
            String fileLength = mpListEntity.getSize();
            boolean isFileExice = FileUtil.ifFileHasExict(saveFilePath, fileLength);
            if (!isFileExice) {
                isBack = false;
                break;
            }
        }
        return isBack;
    }

    private static int distanceSize = 10;
    private static int sizeDefault = 100;

    public static PositionEntity getLogoShowPosition(String showStyle, String allPath) {
        if (showStyle == null || showStyle.length() < 1) {
            showStyle = "1";
        }
        File file = new File(allPath);
        if (file.exists()) {
            //文件存在
            Bitmap bitmap = BitmapFactory.decodeFile(allPath);
            sizeDefault = bitmap.getWidth();
        }
        int leftPosition = distanceSize;
        int topPosition = distanceSize;
        int screenHeight = SharedPerManager.getScreenHeight();
        int screenWidth = SharedPerManager.getScreenWidth();
        switch (showStyle) {
            case "1":
                leftPosition = distanceSize;
                topPosition = distanceSize;
                break;
            case "2":
                leftPosition = screenWidth - sizeDefault - leftPosition;
                topPosition = distanceSize;
                break;
            case "3":
                leftPosition = screenWidth - sizeDefault - leftPosition;
                topPosition = screenHeight - sizeDefault - topPosition;
                break;
            case "4":
                leftPosition = distanceSize;
                topPosition = screenHeight - sizeDefault - topPosition;
                break;
        }
        PositionEntity positionEntity = new PositionEntity(leftPosition, topPosition, sizeDefault, sizeDefault);
        return positionEntity;
    }

    /***
     * 获取倒计时的显示时间
     * @return
     */
    public static String getReduiceTime(long countDown, boolean isShowDev, String dWei) {
        long cacheCountDown = countDown;
        countDown = Math.abs(countDown);
        String showTime = "";
        if (isShowDev) {
            //显示单位
            if (dWei.contains("1")) {  //用天来计算
                showTime = getShowDayTimeDown(countDown);
            } else if (dWei.contains("2")) {  //用天 时 分 秒 来计算
                showTime = getShowTimeDown(countDown, 1);
            }
        } else { //不显示单位
            showTime = getShowTimeDown(countDown, 0);
        }
        if (cacheCountDown < 0) {
            showTime = "-" + showTime;
        }
        return showTime;
    }

    /**
     * 以天来计算
     *
     * @param seconds
     * @return
     */
    private static String getShowDayTimeDown(long seconds) {
        long day = seconds / (60 * 60 * 24);
        long yu = seconds % (60 * 60 * 24);
        day = yu > 0 ? (day + 1) : day;
        if (day == 0) {
            return "0天";
        }
        if (day < 10) {
            return "0" + day + "天 ";
        }
        return day + "天 ";
    }

//    private static String getShowDayTimeDown(long seconds) {
//        long day = 0;
//        day = seconds / (60 * 60 * 24);
//        String showDay = "";
//        if (day < 1 && day > -1) {
//            showDay = "0天";
//        } else if (day > 0 && day < 10) {
//            showDay = "0" + day + "天 ";
//        } else {
//            showDay = day + "天 ";
//        }
//        return showDay;
//    }


    /**
     * @param seconds * @param tag
     *                0:  不显示单位
     *                1:  显示单位
     * @return
     */
    private static String getShowTimeDown(long seconds, int tag) {
        long day = 0;
        long hours = 0;
        long minutes = 0;
        day = seconds / (60 * 60 * 24);
        seconds -= day * 60 * 60 * 24;

        hours = seconds / (60 * 60);
        seconds -= hours * 60 * 60;

        minutes = seconds / 60;
        seconds -= minutes * 60;

        String showDay = "";
        if (day < 1) {
            showDay = "";
        } else if (day > 0 && day < 10) {
            showDay = "0" + day + "天";
        } else {
            showDay = day + "天";
        }
        String showHour = "00";
        if (hours < 10) {
            showHour = "0" + hours;
        } else {
            showHour = hours + "";
        }
        String showMin = "00";
        if (minutes < 10) {
            showMin = "0" + minutes;
        } else {
            showMin = minutes + "";
        }
        String showSec = "00";
        if (seconds < 10) {
            showSec = "0" + seconds;
        } else {
            showSec = seconds + "";
        }
        String showTime = "";
        if (tag == 0) {
            showTime = showDay + showHour + ":" + showMin + ":" + showSec;
        } else if (tag == 1) {
            showTime = showDay + showHour + "时" + showMin + "分" + showSec + "秒";
        }
        return showTime;
    }


    /**
     * 获取当前媒体的音量
     *
     * @param playList
     * @return
     */
    public static int getMediaVolNum(List<MediAddEntity> playList, int currentPlayIndex) {
        int volNum = 70;
        if (playList != null && playList.size() > 0) {
            String volNumString = playList.get(currentPlayIndex).getVolNum().trim();
            if (volNumString == null || volNumString.length() < 1) {
                volNumString = "70";
            }
            volNum = Integer.parseInt(volNumString);
            if (volNum < 0) {
                volNum = 0;
            }
        }
        return volNum;
    }


    /**
     * 单机模式，比例缩放布局设置
     *
     * @param mp
     */
    public static LinearLayout.LayoutParams getLayoutSize(MediaPlayer mp, int viewSizeWidth, int viewSizeHeight) {
        MyLog.video("========单机视频=======设置尺寸===");
        try {
            int vWidth = mp.getVideoWidth();
            int vHeight = mp.getVideoHeight();
            MyLog.video("========单机视频=======父布局的尺寸===" + vWidth + "/" + vHeight + " ----" + viewSizeWidth + " / " + viewSizeHeight);
            float wRatio;
            float hRatio;
            float ratio;
            if (vWidth > viewSizeWidth || vHeight > viewSizeHeight) {
                wRatio = (float) vWidth / (float) viewSizeWidth;
                hRatio = (float) vHeight / (float) viewSizeHeight;
                ratio = Math.max(wRatio, hRatio);
                vWidth = (int) Math.ceil((float) vWidth / ratio);
                vHeight = (int) Math.ceil((float) vHeight / ratio);
            } else {
                wRatio = (float) viewSizeWidth / (float) vWidth;
                hRatio = (float) viewSizeHeight / (float) vHeight;
                ratio = Math.min(wRatio, hRatio);
                vWidth = (int) Math.ceil((float) vWidth * ratio);
                vHeight = (int) Math.ceil((float) vHeight * ratio);
            }
            MyLog.video("=========单机视频==最终显示的尺寸=" + vWidth + " /" + vHeight);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(vWidth, vHeight);
            lp.gravity = Gravity.CENTER;
            return lp;
//            textureView.setLayoutParams(lp);
        } catch (Exception e) {
            MyLog.video("========单机视频=======尺寸异常===" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 获取视频播放控件
     * @param context
     * @param cpEntity
     * @param leftPosition
     * @param topPosition
     * @param width
     * @param height
     * @param videoList
     * @param programPositionMain
     * @return
     */
    public static Generator getVideoPlayView(Activity context, CpListEntity cpEntity, int leftPosition, int topPosition, int width, int height, List<MediAddEntity> videoList, String programPositionMain) {
        Generator generatorView = null;
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
            int screenWidth = SharedPerManager.getScreenWidth();
            int screenHeight = SharedPerManager.getScreenHeight();
            if (screenWidth > screenHeight) {    //横屏
                MyLog.video("======screen=====M11===横屏==Surface");
                generatorView = new ViewVideoSurfaceGenertrator(context, cpEntity, leftPosition, topPosition, width, height, videoList, AppInfo.PROGRAM_POSITION_MAIN);
            } else {
                MyLog.video("=====screen======M11===竖屏==textUreView");
                generatorView = new ViewVideoGenertrator(context, cpEntity, leftPosition, topPosition, width, height, videoList, AppInfo.PROGRAM_POSITION_MAIN);
            }
            return generatorView;
        }
        boolean Support = SharedPerManager.getVideoMoreSize();
        MyLog.playTask("====视频的坐标的坐标==4k support=" + Support + " / videoList=" + videoList.get(0).getUrl());
        if (Support) {
            MyLog.video("=====screen======其他转=主板===Surface");
            generatorView = new ViewVideoSurfaceGenertrator(context, cpEntity, leftPosition, topPosition, width, height, videoList, AppInfo.PROGRAM_POSITION_MAIN);
        } else {
            MyLog.video("======screen=====其他转=主板===textUreView");
            generatorView = new ViewVideoGenertrator(context, cpEntity, leftPosition, topPosition, width, height, videoList, AppInfo.PROGRAM_POSITION_MAIN);
        }
        return generatorView;
    }

    /***
     * 解析正常得图片控件
     * 解决新版本Bannner 只有一张得时候，不切换得问题
     * @param context
     * @param cpEntity
     * @param leftPosition
     * @param topPosition
     * @param width
     * @param height
     * @param imageList
     * @param b
     * @return
     */
    public static Generator getImageGenertorViewParsener(Activity context, CpListEntity cpEntity, int leftPosition, int topPosition, int width, int height, List<MediAddEntity> imageList, boolean b) {
        Generator generatorView = null;
        MediAddEntity mediAddEntity = imageList.get(0);
        generatorView = new ViewImageSingleGenertrator(context, cpEntity, leftPosition, topPosition, width, height, mediAddEntity);
        MyLog.banner("======只有一张图片，加载singleView控件");
        return generatorView;
    }
}
