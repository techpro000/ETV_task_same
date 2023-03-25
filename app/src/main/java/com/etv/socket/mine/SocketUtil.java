package com.etv.socket.mine;

import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.entity.RegisterEntity;
import com.etv.service.EtvService;
import com.etv.util.MyLog;
import com.etv.util.SharedPerUtil;
import com.etv.util.aes.AESTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/***
 * 查看设备是否在线
 * tail -f /opt/server/server.log
 * tail -f /opt/server/server.err
 * redis-cli
 * auth YskjDBpwd!1234567
 * select 1
 * hgetall etv:client:socket:00301BBA02DB38014699F7C4
 * hgetall etv:client:socket:301F9A81650CB002475FEB01
 * hgetall etv:client:socket:301F9A84B910704A0E517AFE
 * hgetall etv:client:socket:301F9A8073AF18937FE2A447
 *
 * 查看运行日志
 * 在服务器上运行 ss -tn ｜ grep 9222
 */
public class SocketUtil {

    public static SocketUtil instance;

    public static SocketUtil getInstance() {
        if (instance == null) {
            synchronized (SocketUtil.class) {
                if (instance == null) {
                    instance = new SocketUtil();
                }
            }
        }
        return instance;
    }

    Socket socket = null;
    SocketStatuesListener listener;
    Thread lineServerThread;

    public void initSocketAndReceiveMessage(SocketStatuesListener listener) {
        this.listener = listener;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    String ipaddress = SharedPerUtil.getSocketIpAddress();
                    int port = SharedPerUtil.getSocketPort();
                    SocketAddress endpoint = new InetSocketAddress(ipaddress, port);
                    socket.connect(endpoint, 5 * 1000);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean isLine = getLineSatues();
                    MyLog.netty("=====socket初始化over=连接状态==" + isLine);
                    if (!isLine) {
                        if (listener != null) {
                            listener.backLineStaues(false, "Line Failed : Connection timeout.Please Check Ip Or Net");
                        }
                        return;
                    }
                    MyLog.netty("========socket=服务器连接成功==发注册消息给服务器=");
                    sendRegisterMacToServer("连接成功，准备注册TCP给服务器", false);
                    AcceptSocketMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    ondestroySocket("连接异常: " + e.toString());
                    if (e instanceof SocketTimeoutException) {
                        if (listener != null) {
                            listener.backLineStaues(false, "SocketTimeoutException: Connection timeout");
                        }
                    } else if (e instanceof NoRouteToHostException) {
                        if (listener != null) {
                            listener.backLineStaues(false, "NoRouteToHostException: IP does not exist");
                        }
                    } else if (e instanceof ConnectException) {
                        if (listener != null) {
                            listener.backLineStaues(false, "ConnectException: The connection is abnormal or rejected. Please check");
                        }
                    }
                }
            }
        };
        lineServerThread = new Thread(runnable);
        lineServerThread.start();
        MyLog.netty("======initSocketAndReceiveMessage =====333========");
    }

    public void sendRegisterMacToServer(String tag, boolean encryption) {
        MyLog.netty("=发一个注册消息 1#MAC 给服务器======" + tag + " / " + encryption);
        RegisterEntity registerEntity = ApiInfo.getRegisteMessageAddNum();
        byte[] macInfo = registerEntity.getRegisterInfo();
        String errorDesc = registerEntity.getErrorDesc();
        if (macInfo == null) {
            listener.backLineStaues(false, errorDesc);
            return;
        }
        if (encryption) {
            macInfo = AESTool.EncryptString(macInfo);
        }
        sendMessage(macInfo, "初始化成功，发一个注册消息=" + new String(macInfo) + " / " + encryption, new MessageSocketListener() {
            @Override
            public void messageSendFailed(boolean isSuccess, String errorDesc) {
                MyLog.netty("初始化成功，发一个注册消息状态：" + isSuccess + " / " + errorDesc);
            }
        });
    }

    /***
     * 获取设备连接状态
     * @return
     */
    public boolean getLineSatues() {
        if (socket == null) {
            return false;
        }
        if (socket.isClosed()) {
            return false;
        }
        return socket.isConnected();
    }

    //接受消息
    boolean isReceiveMessage = false;
    InputStream inputStream = null;

    private void AcceptSocketMessage() {
        try {
            if (!getLineSatues()) {
                return;
            }
            isReceiveMessage = true;
            inputStream = socket.getInputStream();
            int length = 0;
            while (isReceiveMessage) {
                try {
                    if (socket.isClosed()) {
                        MyLog.nettyMessage("socketisClosed==");
                        break;
                    }
                    if (socket.isInputShutdown()) {
                        MyLog.nettyMessage("isInputShutdown==");
                        break;
                    }
                    byte[] buffer = new byte[1024];
                    if ((length = inputStream.read(buffer)) > 0) {
                        buffer = Arrays.copyOf(buffer, length);
                        String desc = AESTool.DecryptString(buffer);
                        buffer = AESTool.DecryptByteGroup(buffer);
                        backMessageReceiverToView(desc, buffer);
                        MyLog.nettyMessage("收到得消息==" + desc);
                    }
                } catch (Exception eee) {
                    MyLog.nettyMessage("=read socket err：" + eee.toString());
                    break;
                }
            }
            inputStream.close();
        } catch (Exception e) {
            MyLog.nettyMessage("errorMessage：" + e.toString());
            e.printStackTrace();
        }
    }

    private void backMessageReceiverToView(String message, byte[] messageByte) {
        AppConfig.isOnline = true;
//        MyLog.nettyMessage("==backMessageReceiver===" + message);
        try {
            if (message.startsWith("235")) {
                //注册服务器返回数据
                sendHeartMessage("发心跳消息给服务器", null);
                if (listener != null) {
                    listener.backLineStaues(true, "Line Server Success");
                }
            } else if (message.startsWith("335")) {
                //SOCKET 发过来得心跳询问，这里需要回 435
                byte[] macInfo = ApiInfo.getHearBacktMessage();
                sendMessage(macInfo, "SOCKET 发过来得335，这里需要回435", null);
            } else if (message.startsWith("435")) {
                //这里表示，服务器返回的心跳消息
                if (listener != null) {
                    listener.heartMessageBack();
                }
            } else if (message.startsWith("535")) {
                if (messageByte == null || messageByte.length < 5) {
                    MyLog.netty("接收的消息==null");
                    return;
                }
                //服务器下发的==指令消息
                String res = new String(messageByte);
                MyLog.netty("=========收到的指令消息=====" + res, true);
                parsenerMessageFromWeb(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 发送空消息，保持长连接
     * @param
     */
    public void sendNullMessage() {
        byte[] macInfo = {9, 9, 9};
        macInfo = AESTool.EncryptString(macInfo);
        sendMessage(macInfo, "发送NULL消息，保持长连接", null);
    }


    /***
     * 发送心跳消息
     */
    public void sendHeartMessage(String tag, MessageSocketListener listener) {
        byte[] macInfo = ApiInfo.getHeartMessage();
        sendMessage(macInfo, tag, listener);
    }

    /**
     * 去解析服务器发过来的消息
     *
     * @param messagePersener
     */
    private void parsenerMessageFromWeb(String messagePersener) {
        if (messagePersener == null || messagePersener.length() < 5) {
            MyLog.netty("========需要解析的数据==null");
            return;
        }
        if (!messagePersener.contains("{") || !messagePersener.contains("}")) {
            MyLog.netty("========需要解析的数据不合法{ - }");
            return;
        }
        MyLog.netty("========需要解析的数据000==" + messagePersener);
        messagePersener = messagePersener.substring(messagePersener.indexOf("{"), messagePersener.lastIndexOf("}") + 1);
        MyLog.netty("========需要解析的数据111==" + messagePersener);
        if (listener != null) {
            listener.receiverMessage(messagePersener);
        }
    }

    /***
     * 发送消息
     * @param msg
     * 消息实体
     * @param tag
     * 打印标签
     * 301F9A8073AF18937FE2A447
     */
    MessageSendRunnable messageSendRunnable;

    public void sendMessage(byte[] msg, String tag, MessageSocketListener listener) {
        if (!getLineSatues()) {
//            MyLog.netty("==发消息===当前不在线，不发消息==" + tag);
            if (listener != null) {
                listener.messageSendFailed(false, "发消息异常：sock is null or nor online");
            }
            return;
        }
//        MyLog.netty("==发消息===当前在线，发消息==" + tag);
        if (messageSendRunnable == null) {
            messageSendRunnable = new MessageSendRunnable();
        }
        messageSendRunnable.setMessageInfo(socket, msg, listener);
        EtvService.getInstance().executor(messageSendRunnable);
    }

    /***
     * @param
     * -1  表示ondestriy
     * 1    表示断开重连
     * @param printTag
     */
    public void ondestroySocket(String printTag) {
        MyLog.netty("===releaseSocket==断开Socket==" + printTag);
        try {
            isReceiveMessage = false;
            if (socket != null) {
                socket.close();
                socket = null;
            }
            //发消息得IO
            if (messageSendRunnable != null) {
                messageSendRunnable.stopSendMessage();
            }
            //连接服务器得线程
            if (lineServerThread != null) {
                lineServerThread.stop();
                lineServerThread = null;
                throw new UnsupportedOperationException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
