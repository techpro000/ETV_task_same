package com.etv.udp.util;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.etv.activity.MainActivity;
import com.etv.service.EtvService;
import com.etv.util.system.SystemManagerInstance;
import com.etv.util.system.SystemManagerUtil;

public class KeyControl {
    private static final String TAG = "KeyControl";

    /**
     * 小程序发送指令
     *
     * @param code
     * @return
     */
    public static void sendCodeToDev(Context context, int code) {
        int backCode = 0;
        switch (code) {
            case 7001://小程序重启键
                backCode = 26;
                SystemManagerInstance.getInstance(context).rebootDev();
                return;
            case 7002: //小程序调大音量
                backCode = 24;
                break;
            case 7003://小程序调小音量
                backCode = 25;
                break;
            case 7004://小程序上键
                backCode = 19;
                break;
            case 7005://小程序下键
                backCode = 20;
                break;
            case 7006://小程序左键
                backCode = 21;
                break;
            case 7007://小程序右键
                backCode = 22;
                break;
            case 7008://小程序ok键
                backCode = 23;
                break;
            case 7009://小程序菜单键
                backCode = 82;
                break;
            case 7010://小程序home键
                backCode = 3;
                startToMainView(context);
                return;
            case 7011://小程序静音键
                backCode = 164;
                break;
            case 7012://返回按钮
                backCode = 4;
                break;
            default:
                backCode = -99;
                break;
        }
        sendMessageTo(backCode);
    }

    private static void startToMainView(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message) {
        try {
            Runtime r = Runtime.getRuntime();
            Process p;
            p = r.exec(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageTo(final int code) {
        if (code < 0) {
            return;
        }
        Log.i("main", "====执行方法======" + code);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(code);
            }
        };
        EtvService.getInstance().executor(runnable);
    }

//    private class SendThread extends Thread {
//        int sendCode;
//
//        public SendThread(int sendCode) {
//            this.sendCode = sendCode;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            Instrumentation inst = new Instrumentation();
//            inst.sendKeyDownUpSync(20);
//        }
//    }
//
//
//    /***
//     * 遥控指令
//     * @param cmd
//     * @return
//     */
//    public static int transcoding(String cmd) {
//        Log.i(TAG, "==按键==" + cmd);
//        int backCode = -1;
//        if (cmd.equals("tv_pointup")) {// 上
//            backCode = 19;
//        } else if (cmd.equals("tv_pointdown")) {// 下
//            backCode = 20;
//        } else if (cmd.equals("tv_pointleft")) {// 左
//            backCode = 21;
//        } else if (cmd.equals("tv_pointright")) {// 右
//            backCode = 22;
//        } else if (cmd.equals("tv_ok")) { // 确认
//            backCode = 23;
//        } else if (cmd.equals("tv_voladd")) {// 音量加
//            backCode = 24;
//        } else if (cmd.equals("tv_volreduce")) {// 音量减
//            backCode = 25;
//        } else if (cmd.equals("tv_volreduce")) {// 音量减
//            backCode = 25;
//        } else if (cmd.equals("tv_volclose")) {// 静音
//            backCode = 164;
//        } else if (cmd.equals("tv_back")) {// 返回
//            backCode = 4;
//        } else if (cmd.equals("tv_onoff")) { // 开关机
//            backCode = 26;
//        } else if (cmd.equals("tv_home")) { // home
//            backCode = 3;
//        } else if (cmd.equals("tv_menu")) { // 菜单
//            backCode = 82;
//        }
//        sendMessageTo(backCode);
//        return backCode;
//    }


}
