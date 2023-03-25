package com.etv.socket.mine;

public interface SocketStatuesListener {

    void backLineStaues(boolean isLineSuccess, String errorDesc);

    //--服务器返回的心跳消息
    void heartMessageBack();

    void receiverMessage(String message);
}
