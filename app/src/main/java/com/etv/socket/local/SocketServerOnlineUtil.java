//package com.etv.socket.local;
//
//import android.app.Activity;
////import android.support.v4.content.FileProvider;
//
//import com.etv.service.EtvService;
//import com.etv.util.FileUtil;
//import com.etv.view.GridButton;
//
//import java.io.File;
//
//public class SocketServerOnlineUtil {
//
//    Activity context;
//    GridButton grid_button_receiver;
//    ServerReceiveView serverReceiveView;
//
//    public SocketServerOnlineUtil(Activity context, ServerReceiveView serverReceiveView) {
//        this.context = context;
//        this.serverReceiveView = serverReceiveView;
//        getView();
//    }
//
//    private void getView() {
//        grid_button_receiver = serverReceiveView.getGridButton();
//    }
//
//
//    ServerReceiveFileRunnable receiveFileRunnable = null;
//
//    public void receiveMessage() {
//        receiveFileRunnable = new ServerReceiveFileRunnable(new ReceiveFileListener() {
//
//            @Override
//            public void receiveFileState(ServerReceiveEntity mServerReceiveEntity) {
//                dealReceiveInfo(mServerReceiveEntity);
//            }
//        });
//        EtvService.getInstance().executor(receiveFileRunnable);
//    }
//
//    /***
//     * 停止接收文件
//     */
//    public void stopReceiveFile() {
//        if (receiveFileRunnable != null) {
//            receiveFileRunnable.stopReceiveFile();
//        }
//    }
//
//    /***
//     * 处理接收的文件相关信息
//     * @param mServerReceiveEntity
//     */
//    private void dealReceiveInfo(ServerReceiveEntity mServerReceiveEntity) {
//        int state = mServerReceiveEntity.getReceive_state();
//        String fileName = mServerReceiveEntity.getFileName();
//        String fileSavePath = mServerReceiveEntity.getFileSavePath();
//        long fileHasReceive = mServerReceiveEntity.getFileHasReceive();
//        long fileTotalLength = mServerReceiveEntity.getFileTotalLength();
//        String receiveDesc = mServerReceiveEntity.getReceiveDesc();
//        switch (state) {
//            case ServerReceiveEntity.RECEIVER_START:
//                grid_button_receiver.updateProgress(fileName, 0, fileTotalLength);
//                break;
//            case ServerReceiveEntity.RECEIVER_PROGRESS:
//                grid_button_receiver.updateProgress(fileName, fileHasReceive, fileTotalLength);
//                break;
//            case ServerReceiveEntity.RECEIVER_SUCCESS:
//                grid_button_receiver.resetState();
//                serverReceiveView.showToast(receiveDesc);
//                break;
//            case ServerReceiveEntity.RECEIVER_ERROR:
//                grid_button_receiver.resetState();
//                serverReceiveView.showToast(receiveDesc);
//                break;
//        }
//    }
//}
