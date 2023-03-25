package com.etv.util;

import android.os.Build;
import android.util.Log;

import com.etv.util.system.CpuModel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class RootCmd {

    private static boolean mHaveRoot = false;
    public static final String PROOERTY_INFO = "persist.sys.displayrot";                  //RK 主屏幕的旋转方向
    public static final String PROOERTY_OTHER_INFO = "persist.orientation.vhinit";        //RK 副屏幕的旋转方向

    public static final String PROOERTY_OTHER_INFO_3399 = "persist.sys.rotation.einit";   //RK3399 副屏幕的旋转方向
    public static final String PROOERTY_OTHER_INFO_GAOTONG = "persist.orientation.ex";    //高通，副屏的旋转方向

    public static final String PROOERTY_OTHER_INFO_PX30_MAIN = "persist.sys.displayrot";   //rk px30 主屏幕的旋转方向
    public static final String PROOERTY_OTHER_INFO_PX30_DOUBLE = "persist.orientation.ex";    //rk px30 副屏幕的旋转方向


    public static final String CPU_MODEL_TYPE = "persist.ys.factroy.code";   //cpu厂商识别码
    public static final String CAMERA_FONR_MIRROR = "persist.hal.fcam.mirror";   //前摄像头显示镜像  true=镜像  false=非镜像
    public static final String CAMERA_BACK_MIRROR = "persist.hal.bcam.mirror";   //后摄像头显示镜像  true=镜像  false=非镜像

    //    0和1,0是没信号，1是有信号
    public static final String JUJLE_HEMI_IN_IS_INSERT = "sys.hdmiin.displays";   //判断hdmi是否插入

    //判断设备是否连网线  0：没有连接   1：已经连接
    public static final String JUJLE_ETH_LINE_DEV_ORDER = "cat /sys/class/net/eth0/carrier";
    //双屏的属性
    public static final String SYS_HWC_DEV_AUX = "sys.hwc.device.aux";
    public static final String SYS_HWC_DEV_EXTEND = "sys.hwc.device.extend";
    //获取屏幕尺寸属性
    public static final String GET_SCREEN_SIZE = "persist.sys.display.fbx";
    public static final String GET_SCREEN_SECOND_SIZE = "persist.sys.framebuffer.aux";
    //高通主板，背光开关属性  0==关    86==开
    public static final String GAOTONG_TURN_LIGNT_INFO = "/sys/class/leds/lcd-backlight/brightness";
    //RK3399 背光属性 0=》开  1：关
    public static final String RK3399_TURN_LIGNT_INFO = "/sys/class/backlight/backlight/bl_power";

    //4.4 屏幕背光   0-开   1 关
    public static final String BRINT_LIGHT_3128_44 = "sys/class/backlight/rk28_bl/bl_power";
    //M11 测试灯光
    public static final String M11_TEST_LIGHT = "/sys/devices/gpiochip0/gpio/gpio199/value";

//    查看节点值，例如：cat /sys/class/leds/lcd-backlight/brightness
//    修改节点值，例如：echo 128 > sys/class/leds/lcd-backlight/brightness
    /**
     * 获取当前有没有触摸行为
     */
    public static final String FINGER_TOUCH_EVENT = "persist.sys.input";


    //    获取和设置SystemProperties属性的代码
    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 判断当前设备有没有权限
     * @return
     */
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                mHaveRoot = true;
            } else {
            }
        } else {
        }
        return mHaveRoot;
    }

    // 执行命令但不关注结果输出
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmdBackInfo(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            Log.e("cdl", cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /***
     * @param command
     * @return
     */
    public static boolean exusecmd(String command, String tag) {
        MyLog.guardian("===write===========" + tag);
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            MyLog.guardian("===write===========" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //  RootCmd.writeFileToSystem(srcfile.getPath(), "/system/media/bootanimation.zip");
    public static void writeFileToSystem(String filePath, String sysFilePath) {
        exusecmd("mount -o rw,remount /system", "writeFileToSystem");
        exusecmd("rm -rf /system/media/boomAnimal.zip", "writeFileToSystem");
        exusecmd("chmod 777 /system/media", "writeFileToSystem");
        exusecmd("cp  " + filePath + " " + sysFilePath, "writeFileToSystem");
    }

    /**
     * 安装守护进程
     *
     * @param filePath
     * @param sysFilePath
     */
    public static void writeFileToSystemApp(String filePath, String sysFilePath) {
        String cpuModel = CpuModel.getMobileType();
        if (cpuModel.contains(CpuModel.CPU_MODEL_PX30)) {
            exusecmd("mount -o rw,remount /system", "安装守护进程");
            exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
            exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程");
            exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程");
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            //android -11
            MyLog.guardian("==writeFileToSystemApp=== android-11以下的版本=========");
            exusecmd("mount -o remount -o rw /", "安装守护进程111");
            exusecmd("mount -o rw,remount /system", "安装守护进程");
            exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程222");
            exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程333==" + "cp  " + filePath + " " + sysFilePath);
            exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程444");
            return;
        }
        if (Build.VERSION.SDK_INT < 26) {
            //  7.1以及 7.1以下的版本
            MyLog.guardian("==writeFileToSystemApp=== 7.1以及 7.1以下的版本=========");
            exusecmd("mount -o rw,remount /system", "安装守护进程");
            exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
            exusecmd("cp -rf " + filePath + " " + sysFilePath, "安装守护进程");
            exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程");
            return;
        }
        if (Build.VERSION.SDK_INT < 28 && Build.VERSION.SDK_INT > 24) {
            MyLog.guardian("====writeFileToSystemApp===8.0-8.1=======");
            exusecmd("mount -o remount -o rw /", "安装守护进程");
            exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
            exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程");
            exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程");
            return;
        }
        if (Build.VERSION.SDK_INT > 27 && Build.VERSION.SDK_INT < 29) {
            if (CpuModel.getMobileType().contains(CpuModel.CPU_MODEL_RK_3399)) {
                MyLog.guardian("====writeFileToSystemApp===9.0版本==3399=====");
                exusecmd("blockdev --setrw /dev/block/by-name/system", "安装守护进程");
                exusecmd("mount -o rw,remount /", "安装守护进程");
                exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
                exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程");
                exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程");
                exusecmd("mount -o ro,remount /", "安装守护进程");
            } else {
                MyLog.guardian("====writeFileToSystemApp===9.0版本==3288=====");
                exusecmd("mount -o remount -o rw /", "安装守护进程");
                exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
                exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程");
                exusecmd("chmod 644 /system/app/guardian.apk", "安装守护进程");
                exusecmd("sync", "安装守护进程");
                exusecmd("mount -o remount -o ro /", "安装守护进程");
            }
            return;
        }

//        MyLog.guardian("====writeFileToSystemApp===9.0以上=======");
//        exusecmd("blockdev --setrw /dev/block/by-name/system", "安装守护进程");
//        exusecmd("mount -o rw,remount /", "安装守护进程");
//        exusecmd("rm -rf /system/app/guardian.apk", "安装守护进程");
//        exusecmd("cp  " + filePath + " " + sysFilePath, "安装守护进程");
//        exusecmd("chmod 777 /system/app/guardian.apk", "安装守护进程");
//        exusecmd("mount -o ro,remount /", "安装守护进程");

//        SystemProperties.set("persist.sys.command","replace_guardian");
//        SystemProperties.set("service.guardian.dirfile","cp guardian的路径");
//        sleep(1);
//        SystemProperties.set("ctl.start", "sys_command");
    }

    /**
     * 讲SO文件拷贝到lib目录下面
     *
     * @param filePath
     * @param sysFilePath
     */
    public static void writeFileFingerToSystemLib(String filePath, String sysFilePath) {
        exusecmd("mount -o rw,remount /system", "安装APK工具");
        exusecmd("rm -rf /system/lib/libinputflinger.so", "安装APK工具");
        exusecmd("cp  " + filePath + " " + sysFilePath, "安装APK工具");
        exusecmd("chmod 777 /system/lib/libinputflinger.so", "安装APK工具");
    }

    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }

    public static boolean delGuardianApk() {
        exusecmd("mount -o rw,remount /system", "delGuardianApk");
        boolean isdel = exusecmd("rm -rf /system/app/guardian.apk", "delGuardianApk");
        exusecmd("chmod 777 /system/app/guardian.apk", "delGuardianApk");
        return isdel;
    }

    /***
     * 清理缓存目录下的文件
     */
    public static void clearApkCache() {
        exusecmd("rm -rf /data/user/0/com.android.packageinstaller/cache/*.apk", "清理U盘升级APK遗留得缓存");
    }

    public static void writeFile(File file, String content) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


}
