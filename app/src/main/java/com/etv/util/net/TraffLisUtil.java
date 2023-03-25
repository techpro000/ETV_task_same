package com.etv.util.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.etv.util.SharedPerManager;

import java.util.List;

/**
 * 流量统计
 */
public class TraffLisUtil {

    public static AppTrafficModel trafficMonitor(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
            for (PackageInfo info : packinfos) {
                String packageName = info.packageName;
                if (packageName.contains(SharedPerManager.getPackageNameBySp())) {
                    int uid = info.applicationInfo.uid;
                    long rx = TrafficStats.getUidRxBytes(uid);
                    long tx = TrafficStats.getUidTxBytes(uid);
                    AppTrafficModel appTrafficModel = new AppTrafficModel();
                    appTrafficModel.setAppInfo(info.applicationInfo);
                    appTrafficModel.setDownload(rx);
                    appTrafficModel.setUpload(tx);
                    /** 获取手机通过 2G/3G 接收的字节流量总数 */
                    TrafficStats.getMobileRxBytes();
                    /** 获取手机通过 2G/3G 接收的数据包总数 */
                    TrafficStats.getMobileRxPackets();
                    /** 获取手机通过 2G/3G 发出的字节流量总数 */
                    TrafficStats.getMobileTxBytes();
                    /** 获取手机通过 2G/3G 发出的数据包总数 */
                    TrafficStats.getMobileTxPackets();
                    /** 获取手机通过所有网络方式接收的字节流量总数(包括 wifi) */
                    TrafficStats.getTotalRxBytes();
                    /** 获取手机通过所有网络方式接收的数据包总数(包括 wifi) */
                    TrafficStats.getTotalRxPackets();
                    /** 获取手机通过所有网络方式发送的字节流量总数(包括 wifi) */
                    TrafficStats.getTotalTxBytes();
                    /** 获取手机通过所有网络方式发送的数据包总数(包括 wifi) */
                    TrafficStats.getTotalTxPackets();
                    /** 获取手机指定 UID 对应的应程序用通过所有网络方式接收的字节流量总数(包括 wifi) */
                    TrafficStats.getUidRxBytes(uid);
                    /** 获取手机指定 UID 对应的应用程序通过所有网络方式发送的字节流量总数(包括 wifi) */
                    TrafficStats.getUidTxBytes(uid);
                    return appTrafficModel;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
