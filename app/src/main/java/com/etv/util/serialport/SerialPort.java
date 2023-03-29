package com.etv.util.serialport;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.etv.udp.util.ParseMessage;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.udpsync.Command;
import com.udpsync.bean.CmdData;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {

    public static final int RESULT_OK = 1001;
    public static final int RESULT_ERROR = 1002;
    private static final String TAG = "SerialPort";
    private Context mContext;
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private static final int PORT_RATE = 115200;
//    private static final File PORT_FILE = new File("/dev/ttyS0");

    public SerialPort(Context context) {
        mContext = context;
        String serialPort = "/dev/ttyS" + SharedPerManager.getTTysPosition();
        MyLog.cdl("==初始化串口类型==" + serialPort);
        File PORT_FILE = new File(serialPort);

        if (!PORT_FILE.canRead() || !PORT_FILE.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 777 " + PORT_FILE.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !PORT_FILE.canRead() || !PORT_FILE.canWrite()) {
                    Log.w(TAG, "root failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mFd = open(PORT_FILE.getAbsolutePath(), PORT_RATE);
        if (mFd == null) {
            Log.w(TAG, "native open returns null");
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public void send(String path, String ipaddress) {
        try {
            byte[] bytes = (path).getBytes("UTF-8");
            logSerPortMessage(new String(bytes));
            mFileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void logSerPortMessage(String s) {
        Log.e("cdl", "logSerPortMessage=" + s);
    }
    // 31 189
    // 2023-03-25 14:11:31.332 3472-3531/com.ys.etv E/cdl: lo


    ReadThread mReadThread;

    public void receive(Handler handler) {
        if (mFileInputStream != null) {
            if (mReadThread != null) {
                mReadThread.interrupt();
                mReadThread = null;
            }
            mReadThread = new ReadThread(handler);
            mReadThread.start();
        }
    }

    public void release() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        try {
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
                mFileOutputStream = null;
            }

            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileInputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    StringBuilder stringBuilder = new StringBuilder();

    private class ReadThread extends Thread {
        Handler mHandler;

        public ReadThread(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (mHandler != null) {
                mHandler = null;
            }
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[128];
                    int size = mFileInputStream.read(buffer);
                    if (size < 1) {
                        reset();
                        return;
                    }
                    String bufferInfo = new String(buffer, 0, size, "UTF-8");
                    if (bufferInfo.startsWith("{")) {
                        logSerPortMessage("开始解析");
                        stringBuilder.setLength(0);
                        stringBuilder.append("{");
                    } else if (bufferInfo.startsWith("}")) {
                        stringBuilder.append("}");
                        logSerPortMessage("解析完成000=" + stringBuilder.toString());
                        String dataStr = stringBuilder.toString();
                        CmdData cmdData = Command.getData(dataStr);
                        if (cmdData != null) {
                            ParseMessage.parsenerMessageUdp(cmdData);
                        }
                    } else {
//                        logSerPortMessage("解析数据=" + bufferInfo);
                        stringBuilder.append(bufferInfo);
                    }
                } catch (Exception e) {
                    reset();
                }
            }
        }

        private void reset() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(RESULT_ERROR);
            }
        }
    }

    static {
        System.loadLibrary("serial_port");
    }

    public native FileDescriptor open(String path, int baudrate);

    public native void close();
}
