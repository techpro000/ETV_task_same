package com.etv.util.system;

import android.util.Log;

import com.etv.util.RootCmd;

import java.io.File;

/***
 * 用来处理M11-测试灯控工具类
 */
public class LightUtil {

    static boolean isRunAuto = false;

    public static void startChangeLightStatues() {
        isRunAuto = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRunAuto) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    changeLightStatues();
                }
            }
        }.start();
    }

    static String lastLightStatues = "0";

    private static void changeLightStatues() {
        try {
            if (lastLightStatues.startsWith("0")) {
                RootCmd.writeFile(new File(RootCmd.M11_TEST_LIGHT), "1");
                lastLightStatues = "1";
            } else {
                lastLightStatues = "0";
                RootCmd.writeFile(new File(RootCmd.M11_TEST_LIGHT), "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void StopLightUtil() {
        try {
            RootCmd.writeFile(new File(RootCmd.M11_TEST_LIGHT), "1");
            isRunAuto = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
