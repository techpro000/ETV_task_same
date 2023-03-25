package com.etv.util.system;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {
    private static final String TAG = "DoubleScreenManager";

    public Utils() {
    }

    public static void writeIOFile(String str, String path) throws IOException, InterruptedException {
        File file = new File(path);
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        if (str.equals("0")) {
            do_exec("busybox echo 0 > " + path);
        } else {
            do_exec("busybox echo 1 > " + path);
        }

    }

    public static void do_exec(String cmd) {
        try {
            Process e = Runtime.getRuntime().exec("su");
            String str = cmd + "\nexit\n";
            e.getOutputStream().write(str.getBytes());
            Log.d("DoubleScreenManager", "cmd：" + cmd);
            if (e.waitFor() != 0) {
                Log.d("DoubleScreenManager", "cmd=" + cmd + " error!");
                throw new SecurityException();
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    static String readGpioPG(String path) {
        String str = "";
        File file = new File(path);

        try {
            FileInputStream e = new FileInputStream(file);
            byte[] buffer = new byte[1];
            e.read(buffer);
            e.close();
            str = new String(buffer);
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return str;
    }

    public static void setValueToProp(String key, String val) {
        try {
            Class classType = Class.forName("android.os.SystemProperties");
            Method e = classType.getDeclaredMethod("set", new Class[]{String.class, String.class});
            e.invoke(classType, new Object[]{key, val});
        } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
        } catch (NoSuchMethodException var5) {
            var5.printStackTrace();
        } catch (InvocationTargetException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public static String getValueFromProp(String key) {
        String value = "";

        try {
            Class classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class[]{String.class});
            value = (String) getMethod.invoke(classType, new Object[]{key});
        } catch (Exception var4) {
        }

        return value;
    }
}
