package com.etv.util.system;


import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

public class CpuModel {

    public static final String CPU_MODEL_PX30 = "px30";      //RK_PX30_8.1
    public static final String CPU_MODEL_RK_3288 = "rk3288";  //RK_3288
    public static final String CPU_RK_3566 = "rk3566";        //RK-3566
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
}
