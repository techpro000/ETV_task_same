package com.udpsync;

public interface OnUDPCallback {
    void onReceive(byte[] data);
    void onError(Throwable e);
}
