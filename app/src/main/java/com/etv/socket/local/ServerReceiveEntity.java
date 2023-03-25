package com.etv.socket.local;

/**
 * server接收文件实体类
 */

public class ServerReceiveEntity {

    public static final int RECEIVER_START = 0;
    public static final int RECEIVER_PROGRESS = 1;
    public static final int RECEIVER_SUCCESS = 2;
    public static final int RECEIVER_ERROR = 3;

    /***
     * 接收文件的状态
     */
    int receive_state;
    /***
     * 接收文件的名字
     */
    String fileName;

    long fileHasReceive;
    /***
     * 接收文件的长度
     */
    long fileTotalLength;
    /***
     * 接收文件的保存地址
     */
    String fileSavePath;
    /***
     * 接收文件中的异常信息
     */
    String receiveDesc;

    public ServerReceiveEntity(int receive_state, String fileName, String fileSavePath, long fileHasReceive, long fileTotalLength, String receiveDesc) {
        this.receive_state = receive_state;
        this.fileName = fileName;
        this.fileHasReceive = fileHasReceive;
        this.fileTotalLength = fileTotalLength;
        this.fileSavePath = fileSavePath;
        this.receiveDesc = receiveDesc;
    }

    public long getFileHasReceive() {
        return fileHasReceive;
    }

    public void setFileHasReceive(long fileHasReceive) {
        this.fileHasReceive = fileHasReceive;
    }

    public int getReceive_state() {
        return receive_state;
    }

    public void setReceive_state(int receive_state) {
        this.receive_state = receive_state;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileTotalLength() {
        return fileTotalLength;
    }

    public void setFileTotalLength(long fileTotalLength) {
        this.fileTotalLength = fileTotalLength;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public String getReceiveDesc() {
        return receiveDesc;
    }

    public void setReceiveDesc(String receiveDesc) {
        this.receiveDesc = receiveDesc;
    }

    @Override
    public String toString() {
        return "ServerReceiveEntity{" +
                "receive_state=" + receive_state +
                ", fileName='" + fileName + '\'' +
                ", fileHasReceive=" + fileHasReceive +
                ", fileTotalLength=" + fileTotalLength +
                ", fileSavePath='" + fileSavePath + '\'' +
                ", receiveDesc='" + receiveDesc + '\'' +
                '}';
    }
}
