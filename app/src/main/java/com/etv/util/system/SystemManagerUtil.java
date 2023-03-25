package com.etv.util.system;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.EtvApplication;
import com.etv.activity.SplashLowActivity;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.ScreenEntity;
import com.ys.model.util.ActivityCollector;
import com.etv.util.MyLog;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.ys.rkapi.MyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/***
 * 息屏良平
 */
public class SystemManagerUtil {

    /**
     * 每次写入大数据的时候，调用一次
     */
    public static void syncSdcard() {
        try {
            Process process = Runtime.getRuntime().exec("sync");
            process.waitFor();
        } catch (Exception e) {
            Log.e("exect", e.getMessage(), e);
        }
    }

    /**
     * 开启或者关闭导航栏，顶部菜单栏
     *
     * @param context
     * @param isOpen
     */
    public static void openCloseChuangpinLeaderBar(Context context, boolean isOpen) {

    }

    /***
     * 获取CPUid
     * @return
     */
    public static String getCpuInfo() {
        String cpuInfo = "";
        try {
            File file = new File("/proc/cpuinfo");
            FileReader reader = new FileReader(file);
            char[] bb = new char[1024];
            int n;
            while ((n = reader.read(bb)) != -1) {
                cpuInfo += new String(bb, 0, n);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(cpuInfo) || cpuInfo.length() > 2) {
            cpuInfo = cpuInfo.substring(cpuInfo.lastIndexOf(":") + 1, cpuInfo.length()).trim();
        }
        return cpuInfo;
    }


    /**
     * 判断屏幕是横屏还是竖屏
     *
     * @param context true 横屏
     *                false 视频
     * @return
     */
    public static boolean isScreenHorOrVer(Context context, String screenType) {
        if (screenType.contains(AppInfo.PROGRAM_POSITION_MAIN)) {
            //主屏
            return getMainScreenHroOrVer(context);
        } else if (screenType.contains(AppInfo.PROGRAM_POSITION_SECOND)) {
            //副屏
            return getSecondScreenHrorVer();
        }
        return true;
    }

    /***
     * 获取副屏是否是横竖屏
     * @return
     */
    public static boolean getSecondScreenHrorVer() {
        List<ScreenEntity> listScreen = EtvApplication.getInstance().getListScreen();
        if (listScreen == null || listScreen.size() < 2) {
            return true;
        }
        ScreenEntity screenEntity = listScreen.get(1);
        if (screenEntity == null) {
            return true;
        }
        int screenWidth = screenEntity.getScreenWidth();
        int screenHeight = screenEntity.getScreenHeight();
        boolean isHorVer = false;
        if (screenWidth - screenHeight > 0) {
            isHorVer = true;
        }
        MyLog.cdl("=======获取副屏得屏幕状态信息===" + isHorVer + " / 屏幕信息==" + screenEntity.toString());
        return isHorVer;
    }

    /**
     * 获取主屏得行书评
     * true  横屏
     * false 竖屏
     *
     * @return
     */
    private static boolean getMainScreenHroOrVer(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }
        return true;

    }


    public static void do_exec(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            String str = paramString + "\nexit\n";
            localProcess.getOutputStream().write(str.getBytes());
            if (localProcess.waitFor() != 0) {
                System.out.println("cmd=" + paramString + " error!");
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启APP操作
     *
     * @param context
     */
    public static void rebootApp(Context context) {
        try {
            ActivityCollector.finishAll();
            Intent intent = new Intent(context, SplashLowActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openSecondaryScreenLight() {
        try {
            Utils.writeIOFile("1", "/sys/class/display/HDMI/enable");
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    private static void closeSecondaryScreenLight() {
        try {
            Utils.writeIOFile("0", "/sys/class/display/HDMI/enable");
        } catch (IOException var2) {
            var2.printStackTrace();
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }
    }


    /**
     * 设置屏幕的亮度
     */
    public static void setScreenBrightness(Activity context, int process) {
        //设置当前窗口的亮度值.这种方法需要权限android.permission.WRITE_EXTERNAL_STORAGE
        WindowManager.LayoutParams localLayoutParams = context.getWindow().getAttributes();
        float f = process / 255.0F;
        localLayoutParams.screenBrightness = f;
        context.getWindow().setAttributes(localLayoutParams);
        //修改系统的亮度值,以至于退出应用程序亮度保持
        saveBrightness(context.getContentResolver(), process);

    }

    /***
     * 保存屏幕亮度
     * @param resolver
     * @param brightness
     */
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        //改变系统的亮度值
        //这里需要权限android.permission.WRITE_SETTINGS
        //设置为手动调节模式
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //保存到系统中
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }


    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
     */
    private void setScreenMode(Activity context, int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度值  0--255
     */
    private void saveScreenBrightness(Activity context, int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    /**
     * 获得当前屏幕亮度值  0--255
     */
    public static int getScreenBrightness(Activity context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }


    /**
     * 获取屏幕亮度
     *
     * @return
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 获取屏幕的旋转角度
     * RK-3128
     *
     * @param context
     * @return
     */
    public static int getScreenRoate(Context context) {
        try {
            int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).
                    getDefaultDisplay().getRotation();
            switch (angle) {
                case Surface.ROTATION_0:
                    return 0;
                case Surface.ROTATION_90:
                    return 90;
                case Surface.ROTATION_180:
                    return 180;
                case Surface.ROTATION_270:
                    return 270;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * <p>
     * 获取主屏的旋转角度
     * 1  横屏
     * -1 竖屏
     * 0  方屏
     */
    public static int getMainScreenRoate() {
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        int mainScreenRoate = 1;
        if (screenWidth > screenHeight) {
            mainScreenRoate = 1;
        } else if (screenWidth < screenHeight) {
            mainScreenRoate = -1;
        } else {
            mainScreenRoate = 0;
        }
        return mainScreenRoate;
    }


    /***
     * 获取高通副屏的旋转角度
     * @param screenPosition
     *      * 1  横屏
     *      * -1 竖屏
     *      * 0  方屏
     * @return
     */
    public static int getScreenGTRoate(String screenPosition, int width, int height) {
        if (width == height) {
            return 0;
        }
        String roate = RootCmd.getProperty(screenPosition, "100");
//         ====GT屏幕旋转得角度====0   ==0
//         ====GT屏幕旋转得角度====1  ==90
//         ====GT屏幕旋转得角度====2   ==180
//         ====GT屏幕旋转得角度====3   ==270
        MyLog.cdl("====GT屏幕旋转得角度====" + roate);
        if (roate.contains("100")) {
            return 1;
        }
        /**
         *  0   0度
         * 1   旋转90度
         * 2   旋转180度
         * 3   旋转270度
         * 4   和主屏一样的角度
         */
        int roateNum = 1;
        try {
            roateNum = Integer.parseInt(roate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (roateNum == 0 || roateNum == 2 || roateNum == 180) {
            if (width > height) {
                return 1;
            }
            return -1;
        }
        if (roateNum == 90 || roateNum == 270 || roateNum == 1 || roateNum == 3) {
            if (width > height) {
                return -1;
            }
            return 1;
        }
        return 1;
    }

    /**
     * <p>
     * 1  横屏
     * -1 竖屏
     * 0  方屏
     *
     * @return RootCmd.PROOERTY_OTHER_INFO
     */
    public static int getScreenRoate(String screenPosition, int width, int height) {
        int screenRoate = 0;
        String roate = RootCmd.getProperty(screenPosition, "0");
        screenRoate = Integer.parseInt(roate);
        MyLog.cdl("====屏幕旋转得角度====" + screenRoate);
        /**
         *  0   0度
         * 1   旋转90度
         * 2   旋转180度
         * 3   旋转270度
         * 4   和主屏一样的角度
         */
        if (screenRoate == 0 || screenRoate == 2) {  //横屏
            screenRoate = 1;
        } else if (screenRoate == 1 || screenRoate == 3) { //竖屏
            screenRoate = -1;
        } else if (screenRoate == 4) {  //副屏和主屏一个方向
            int mainWidth = SharedPerManager.getScreenWidth();
            int mainHeight = SharedPerManager.getScreenHeight();
            if (mainWidth > mainHeight) { //横屏
                screenRoate = 1;
            } else if (mainWidth < mainHeight) { //竖屏
                screenRoate = -1;
            } else if (mainWidth == mainHeight) { //方屏
                screenRoate = 0;
            }
        }
        return screenRoate;
    }
}
