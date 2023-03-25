//package com.etv.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.etv.config.AppInfo;
//import com.etv.util.MyLog;
//
//public class SDReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (!AppInfo.isAppRun) { //如果程序没有起来，就不执行
//            return;
//        }
//        MyLog.i("haha", "==============接收到广播====" + action);
//        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {  //U盘 SD卡拔出
//            Intent intent1 = new Intent();
//            intent1.setAction(AppInfo.SD_EFECT);
//            context.sendBroadcast(intent1);
//        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {  //U盘 SD卡插入
//            String USBPath = intent.getData().getPath();
//            MyLog.e("cdl", "==============USBPath========" + USBPath);
//            Intent intent2 = new Intent();
//            intent2.setAction(AppInfo.SD_MOUNTED);
//            context.sendBroadcast(intent2);
//        }
//    }
//}
