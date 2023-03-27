package com.ys.model.util;


public class BiantaiModel {

    private static long lastOtherTime;

    public static boolean otherTimeCheck(long timeDistance) {
        long current = System.currentTimeMillis();
        long distanceTime = current - lastOtherTime;
        if ((0L < distanceTime) && (distanceTime < timeDistance)) {
            return true;
        }
        lastOtherTime = current;
        return false;
    }

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


}
