package com.etv.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.etv.util.apwifi.WifiMgr;
import com.etv.util.system.CpuModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取设备相关的编码
 */
public class CodeUtil {

    /***
     * 获取设备的唯一编码
     * 3128的cpu编码全是 000.这里直接不取CPU编码
     *
     * 0:先取保存的，取到一次成功后，保存在本地，以后直接调用
     * 1:先去取序列号，如果能娶到序列号就用序列号
     * 2：取不到序列号，就用设备硬件生成的UUID
     * @return
     */
    private static String UniquePsuedoIDCode = "";

    public static String OTHER_KEY_ADD = "012345678901234567890123";

    public static String getUniquePsuedoID() {
        String macAddress = getUniqueOnly();
        if (macAddress.length() < 16) {
            macAddress = macAddress + OTHER_KEY_ADD;
        }
        macAddress = macAddress.substring(0, 16).toUpperCase();
        MyLog.cdl("=====获取得MAC==最终====" + macAddress);
        return macAddress;
    }

    private static String getUniqueOnly() {
        if (UniquePsuedoIDCode != null && UniquePsuedoIDCode.length() > 2) {
            return UniquePsuedoIDCode;
        }
        String onlyCode = SharedPerManager.getUniquePsuedoID();
        if (!TextUtils.isEmpty(onlyCode)) {
            UniquePsuedoIDCode = onlyCode;
            return UniquePsuedoIDCode;
        }
        String macAddressEth = getEthMAC();
        if (TextUtils.isEmpty(macAddressEth)) {
            macAddressEth = getEthMAC();
        }
        String SerialNumber = getSerialNumber();
        MyLog.cdl("========获取得MAC==000=" + macAddressEth + " /序列号=" + SerialNumber);
        onlyCode = macAddressEth + SerialNumber;
        MyLog.cdl("========获取得MAC==111=" + onlyCode);
        if (onlyCode == null || onlyCode.length() < 3) {
            UniquePsuedoIDCode = onlyCode;
            MyLog.cdl("========获取得MAC==222=" + UniquePsuedoIDCode);
            return UniquePsuedoIDCode;
        }
        onlyCode = onlyCode.replace(":", "").trim();
        SharedPerManager.setUniquePsuedoID(onlyCode);
        UniquePsuedoIDCode = onlyCode;
        MyLog.cdl("========获取得MAC==333=" + UniquePsuedoIDCode);
        return UniquePsuedoIDCode;
    }

    /***
     * 获取设备得序列号
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                serial = Build.getSerial();
            } else {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            Log.e("setSerialNumber", "获取设备序列号失败:" + e.toString());
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(serial)) {
            serial = getEthMAC();
        }
        if (serial != null && serial.length() > 3 && serial.contains(":")) {
            serial = serial.replace(":", "");
        }
        serial = new StringBuilder(serial).reverse().toString();
        Log.e("setSerialNumber", "serial:" + serial);
        return serial;
    }

    /***
     * 获取设备的Mac地址
     * @return
     */
    public static String getEthMAC() {
        String macaddress = SharedPerManager.getDevMacAddress();
        if (!TextUtils.isEmpty(macaddress)) {
            return macaddress;
        }
        String macSerial = null;
        String str = "";
        try {
            Process ex = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address");
            InputStreamReader ir = new InputStreamReader(ex.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        macaddress = macSerial != null && !macSerial.equals("") && macSerial.length() == 17 ? macSerial.toUpperCase() : "";
        SharedPerManager.setDevMacAddress(macaddress);
        return macaddress;
    }


//    public static String getWifiMAC() {
//        String macSerial = null;
//        String str = "";
//        try {
//            Process ex = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
//            InputStreamReader ir = new InputStreamReader(ex.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//            while (null != str) {
//                str = input.readLine();
//                if (str != null) {
//                    macSerial = str.trim();
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return macSerial != null && !macSerial.equals("") && macSerial.length() == 17 ? macSerial.toUpperCase() : "";
//    }


    /***
     * 获取IP地址
     * @param context
     * @return
     */
    public static String getIpAddress(Context context, String tag) {
        String ipAddress = "0.0.0.0";
        if (!NetWorkUtils.isNetworkConnected(context)) {
            return ipAddress;
        }
        try {
            int netType = NetWorkUtils.getNetworkState(context);
            if (netType == NetWorkUtils.NETWORN_WIFI) {
                ipAddress = WifiMgr.getInstance(context).getCurrentIpAddress();
            } else if (netType == NetWorkUtils.NETWORK_MOBILE) {    //手机2.3.4G网络
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            ipAddress = inetAddress.getHostAddress();
                        }
                    }
                }
            } else if (netType == NetWorkUtils.NETWORK_ETH_NET) {
                //以太网,先使用系统API，
                ipAddress = getEthIpAddress(context, tag);
            }
        } catch (Exception e) {
            ipAddress = "0.0.0.0";
            e.printStackTrace();
        }
        //防止IP获取失败
        if (TextUtils.isEmpty(ipAddress) || ipAddress == null) {
            ipAddress = "192.168.1.251";
        }
        return ipAddress;
    }

    public static String getEthIpAddress(Context context, String tag) {
        String defaultIp = "192.168.255.255";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipAddress = inetAddress.getHostAddress().toString();
                        if (!ipAddress.contains("::"))
                            return inetAddress.getHostAddress().toString();
                    } else {
                        continue;
                    }
                }
            }
        } catch (SocketException ex) {
            MyLog.cdl("===获取以太网的IP errpr : " + ex.toString());
            ex.printStackTrace();
        }
        return defaultIp;
    }

    /***
     * 获取系统版本号
     * 板卡型号 + 系统版本 + 版本号 +软件版本号
     * @param context
     * @return
     */
    private static String SYSTEM_CODE_VERSION = "";

    public static String getSystCodeVersion(Context context) {
        if (SYSTEM_CODE_VERSION != null && SYSTEM_CODE_VERSION.length() > 2) {
            return SYSTEM_CODE_VERSION;
        }
        String codeBack = "";
        String sysCode = getSysVersion();
        String appVersion = "(" + getAppVersion(context) + ")";
        codeBack = sysCode + appVersion;
        SYSTEM_CODE_VERSION = codeBack;
        return codeBack;
    }

    public static String getAppVersion(Context context) {
        String appVersion = APKUtil.getVersionName(context) + "_"
                + APKUtil.getVersionCode(context);
        return appVersion;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVersion() {
        String sysCodeBack = "";
        String cpuModule = CpuModel.getMobileType();
        String systemVersion = getSystemVersion();
        String imgVersion = getImgVersion();
        sysCodeBack = cpuModule + "_" + systemVersion + "_" + imgVersion;
        Log.e("CDL", "系统版本号： " + cpuModule + " /systemVersion= " + systemVersion + " /imgVersion= " + imgVersion);
        return sysCodeBack;
    }

    /***
     * 获取固件版本号
     * @return
     */
    public static String getImgVersion() {
        String cpuMpdel = CpuModel.getMobileType();
        String version = Build.DISPLAY.toString().trim();
        try {
            if (TextUtils.isEmpty(version) || version.length() < 2) {
                return "-获取失败-";
            }
            MyLog.cdl("====version===cpuMpdel==" + cpuMpdel);
            if (cpuMpdel.contains(CpuModel.CPU_MODEL_PX30)) {
                return version;
            }
            if (cpuMpdel.startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
                return version;
            }
            MyLog.cdl("====version===123==" + version);
            //前面得应为全部都不要了
            version = version.substring(version.indexOf("20"));
            //先获取前面得年月日
            String date = version.substring(0, version.indexOf("."));
            //去掉年月日后边得数据
            String versionOther = version.substring(version.indexOf(".") + 1);
            //抽取后边得数字
            String backNum = getAlbNum(versionOther);
            version = date + "." + backNum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 从字符串中抽取数字
     *
     * @param versionNum
     * @return
     */
    private static String getAlbNum(String versionNum) {
        versionNum = versionNum.trim();
        String str2 = "";
        if (versionNum != null && !"".equals(versionNum)) {
            for (int i = 0; i < versionNum.length(); i++) {
                if (versionNum.charAt(i) >= 48 && versionNum.charAt(i) <= 57) {
                    str2 += versionNum.charAt(i);
                }
            }
        }
        return str2;
    }

    /***
     * 获取系统版本的时间
     * @return
     */
    public static String getImgVersionDate() {
        String version = getImgVersion();
        return version;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
//        String systemVersion = "4.4";   //7.0
//        int sdkVersion = android.os.Build.VERSION.SDK_INT;
//        if (sdkVersion == Build.VERSION_CODES.KITKAT) {   //19
//            systemVersion = "4.4";
//        } else if (sdkVersion == Build.VERSION_CODES.LOLLIPOP) {  //21
//            systemVersion = "5.0";
//        } else if (sdkVersion == Build.VERSION_CODES.LOLLIPOP_MR1) {  //22
//            systemVersion = "5.1";
//        } else if (sdkVersion == Build.VERSION_CODES.M) {  //23
//            systemVersion = "6.0";
//        } else if (sdkVersion == Build.VERSION_CODES.N) {  //24
//            systemVersion = "7.0";
//        } else if (sdkVersion == Build.VERSION_CODES.N_MR1) {  //25
//            systemVersion = "7.1";
//        } else if (sdkVersion == Build.VERSION_CODES.O) {  //26
//            systemVersion = "8.0";
//        } else {
//            systemVersion = "9.0";
//        }
//        return systemVersion;
        return android.os.Build.VERSION.RELEASE;
    }

}
