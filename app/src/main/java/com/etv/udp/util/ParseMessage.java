package com.etv.udp.util;

import android.util.Log;

import com.etv.util.MyLog;
import com.etv.util.SharedPerUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.udpsync.CmdAct;
import com.udpsync.bean.CmdData;
import com.udpsync.observe.CmdObserver;

public class ParseMessage {

    private static long lastTime;
//    视频进度
//    {"pos":41742,"time":1679721445423,"cmd":2,"path":"/storage/emulated/0/etv/task/1603219203070578688.mp4","secenId":"b4f681659dcf41f690fb"}
//    切换下一个
//     {"fileType":0,"pos":0,"cmd":1,"path":"/storage/emulated/0/etv/task/20211011080034523fab270e007490982f4.jpg","secenId":"b74b4501fca54795ad05","playParam":"10"}
//       {"fileType":2,"pos":0,"cmd":1,"path":"/storage/emulated/0/etv/task/1593067305618202624.mp4","secenId":"b4f681659dcf41f690fb","playParam":"10"}


    private static void logSerPortMessage(String s) {
        Log.e("cdl", "logSerPortMessage=" + s);
    }

    public static void parsenerMessageUdp(CmdData cmdData) {
        logSerPortMessage("解析完成111=cmdData=" + cmdData.cmd);
        switch (cmdData.cmd) {
            case CmdAct.CMD_PLAY:
                // 1
                AppStatuesListener.getInstance()
                        .startToUploadSameTask
                        .postValue(cmdData);
                break;
            case CmdAct.CMD_PROGRESS:
                // 2
                if (SharedPerUtil.getSameTaskMainLeader()) {
                    break;
                }
                AppStatuesListener.getInstance().playProgress.setValue(cmdData);
                break;
            case CmdAct.CMD_SEEK_PROGRESS:
                // 3
                long time = System.currentTimeMillis();
                if (time - lastTime > 500) {
                    CmdObserver.get().setValue(cmdData);
                    System.out.println("=============mesc seek->" + cmdData.pos);
                }
                lastTime = time;
                break;
        }
    }

}
