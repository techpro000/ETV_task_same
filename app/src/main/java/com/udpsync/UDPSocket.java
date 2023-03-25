package com.udpsync;

import android.os.Handler;
import android.os.Looper;

import com.etv.util.MyLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSocket {

    private final int mPort = 8800;
    private boolean mRunning;

    private static final int POOL_SIZE = 6;

    public static final String ALL_HOST = "255.255.255.255";

    private static final int RECEIVED_DATA = 1;
    private static final int ERROR = -1;

    private final Handler mHandler;
    private DatagramSocket mSocket;
    private final ReceiveThread mReceiveThread;
    private final ExecutorService mExecutor;

    private OnUDPCallback mCallback;

    public UDPSocket() {
        mReceiveThread = new ReceiveThread();
        mExecutor = Executors.newFixedThreadPool(POOL_SIZE);

        Looper looper = Looper.getMainLooper();
        mHandler = new Handler(looper, msg -> {
            if (mCallback == null) {
                return false;
            }
            switch (msg.what) {
                case RECEIVED_DATA:
                    mCallback.onReceive((byte[]) msg.obj);
                    break;
                case ERROR:
                    Throwable throwable = (Throwable) msg.obj;
                    mCallback.onError(throwable);
                    break;
            }
            return true;
        });
    }

    public void startSocket() {
        if (mRunning) {
            return;
        }
        mExecutor.execute(mReceiveThread);
    }

    public boolean isActive() {
        return mRunning;
    }

    public void send(String data) {
        send(ALL_HOST, data);
    }

    public void send(String host, String data) {
        send(host, data.getBytes());
    }

    public void send(String host, byte[] data) {
        mExecutor.execute(new SendThread(host, mPort, data));
    }

    private class SendThread implements Runnable {

        private final int toPort;
        private final String host;
        private final byte[] data;

        private SendThread(String host, int port, byte[] data) {
            this.toPort = port;
            this.data = data;
            this.host = host;
        }

        @Override
        public void run() {
            if (host == null || data == null) {
                return;
            }
            try {
                InetAddress address = InetAddress.getByName(host);
                mSocket.send(new DatagramPacket(data, data.length, address, toPort));
                System.out.println("send to HOST--> " + address.getHostAddress() + "  port==> " + toPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                MyLog.udp("--------onReceive: 开始接收消息");
                mRunning = true;
                mSocket = new DatagramSocket(mPort);
                while (mRunning) {
                    byte[] data = new byte[4096];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    mSocket.receive(packet);
                    byte[] rec = Arrays.copyOf(data, packet.getLength());
                    mHandler.obtainMessage(RECEIVED_DATA, rec).sendToTarget();
                    System.out.println("接收到：data len: " + packet.getLength());
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(ERROR, e).sendToTarget();
            }
            mRunning = false;
        }
    }

    public void release() {
        mRunning = false;
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.close();
        }
    }

    public void setCallback(OnUDPCallback callback) {
        this.mCallback = callback;
    }
}
