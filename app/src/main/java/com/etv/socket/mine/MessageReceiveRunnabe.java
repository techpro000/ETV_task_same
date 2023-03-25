package com.etv.socket.mine;

import android.util.Log;

import com.etv.config.ApiInfo;
import com.etv.util.MyLog;
import com.etv.util.aes.AESTool;

import java.io.DataInputStream;
import java.net.Socket;

public class MessageReceiveRunnabe implements Runnable {

    DataInputStream inputStream = null;
    Socket socket = null;
    SocketMessageReceiveListener listener = null;

    public MessageReceiveRunnabe(Socket socket, SocketMessageReceiveListener listener) {
        this.socket = socket;
        this.listener = listener;
    }

    @Override
    public void run() {
        receiveMessage();
    }

    private void receiveMessage() {
        try {
            if (socket == null || socket.isClosed()) {
                if (listener != null) {
                    listener.backMessageReceiver(false, "Socket is Broken", null);
                }
                return;
            }
            inputStream = new DataInputStream(socket.getInputStream());
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buf)) != -1) {
                String desc = AESTool.DecryptString(buf);
                buf = AESTool.DecryptByteGroup(buf);
//                MyLog.netty("====线程中接收到消息222=====" + desc);
                if (listener != null) {
                    listener.backMessageReceiver(true, desc, buf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("aaaa", "run: 接受失败");
        }
    }

    public void onStopReceiveMessage() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
