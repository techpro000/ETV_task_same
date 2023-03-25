package com.etv.socket.online;

public interface SocketWebListener {
    int SOCKET_OPEN = 1;
    int SOCKET_INIT = 0;
    int SOCKET_CLOSE = -1;
    int SOCKET_ERROR = -2;

    void socketState(int state, String desc);

    void receiverMessage(String message);
}