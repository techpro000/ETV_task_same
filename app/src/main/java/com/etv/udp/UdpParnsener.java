package com.etv.udp;

import com.etv.test.SameTaskVideo;
import com.etv.test.StartUploadEntity;
import com.etv.util.MyLog;
import com.etv.util.SharedPerUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.udpsync.CmdAct;
import com.udpsync.Command;
import com.udpsync.OnUDPCallback;
import com.udpsync.UDPSocket;
import com.udpsync.bean.CmdData;
import com.udpsync.observe.CmdObserver;

public class UdpParnsener {

    private long lastTime;

    public UdpParnsener() {

    }

    UDPSocket udpSocket;

    public void initUdp() {
        if (udpSocket == null) {
            MyLog.udp("--------initUdp: ");
            udpSocket = new UDPSocket();
        }
        udpSocket.setCallback(new OnUDPCallback() {
            @Override
            public void onReceive(byte[] data) {
                String dataStr = new String(data);
                CmdData cmdData = Command.getData(dataStr);
                if (cmdData != null) {
                    parsenerMessageUdp(cmdData);
                }
                MyLog.udp("--------onReceive: " + dataStr);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
        udpSocket.startSocket();
    }

    private void parsenerMessageUdp(CmdData cmdData) {
        switch (cmdData.cmd) {
            case CmdAct.CMD_PLAY:
                AppStatuesListener.getInstance()
                        .startToUploadSameTask
                        .setValue(cmdData);
                break;
            case CmdAct.CMD_PROGRESS:
                if (SharedPerUtil.getSameTaskMainLeader()) {
                    break;
                }
                AppStatuesListener.getInstance().playProgress.setValue(cmdData);
                break;
            case CmdAct.CMD_SEEK_PROGRESS:
                long time = System.currentTimeMillis();
                if (time - lastTime > 500) {
                    CmdObserver.get().setValue(cmdData);
                    System.out.println("=============mesc seek->" + cmdData.pos);
                }
                lastTime = time;
                break;
        }
    }

    public void sendUdpMessageByIp(String sendIp, String message) {
        if (udpSocket == null) {
            MyLog.udp("=sendUdpMessageByIp=null");
            return;
        }
        MyLog.udp("=sendUdpMessageByIp=" + message + " /ip=" + sendIp);
        udpSocket.send(sendIp, message);
    }

    public void onDestroyParsener() {
        if (udpSocket != null) {
            udpSocket.release();
        }
    }


}
