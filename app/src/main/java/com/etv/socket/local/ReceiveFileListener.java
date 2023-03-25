package com.etv.socket.local;


/**
 * 服务端接受客户端发来的消息监听
 * Created by jsjm on 2018/2/26.
 */

public interface ReceiveFileListener {

    /***
     * 接收文件状态信息
     * @param mServerReceiveEntity
     */
    void receiveFileState(ServerReceiveEntity mServerReceiveEntity);

}
