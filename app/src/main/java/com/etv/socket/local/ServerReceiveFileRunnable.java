package com.etv.socket.local;

import android.os.Handler;
import android.util.Log;

import com.etv.config.AppInfo;
import com.etv.util.NetWorkUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReceiveFileRunnable implements Runnable {

    private ServerSocket server;
    ReceiveFileListener listener;
    boolean isReceive = true;
    private Handler handler = new Handler();
    public static final int SERVER_PORT = 9865;

    public ServerReceiveFileRunnable(ReceiveFileListener listener) {
        this.listener = listener;
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (Exception e) {
            isReceive = false;
            backInfo(ServerReceiveEntity.RECEIVER_ERROR, "", "", 0, 0, e.toString());
        }
    }

    @Override
    public void run() {
        while (isReceive) {
            ReceiveFile();
        }
    }

    void ReceiveFile() {
        try {
            Socket name = server.accept();
            InputStream nameStream = name.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(nameStream);
            BufferedReader br = new BufferedReader(streamReader);
            String nameLength = br.readLine();
            Log.e("receiveFile", "===nameLength：" + nameLength);
            String fileName = "";
            long fileLength = 0;
            try {
                JSONObject jsonObject = new JSONObject(nameLength);
                fileName = jsonObject.getString("fileName");
                fileLength = jsonObject.getLong("fileTotalSize");
            } catch (Exception e) {
                Log.e("receiveFile", "=====接收文件解析异常===" + e.toString());
            }
            String time = System.currentTimeMillis() + "";
            String savePath = AppInfo.BASE_IMAGE_RECEIVER() + "/" + time + fileName;
            Log.e("receiveFile", "=====保存的数据路径===" + savePath);
            br.close();
            streamReader.close();
            nameStream.close();
            name.close();
            backInfo(ServerReceiveEntity.RECEIVER_START, fileName, savePath, 0, fileLength, "开始接收文件");
            Socket data = server.accept();
            InputStream dataStream = data.getInputStream();
            File fileDir = new File(savePath);
            if (fileDir.exists()) {
                fileDir.delete();
            }
            fileDir.createNewFile();
            FileOutputStream file = new FileOutputStream(savePath, false);
            byte[] buffer = new byte[1024];
            int size = -1;
            long sum = 0;
            while ((size = dataStream.read(buffer)) != -1) {
                file.write(buffer, 0, size);
                sum += size;
                Log.e("====", "++++++" + NetWorkUtils.bytes2kb(sum) + "  /" + NetWorkUtils.bytes2kb(fileLength));
                backInfo(ServerReceiveEntity.RECEIVER_PROGRESS, fileName, savePath, sum, fileLength, "接收中");
            }
            file.close();
            dataStream.close();
            data.close();
            backInfo(ServerReceiveEntity.RECEIVER_SUCCESS, fileName, savePath, fileLength, fileLength, "接收成功");
        } catch (Exception e) {
            isReceive = false;
            backInfo(ServerReceiveEntity.RECEIVER_ERROR, "", "", 0, 0, e.toString());
        }
    }


    public void backInfo(final int receive_state, final String fileName, final String fileSavePath, final long fileHasReceive, final long fileTotalLength, final String receiveDesc) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ServerReceiveEntity serverReceiveEntity = new ServerReceiveEntity(receive_state, fileName, fileSavePath, fileHasReceive, fileTotalLength, receiveDesc);
                listener.receiveFileState(serverReceiveEntity);
            }
        });
    }

    public void stopReceiveFile() {
        try {
            isReceive = false;
            if (server != null) {
                server.close();
            }
        } catch (Exception e) {
        }
    }

}
