package com.etv.service.view;

public interface TcpHartStatuesListener {

    void sendHeartMessage(String message);

    void registerDev(String message);
}
