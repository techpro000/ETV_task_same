package com.etv.udp;

import com.etv.udp.util.ParseMessage;
import com.etv.util.MyLog;
import com.etv.util.SharedPerUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.udpsync.CmdAct;
import com.udpsync.Command;
import com.udpsync.OnUDPCallback;
import com.udpsync.UDPSocket;
import com.udpsync.bean.CmdData;

public class UdpParnsener {

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
                MyLog.udp("--------onReceive: " + dataStr);
                CmdData cmdData = Command.getData(dataStr);
                if (cmdData != null) {
                    ParseMessage.parsenerMessageUdp(cmdData);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
        udpSocket.startSocket();
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
