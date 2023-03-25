package com.etv.util.system;


import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

public class CpuModel {

    public static final String CPU_MODEL_AOSP = "aosp";      //朗国主板
    public static final String CPU_MODEL_PX30 = "px30";      //RK_PX30_8.1
    public static final String CPU_MODEL_SMD = "smd";        //1高通通用版本
    public static final String CPU_MODEL_MSM = "msm";        //1高通通用版本
    public static final String CPU_MODEL_WING = "wing";      //视美泰A20
    public static final String CPU_MODEL_RK_DEFAULT = "rk";
    public static final String CPU_MODEL_MLOGIC = "x301";    //mlogic_X301
    public static final String CPU_MODEL_RK_3128 = "rk312";  //RK_3128
    public static final String CPU_MODEL_RK_3399 = "rk3399";  //RK_3399
    public static final String CPU_MODEL_RK_3288 = "rk3288";  //RK_3288
    public static final String CPU_MODEL_MTK_M11 = "mt5862";  //M11主板
    public static final String CPU_MODEL_3568_11 = "rk3568";  //3568-android-11
    /***
     * 获取CPU型号
     * @return
     * rk3399
     * px30
     * aosp: 朗国
     * wing: 视美泰A20
     */
    private static String Cpumodel = "";

    public static String getMobileType() {
        if (Cpumodel != null && Cpumodel.length() > 1) {
            MyLog.cdl("========getMobileType=========" + Cpumodel);
            return Cpumodel;
        }
        String cpuModel = SharedPerManager.getCpuModel();
        if (cpuModel != null && cpuModel.length() > 2) {
            Cpumodel = cpuModel;
            MyLog.cdl("========getMobileType=========" + Cpumodel);
            return cpuModel;
        }
        cpuModel = android.os.Build.PRODUCT;
        if (cpuModel.contains("_")) {
            cpuModel = cpuModel.substring(0, cpuModel.indexOf("_"));
        }
        Cpumodel = cpuModel;
        SharedPerManager.setCpuModel(cpuModel);
        MyLog.cdl("========getMobileType=========" + Cpumodel);
        return cpuModel;
    }

    /***
     * 是否是 3128的主板
     * @return
     */
    public static boolean ISRK312X() {
        String cpuModel = getMobileType();
        if (cpuModel.startsWith(CPU_MODEL_RK_3128)) {
            return true;
        }
        return false;
    }


}
