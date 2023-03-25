package com.etv.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.etv.config.AppConfig;
import com.etv.http.util.AppRunnable;
import com.etv.service.EtvService;
import com.etv.setting.SystemApkInstallActivity;
import com.etv.setting.app.AppManagerActivity;
import com.etv.util.system.CpuModel;
import com.ys.model.dialog.MyToastView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class APKUtil {
    Context context;

    public APKUtil(Context context) {
        this.context = context;
    }


    /***
     * 获取APP的软件版本号
     * @param context
     * @param packname
     * @return
     */
    public static int getOtherAppVersion(Context context, String packname) {
        int versionCode = 1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            versionCode = packinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void startAppFromService(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startApp(Context context, String packageName) {
        AppRunnable runnable = new AppRunnable(context, packageName);
        EtvService.getInstance().executor(runnable);
    }

    /***
     * 打开文件管理器
     * @param context
     */
    public static void openFileManagerApk(Context context) {
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
            String packageName = "com.jrm.localmm";
            Intent parama96 = new Intent("android.intent.action.MAIN");
            parama96.addCategory("android.intent.category.LAUNCHER");
            parama96.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            parama96.setComponent(new ComponentName(packageName, packageName + ".ui.main.FileBrowserActivity"));
            context.startActivity(parama96);
            return;
        }
        if (APKUtil.ApkState(context, "com.android.rockchip")) {
            Intent param = new Intent("android.intent.action.MAIN");
            param.addCategory("android.intent.category.LAUNCHER");
            param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            param.setComponent(new ComponentName("com.android.rockchip", "com.android.rockchip.RockExplorer"));
            context.startActivity(param);
        } else if (APKUtil.ApkState(context, "com.softwinner.TvdFileManager")) {
            Intent param = new Intent("android.intent.action.MAIN");
            param.addCategory("android.intent.category.LAUNCHER");
            param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            param.setComponent(new ComponentName("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI"));
            context.startActivity(param);
        } else if (APKUtil.ApkState(context, "com.cyanogenmod.filemanager")) {
            startApp(context, "com.cyanogenmod.filemanager");
        } else if (APKUtil.ApkState(context, "com.xbh.filemanager")) {
            startApp(context, "com.xbh.filemanager");
        } else {
            MyToastView.getInstance().Toast(context, "没有找到合适的文件管理器，请联系售后");
        }
    }


    /**
     * 获取所有进程包名
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getAllProcess(Context context) {
        ArrayList<String> list = new ArrayList<String>();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo runningApp : appProcesses) {
            list.add(runningApp.processName);
        }
        return list;
    }

    /***
     * 返回当前前台运行的app
     *
     * @return
     */
    public static String appIsRunForset(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        String packName = appProcesses.get(0).processName;
        return packName;
    }

    /**
     * 安装APK文件
     * teaonly.rk.droidipcam
     */
//    public void installApk(String filePath) {
//        try {
//            String authorities = "com.ys.etv.fileprovider";
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            File apkFile = new File(filePath);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                Uri contentUri = FileProvider.getUriForFile(context, authorities, apkFile);
//       context.grantUriPermission(getPackageName(context), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//            } else {
//                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//            }
//            context.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
//        } catch (Exception e) {
//            MyLog.cdl("======安装异常==" + e.toString());
//            e.printStackTrace();
//        }
//    }
    public void installApk(String filePath) {
        try {
            String authorities = "com.ys.etv.fileprovider";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File apkFile = new File(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, authorities, apkFile);
                context.grantUriPermission(getPackageName(context), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void installApkStatic(Context context, String filePath) {
        try {
            String authorities = "com.ys.etv.fileprovider";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File apkFile = new File(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, authorities, apkFile);
                context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            MyLog.cdl("======安装异常==" + e.toString());
            e.printStackTrace();
        }
    }

    /***
     * 判断APK有没有安装
     * @return
     */
    public static boolean ApkState(final Context context, String packageName) {
        if (context == null) {
            return false;
        }
        boolean isInstall = false;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    packageName, 0);
            if (packageInfo != null) {
                isInstall = true;
            } else {
                isInstall = false;
            }
        } catch (Exception e) {
            isInstall = false;
        }
        return isInstall;
    }

    //版本名
    public static String getVersionName(Context context) {
        if (context == null) {
            return "";
        }
        String packageName = getPackageInfo(context).versionName;
        return packageName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        if (context == null) {
            return -1;
        }
        return getPackageInfo(context).versionCode;
    }

    public static PackageInfo getPackageInfo(Context context) {
        if (context == null) {
            return null;
        }
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeFile(String str) throws IOException, InterruptedException {
        File file = new File("/sys/devices/fb.11/graphics/fb0/pwr_bl");
        file.setExecutable(true);
        file.setReadable(true);//设置可读权限
        file.setWritable(true);//设置可写权限
        if (str.equals("0")) {
            do_exec("busybox echo 0 > /sys/devices/fb.11/graphics/fb0/pwr_bl");
        } else {
            do_exec("busybox echo 1 > /sys/devices/fb.11/graphics/fb0/pwr_bl");
        }
    }

    public static void do_exec(String cmd) {
        try {
            /* Missing read/write permission, trying to chmod the file */
            Process su;
            su = Runtime.getRuntime().exec("su");
            String str = cmd + "\n" + "exit\n";
            su.getOutputStream().write(str.getBytes());

            if ((su.waitFor() != 0)) {
                System.out.println("cmd=" + cmd + " error!");
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openOrClose(String cmd) {
        try {
            APKUtil.writeFile(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static String ACTION_INSTALL_APP = "com.android.lango.installapp";

    public void installApkByLG(String path) {
        //朗国APK静默安装升级，这里不要删除
//         发送该广播时会携带被安装APP的安装路径，传递如下：
        Intent intent = new Intent(ACTION_INSTALL_APP);
//         传入需要安装的APP的绝对路径/xx/xx/xx.apk
        intent.putExtra("apppath", path);
        context.sendBroadcast(intent);
    }


    /***
     * 静默安装APK
     * @param apkPath
     * @return
     */
    public boolean installApkSlient(String apkPath) {
        //朗国APK静默安装升级，这里不要删除
        // public final static String ACTION_INSTALL_APP = "com.android.lango.installapp";
        // 发送该广播时会携带被安装APP的安装路径，传递如下：
        // Intent intent = new Intent("com.android.lango.installapp");
        // 传入需要安装的APP的绝对路径/xx/xx/xx.apk
        // intent.putExtra("apppath", "/xx/xx/xx.apk");
        // context.sendBroadcast(intent);

        boolean result = false;
        File f = new File(apkPath);
        if (f != null && f.exists()) {
            DataOutputStream dataOutputStream = null;
            BufferedReader errorStream = null;
            try {
                // 申请su权限
                Process process = Runtime.getRuntime().exec("su");
                dataOutputStream = new DataOutputStream(process.getOutputStream());
                // 执行pm install命令
                String command = "pm install -r " + apkPath + "\n";
                dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                dataOutputStream.flush();
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
                process.waitFor();
                errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String msg = "";
                String line;
                // 读取命令的执行结果
                while ((line = errorStream.readLine()) != null) {
                    msg += line;
                }
                // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
                if (!msg.contains("Failure")) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (errorStream != null) {
                        errorStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        return result;
    }

}
