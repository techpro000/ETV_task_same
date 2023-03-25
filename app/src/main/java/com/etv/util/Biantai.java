package com.etv.util;

import com.etv.config.AppInfo;

public class Biantai {

    private static long lasThreeTime;

    public static boolean isThreeClick() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lasThreeTime;
            if ((0L < l2) && (l2 < 3000L)) {
                return true;
            }
            lasThreeTime = l1;
        } catch (Exception e) {
        }
        return false;
    }


    private static long lasRequestTask;

    public static boolean isRequestTaskInfo() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lasRequestTask;
            if ((0L < l2) && (l2 < 1500L)) {
                return true;
            }
            lasRequestTask = l1;
        } catch (Exception e) {
        }
        return false;
    }


    private static long lasTwoTime;

    public static boolean isTwoClick() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lasTwoTime;
            if ((0L < l2) && (l2 < 2000L)) {
                return true;
            }
            lasTwoTime = l1;
        } catch (Exception e) {
        }
        return false;
    }


    private long nextSecenTime;

    /**
     * 混播节目延迟
     * @return
     */
    public boolean playNextDelayTime(String screenPosition) {
        //过滤掉副屏的节目切换时间
        if (screenPosition == AppInfo.PROGRAM_POSITION_SECOND) {
            return false;
        }
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - nextSecenTime;
            if ((0L < l2) && (l2 < 1000L)) {
                return true;
            }
            nextSecenTime = l1;
        } catch (Exception e) {
        }
        return false;
    }



    private static long lasOneTime;

    public static boolean isOneClick() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lasOneTime;
            if ((0L < l2) && (l2 < 1000L)) {
                return true;
            }
            lasOneTime = l1;
        } catch (Exception e) {
        }

        return false;
    }

    private static long lastHeartTime;

    /**
     * 设置心跳的频率
     *
     * @return
     */
    public static boolean checkHeartTime() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lastHeartTime;
            if ((0L < l2) && (l2 < 2000L)) {
                return true;
            }
            lastHeartTime = l1;
        } catch (Exception e) {
        }
        return false;
    }

    private static long lasMainTime;

    public static boolean isMainOnResume() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = l1 - lasMainTime;
            if ((0L < l2) && (l2 < 1000L)) {
                return true;
            }
            lasMainTime = l1;
        } catch (Exception e) {
        }
        return false;
    }

}
