package com.etv.setting.util;

import android.text.TextUtils;

public class InterUtil {

    /***
     * 判断网关，IP是否正确
     * @param ipaddress
     * @return
     */
    public static boolean isIpNetMacSuccess(String ipaddress) {
        boolean isBace = true;
        try {
            if (TextUtils.isEmpty(ipaddress) || ipaddress.length() < 7) {
                return false;
            }
            String first = ipaddress.substring(0, ipaddress.indexOf("."));
            System.out.println("==" + first);
            String lastString = ipaddress.substring(first.length() + 1,
                    ipaddress.length());
            String second = lastString.substring(0, lastString.indexOf("."));
            System.out.println("==" + second);
            lastString = lastString.substring(second.length() + 1,
                    lastString.length());
            String third = lastString.substring(0, lastString.indexOf("."));
            System.out.println("==third==" + third);
            String fourth = lastString.substring(lastString.indexOf(".") + 1,
                    lastString.length());
            System.out.println("==third==" + fourth);
            int one = Integer.parseInt(first);
            int two = Integer.parseInt(second);
            int three = Integer.parseInt(third);
            int four = Integer.parseInt(fourth);
            if (one < 0 || two < 0 || three < 0 || four < 0) {
                isBace = false;
            }
            if (one > 255 || two > 255 || three > 255 || four > 255) {
                isBace = false;
            }
        } catch (Exception e) {
            isBace = false;
            System.out.println("====e===" + e.toString());
        }
        return isBace;
    }


}
