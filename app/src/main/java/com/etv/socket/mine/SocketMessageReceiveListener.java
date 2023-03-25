package com.etv.socket.mine;

public interface SocketMessageReceiveListener {
    void backMessageReceiver(boolean isTrue, String message, byte[] messageByte);
}
