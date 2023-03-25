package com.etv.util;

import android.util.Log;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;

import java.io.File;

public class MyLog {

    public static void i(String tag, String s) {
        if (AppConfig.IF_PRINT_LOG) {
            Log.i(tag, s);
        }
    }

    public static void e(String tag, String s) {
        if (AppConfig.IF_PRINT_LOG) {
            Log.e(tag, s);
        }
    }


    /***
     * 用来打印接受的指令
     * @param message
     */
    public static void message(String message) {
        message(message, false);
    }

    public static void message(String message, boolean print) {
        e("message", message);
        if (print) {
            printExceptionToSd(message);
        }
    }

    public static void http(String s) {
        e("http", s);
    }

    public static void image(String s) {
        i("image", s);
    }

    public static void update(String json) {
        update(json, false);
    }

    public static void update(String json, boolean isPrint) {
        e("update", json);
        if (isPrint) {
            printExceptionToSd(json);
        }
    }

    public static void login(String s) {
        e("login", s);
    }

    public static void media(String s) {
        e("video", s);
    }

    public static void powerOnOff(String s) {
        e("powerOnOff", s);
        printExceptionToSd(s);
    }

    public static void powerOnOff(String s, boolean isPrint) {
        e("powerOnOff", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void task(String s) {
        task(s, false);
    }

    public static void del(String s) {
        e("delete", s);
    }


    public static void task(String s, boolean isPrint) {
        e("task", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void playTask(String s) {
        playTask(s, false);
    }

    public static void police(String s) {
        e("police", s);
    }


    public static void playTask(String s, boolean isPrint) {
        e("playTask", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void playTaskBack(String s) {
        e("playTaskBack", s);
    }

    /**
     * 混播log
     *
     * @param s
     */
    public static void playMix(String s) {
        e("playMix", s);
    }

    public static void apk(String s) {
        e("apk", s);
    }

    public static void socket(String s) {
        socket(s, false);
    }

    public static void socket(String s, boolean isPrint) {
        e("SiteWebsocket", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void wifi(String s) {
        e("wifi", s);
    }

    public static void diff(String s) {
        diff(s, false);
    }

    public static void diff(String s, boolean isPrint) {
        e("diff", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void video(String s) {
        video(s, false);
    }

    public static void video(String s, boolean isPrint) {
        e("videoPlay", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void hdmi(String desc) {
        hdmi(desc, false);
    }

    public static void hdmi(String desc, boolean isPrint) {
        if (isPrint) {
            printExceptionToSd(desc);
        }
        e("hdmi_in", desc);
    }

    public static void cdl(String s) {
        cdl(s, false);
    }

    public static void face(String desc, boolean isPrint) {
        e("face", desc);
        if (isPrint) {
            printExceptionToSd(desc);
        }
    }

    public static void face(String desc) {
        face(desc, false);
    }

    public static void cdl(String s, boolean isPrint) {
        e("cdl", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void draw(String s) {
        e("draw", s);
    }

    public static void timer(String s) {
        timer(s, false);
    }

    public static void timer(String s, boolean isPrint) {
        e("timer", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void down(String s) {
        down(s, false);
    }

    public static void down(String s, boolean isPrint) {
        e("down", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void banner(String toString) {
        banner(toString, false);
    }

    public static void banner(String toString, boolean isPrint) {
        e("banner", toString);
        if (isPrint) {
            printExceptionToSd(toString);
        }
    }

    public static void location(String s) {
        e("location", s);
    }

    public static void taskDown(String s) {
        e("taskDown", s);
    }

    public static void test(String s) {
        e("test", s);
    }

    public static void wps(String s) {
        e("wps", s);
    }

    public static void file(String s) {
        e("file", s);
    }

    public static void db(String s) {
        db(s, false);
    }

    public static void db(String s, boolean isPrint) {
        e("db", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void udp(String s) {
        e("udp", s);
    }

    public static void temp(String msg) {
        e("temputure", msg);
    }

    public static void touch(String s) {
        e("touch", s);
    }

    public static void guardian(String s) {
        e("guardian", s);
    }

    public static void ExceptionPrint(String message) {
        e("ExceptionPrint", message);
        printExceptionToSd(message);
    }

    public static void sleep(String s) {
        sleep(s, false);
    }

    public static void sleep(String s, boolean isPrint) {
        e("sleep", s);
        if (isPrint) {
            printExceptionToSd(s);
        }
    }

    public static void netty(String desc) {
        netty(desc, false);
    }

    public static void nettyMessage(String desc) {
        e("netty", "Message: " + desc);
    }

    public static void netty(String desc, boolean isPrint) {
        e("netty", desc);
        if (isPrint) {
            printExceptionToSd(desc);
        }
    }

    public static void screen(String s) {
        e("screen", s);
        printExceptionToSd(s);
    }

    public static void aesCodeln(String s) {
        e("aesCodeln", s);
    }

    public static void bgg(String desc) {
        e("bgg", desc);
//        printExceptionToSd("==背景图处理: " + desc);
    }

    public static void sdckeck(String toString) {
        e("sdckeck", toString);
    }

    public static void phone(String s) {
        e("phone", s);
    }

    public static void gpio(String s) {
        e("gpio", s);
    }

    public static void zip(String s) {
        e("zip", s);
    }

    public static void tts(String tts) {
        tts(tts, false);
    }

    public static void usb(String s) {
        e("usb", s);
    }

    public static void tts(String tts, boolean isPrint) {
        e("tts", tts);
        if (isPrint) {
            printExceptionToSd(tts);
        }
    }

    private static void printExceptionToSd(String message) {
        try {
            String dataInfo = SimpleDateUtil.getCurrentDateLong() + ".txt";
            String woekPlace = AppInfo.BASE_CRASH_LOG() + "/" + dataInfo;
            File file = new File(woekPlace);
            if (file.exists() && file.isDirectory()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            long currentTime = SimpleDateUtil.getCurrentTimelONG();
            FileUtil.writeMessageInfoToTxt(woekPlace, currentTime + "===" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printUdpLogToSd(String fileName, String message) {
        try {
            String time = SimpleDateUtil.formatTaskTimeShow(System.currentTimeMillis()) + " ";
            String filePath = AppInfo.BASE_CRASH_LOG();
            FileUtil.writeLogInfoToFile(time + message, filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
