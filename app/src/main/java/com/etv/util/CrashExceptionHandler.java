package com.etv.util;

import com.etv.config.AppInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashExceptionHandler implements UncaughtExceptionHandler {
    private static CrashExceptionHandler carshInstance;

    @Override
    public void uncaughtException(Thread td, Throwable tb) {
        tb.printStackTrace();
        writeToSDCard(td, tb);
        if (td.getId() == 1) {// UI异常

        } else {

        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void writeToSDCard(Thread td, Throwable tb) {
        FileOutputStream fos = null;
        try {
            String crashLogPath = AppInfo.BASE_CRASH_LOG();
            File logFile = new File(crashLogPath);
            MyLog.e("crash", "tb===" + tb.toString());
            if (!logFile.exists()) {
                if (!logFile.mkdirs()) {
                    MyLog.e("crash", "create crash file fail");
                }
            }
            String logPath = crashLogPath + "/"
                    + new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault())
                    .format(new Date()) + ".txt";
            fos = new FileOutputStream(logPath);
            PrintStream ps = new PrintStream(fos);
            tb.printStackTrace(ps);
            ps.flush();
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public CrashExceptionHandler() {

    }

    public static CrashExceptionHandler getCrashInstance() {
        if (carshInstance == null) {
            synchronized (CrashExceptionHandler.class) {
                if (carshInstance == null) {
                    carshInstance = new CrashExceptionHandler();
                }
            }
        }
        return carshInstance;
    }
}