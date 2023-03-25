package com.etv.socket.mine;

import com.etv.util.MyLog;

import java.io.DataOutputStream;
import java.net.Socket;

public class MessageSendRunnable implements Runnable {

    byte[] msg;
    Socket socket;
    MessageSocketListener listener;
    DataOutputStream outputStream;

    public MessageSendRunnable() {

    }

    public void setMessageInfo(Socket socket, byte[] msg, MessageSocketListener listener) {
        this.socket = socket;
        this.msg = msg;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (socket == null) {
            if (listener != null) {
                listener.messageSendFailed(false, "Socket is null");
            }
            MyLog.netty("发消息==Socke null");
            return;
        }
        if (!socket.isConnected()) {
            if (listener != null) {
                listener.messageSendFailed(false, "Socket 断开连接");
            }
            MyLog.netty("发消息==Socke 断开连接");
            return;
        }
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(msg);
            outputStream.flush();
            if (listener != null) {
                listener.messageSendFailed(true, "发消息成功");
            }
        } catch (Exception e) {
            MyLog.netty("发消息==Socke error==" + e.toString());
            if (listener != null) {
                listener.messageSendFailed(false, "发消息异常：" + e.toString());
            }
            e.printStackTrace();
        }
    }

    public void stopSendMessage() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
