package com.etv.util;

public class TextUtils {


    public static byte[] getMacToByteAndNums(String str) {
        byte[] srtbyte = str.getBytes();
        byte[] byteRegister = new byte[srtbyte.length + 1];
        byteRegister[0] = 1;
        for (int i = 1; i < srtbyte.length + 1; i++) {
            byteRegister[i] = srtbyte[i - 1];
        }
        return byteRegister;
    }

    /***
     * 组合注册码
     * @param str
     * @return
     */
    public static byte[] getMacToByte(String str) {
        byte[] srtbyte = str.getBytes();
        byte[] byteRegister = new byte[srtbyte.length + 1];
        byteRegister[0] = 1;
        for (int i = 1; i < srtbyte.length + 1; i++) {
            byteRegister[i] = srtbyte[i - 1];
        }
        return byteRegister;
    }

    /***
     * 获取反馈指令消息给 socket 服务器
     * @param str
     * @return
     */
    public static byte[] getOrderBackMessage(String str) {
        byte[] srtbyte = str.getBytes();
        byte[] byteRegister = new byte[srtbyte.length + 1];
        byteRegister[0] = 6;
        for (int i = 1; i < srtbyte.length + 1; i++) {
            byteRegister[i] = srtbyte[i - 1];
        }
        return byteRegister;
    }


}
