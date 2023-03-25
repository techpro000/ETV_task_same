package com.etv.activity.sdcheck;

import android.content.Context;
import android.text.TextUtils;

import com.etv.activity.sdcheck.model.SdCheckListener;
import com.etv.activity.sdcheck.model.SdCheckModel;
import com.etv.activity.sdcheck.model.SdCheckModelmpl;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.MediaBean;
import com.etv.http.util.FileWriteRunnable;
import com.etv.http.util.GetFileFromPathForRunnable;
import com.etv.http.util.GetMediaListFromPathNewRunnable;
import com.etv.listener.CompressImageListener;
import com.etv.listener.WriteSdListener;
import com.etv.service.EtvService;
import com.etv.service.TcpService;
import com.etv.service.TcpSocketService;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.SingleTaskEntity;
import com.etv.task.model.TaskModelmpl;
import com.etv.task.model.TaskRequestListener;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.image.CompressImageUtil;
import com.etv.util.system.LanguageChangeUtil;
import com.ys.etv.R;
import com.ys.model.entity.FileEntity;
import com.ys.model.util.FileMatch;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SdCheckParsener implements SdCheckListener {

    Context context;
    SdCheckView sdCheckView;
    SdCheckModel sdCheckModel;

    public SdCheckParsener(Context context, SdCheckView sdCheckView) {
        this.context = context;
        this.sdCheckView = sdCheckView;
        sdCheckModel = new SdCheckModelmpl(this);
    }

    List<MediaBean> listFile = new ArrayList<MediaBean>();

    /**
     * 拷贝文件到ETV单机目录下
     *
     * @param filePath
     */
    public void copyDisTaskToLocal(String filePath, int fileType) {
        MyLog.cdl("=====检索的文件个数=开始草族谱==");
        try {
            GetFileFromPathForRunnable runnable = new GetFileFromPathForRunnable(filePath, new GetFileFromPathForRunnable.QueryFileFromPathListener() {
                @Override
                public void backFileList(boolean isSuccess, List<File> listFileSearch, String errorDesc) {
                    if (!isSuccess) {
                        setThreeClose(errorDesc);
                        return;
                    }
                    if (listFileSearch == null || listFileSearch.size() < 1) {
                        setThreeClose(context.getString(R.string.group_no_file));
                        return;
                    }
                    MyLog.cdl("=====检索的文件个数===" + listFileSearch.size());
                    parpreToWriteFileToSd(listFileSearch, fileType);
                }
            });
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int TYPE_COPY_SINGLE = 0;   //单机节目
    public static final int TYPE_COPY_LINE = 1;   //连屏操作

    /***
     * 文件拷贝
     * @param listFileSearch
     * @param  copyType
     * type == 0    单机任务拷贝
     * type == 1    单机连屏拷贝
     */
    private void parpreToWriteFileToSd(List<File> listFileSearch, int copyType) {
        addInfoToList(LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.copy_file_num, listFileSearch.size() + ""));
        HasCopyNum = listFileSearch.size();
        listFile.clear();
        for (File fileCurrent : listFileSearch) {
            String filePth = fileCurrent.getPath();
            String fileName = fileCurrent.getName();
            MyLog.cdl("====离线文件的路径==" + filePth);
            int fileType = FileMatch.fileMatch(fileName);
            switch (copyType) {
                case TYPE_COPY_SINGLE:
                    if (fileType == FileEntity.STYLE_FILE_IMAGE
                            || fileType == FileEntity.STYLE_FILE_VIDEO
                            || fileType == FileEntity.STYLE_FILE_MUSIC
                            || fileType == FileEntity.STYLE_FILE_PDF) {
                        listFile.add(new MediaBean(fileName, filePth, fileType));
                    }
                    break;
                case TYPE_COPY_LINE:
                    if (fileType == FileEntity.STYLE_FILE_VIDEO) {
                        listFile.add(new MediaBean(fileName, filePth, fileType));
                    }
                    break;
            }
        }
        if (listFile == null || listFile.size() < 1) {
            setThreeClose(context.getString(R.string.no_use_pic));
            return;
        }
        copyFileOneByOne(copyType);
    }


    private void copyFileOneByOne(int copyType) {
        if (copyType == TYPE_COPY_SINGLE) {
            if (listFile == null || listFile.size() < 1) {
                addInfoToList(context.getString(R.string.parpre_zip_media));
                checkImageFileSizeIfSuccess();
                return;
            }
        }
        if (copyType == TYPE_COPY_LINE) {
            if (listFile == null || listFile.size() < 1) {
                setThreeClose(context.getString(R.string.file_write_over));
                return;
            }
        }
        copyFileToLocalFileDir(listFile.get(0), copyType);
    }

    /**
     * 检测图片分辨率是否过大
     */
    private void checkImageFileSizeIfSuccess() {
        list_image.clear();
        String path = AppInfo.TASK_SINGLE_PATH();
        GetMediaListFromPathNewRunnable runnable = new GetMediaListFromPathNewRunnable(path, new GetMediaListFromPathNewRunnable.GetSingleTaskEntityListener() {
            @Override
            public void backTaskEntity(boolean isTrue, SingleTaskEntity singleTaskEntity, String errorDesc) {
                if (!isTrue) {
                    setThreeClose(context.getString(R.string.check_over));
                    return;
                }
                if (singleTaskEntity == null) {
                    setThreeClose(context.getString(R.string.no_media_file));
                    return;
                }
                List<MediAddEntity> list_image_main = singleTaskEntity.getList_image();
                List<MediAddEntity> list_image_second = singleTaskEntity.getList_image_double();
                if (list_image_main != null && list_image_main.size() > 0) {
                    list_image.addAll(list_image_main);
                }
                if (list_image_second != null && list_image_second.size() > 0) {
                    list_image.addAll(list_image_second);
                }
                dealFileListInfo();
            }
        });
        EtvService.getInstance().executor(runnable);
    }

    //    用来封装图片集合
    List<MediAddEntity> list_image = new ArrayList<MediAddEntity>();
    CompressImageUtil compressImageUtil;

    private void dealFileListInfo() {
        if (list_image == null || list_image.size() < 1) {
            setThreeClose(context.getString(R.string.zip_over));
            return;
        }
        String filePath = list_image.get(0).getUrl();
        checkFileIfRightOneByOne(filePath);
    }

    private void checkFileIfRightOneByOne(String filePath) {
        MyLog.cdl("====准备压缩路径===" + filePath);
        String showDesc = LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.parpre_zip_file, filePath);
        addInfoToList(showDesc);
        if (compressImageUtil == null) {
            compressImageUtil = new CompressImageUtil(context);
        }
        compressImageUtil.compressPic(filePath, new CompressImageListener() {
            @Override
            public void backErrorDesc(String desc) {
                MyLog.cdl("======压缩失败==" + desc);
                if (list_image == null || list_image.size() < 1) {
                    setThreeClose(context.getString(R.string.zip_check_over));
                    return;
                }
                String checkFilec = LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.check_failed, desc);
                addInfoToList(checkFilec);
                list_image.remove(0);
                dealFileListInfo();
            }

            @Override
            public void backImageSuccess(String oldPath, String imagePath) {
                if (list_image == null || list_image.size() < 1) {
                    setThreeClose(context.getString(R.string.zip_success));
                    return;
                }
                //如果转码成功，就需要删除旧文件
                if (imagePath.contains("compress")) {
                    FileUtil.deleteDirOrFilePath(oldPath, "转码成功删除旧文件");
                }
                addInfoToList(LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.check_success, imagePath));
                list_image.remove(0);
                dealFileListInfo();
            }
        });
    }

    /**
     * 将文件拷贝到本地路径
     *
     * @param filePth
     */
    int HasCopyNum = 0;

    private void copyFileToLocalFileDir(MediaBean mediaBean, int copyType) {
        if (mediaBean == null) {
            if (listFile != null && listFile.size() > 0) {
                listFile.remove(0);
            }
            copyFileOneByOne(copyType);
            return;
        }
        try {
            String filePth = mediaBean.getPath();
            String fileName = mediaBean.getName();
            sdCheckView.addInfoToList(context.getString(R.string.start_copy) + " : " + filePth);
            String savePath = "";
            switch (copyType) {
                case TYPE_COPY_SINGLE:
                    savePath = filePth.substring(filePth.indexOf("etv-media/") + 10);
                    savePath = AppInfo.TASK_SINGLE_PATH() + "/" + savePath;
                    break;
                case TYPE_COPY_LINE:
                    savePath = AppInfo.LINE_SCREEN_PATH() + "/" + fileName;
                    break;
            }
            MyLog.cdl("=======文件写入==真实路径=" + filePth + " / " + savePath);
            FileWriteRunnable runnable = new FileWriteRunnable(context, filePth, savePath, new WriteSdListener() {

                @Override
                public void writeProgress(int progress) {
                    sdCheckView.writeFileProgress(progress);
                }

                @Override
                public void writeSuccess(String savePath) {
                    sdCheckView.addInfoToList(context.getString(R.string.write_success) + ": " + savePath);
                    if (listFile != null && listFile.size() > 0) {
                        listFile.remove(0);
                    }
                    copyFileOneByOne(copyType);
                }

                @Override
                public void writrFailed(String errorrDesc) {
                    sdCheckView.addInfoToList(context.getString(R.string.write_failed) + ": " + errorrDesc);
                    if (listFile != null && listFile.size() > 0) {
                        listFile.remove(0);
                    }
                    copyFileOneByOne(copyType);
                }
            });
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改Ip地址
     *
     * @param ipAddressPath
     */
    public void modifySharedIpAddress(String ipAddressPath) {
        String txtInfo = FileUtil.getTxtInfoFromTxtFile(ipAddressPath);
        if (txtInfo == null || TextUtils.isEmpty(txtInfo) || txtInfo.length() < 3) {
            sdCheckView.setThreeClose(context.getString(R.string.no_ip));
            return;
        }
        sdCheckView.addInfoToList(context.getString(R.string.get_ip_info) + ":" + txtInfo);
        try {
//            {"ip":"192.168.1.1","port":"8899","userName":"139999999999"}
            JSONObject jsonObject = new JSONObject(txtInfo);
            String serverIP = jsonObject.getString("ip");
            SharedPerManager.setWebHost(serverIP);
            sdCheckView.addInfoToList("Setting Server IP: " + serverIP);
            String serverPort = jsonObject.getString("port");
            SharedPerManager.setWebPort(serverPort);
            sdCheckView.addInfoToList("Setting Server Port: " + serverPort);
            String userName = jsonObject.getString("userName");
            SharedPerManager.setUserName(userName, "U盘修改IP信息");
            sdCheckView.addInfoToList("Setting Server UserName: " + userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sdCheckView.addInfoToList("Setting Success ,Auto Line Server ...");
        if (!NetWorkUtils.isNetworkConnected(context)) {
            sdCheckView.setThreeClose("Current Not Net ...");
            return;
        }
        sdCheckView.setThreeClose("Auto Line Server ...");
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            TcpService.getInstance().dealDisOnlineDev("To modify the IP address of the USB flash disk, disconnect it first and then reconnect it !", true);
            TcpService.getInstance().getDevHartStateInfo(-1, "SD card modify IP");
        } else {
            TcpSocketService.getInstance().dealDisOnlineDev("To modify the IP address of the USB flash disk, disconnect it first and then reconnect it !", true);
            TcpSocketService.getInstance().getDevHartStateInfo(-1, "SD card modify IP");
        }
    }

    TaskModelmpl taskModeImpl;

    private void parsenerTaskInfo(final String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            sdCheckView.setThreeClose(context.getString(R.string.no_data));
            return;
        }
        final String taskInfo = FileUtil.getTxtInfoFromTxtFile(filePath, FileUtil.TYPE_UTF_8);
//        final String taskInfo = FileUtil.getTxtInfoFromTxtFile(filePath, FileUtil.TYPE_GBK);
        MyLog.cdl("====获取的任务详情==" + taskInfo);
        if (taskInfo == null || taskInfo.length() < 3) {
            sdCheckView.setThreeClose(context.getString(R.string.get_disonline_failed));
            return;
        }
        if (taskModeImpl == null) {
            taskModeImpl = new TaskModelmpl();
        }
        taskModeImpl.parsenerSingleTaskEntity(taskInfo, new TaskRequestListener() {

            @Override
            public void modifyTxtInfoStatues(boolean isSuccess, String desc) {

            }

            @Override
            public void playNextProgram(boolean isBack, List<SceneEntity> listPm, int tag) {

            }

            @Override
            public void finishMySelf(String errorDesc) {
                MyLog.cdl("========解析异常=======" + errorDesc);
                sdCheckView.setThreeClose(context.getString(R.string.parsener_error) + "：" + errorDesc);
            }

            @Override
            public void parserJsonOver(String tag) {
                //清理当前的task.以防下次误判
                String file = AppInfo.BASE_TASK_URL() + "/task.txt";
                FileUtil.deleteDirOrFilePath(file, "====解析完成==parserJsonOver====");
                sdCheckView.setThreeClose(context.getString(R.string.parsener_success));
            }
        });
    }

    /***
     * 离线任务解压完毕，这里去读取数据
     * @param usbPath
     */
    @Override
    public void zipTaskSuccess(String usbPath) {
        sdCheckView.addInfoToList(context.getString(R.string.parsener_task_path) + ": " + usbPath);
        parsenerTaskInfo(usbPath + "/task.txt");
    }

    /***
     * 安装APK
     * @param filePath
     */
    public void installApk(String filePath) {
        sdCheckModel.installApk(context, filePath);
    }

    /***
     * 将离线任务解压到SD卡内
     * @param filePath
     */
    public void zipFileToSdTask(String filePath) {
        sdCheckModel.unZipTaskFile(context, filePath);
    }

    public void stopZipFile() {
        sdCheckModel.stopZipTask();
    }

    @Override
    public void setThreeClose(String desc) {
        if (sdCheckView != null) {
            sdCheckView.setThreeClose(desc);
        }
    }

    @Override
    public void addInfoToList(String desc) {
        if (sdCheckView != null) {
            sdCheckView.addInfoToList(desc);
        }
    }

    @Override
    public void writeFileProgress(boolean isOve, String savePath, int progress) {
        if (sdCheckView != null) {
            sdCheckView.writeFileProgress(progress);
        }
    }


    public void modifyWelcomeMediaFile(String filePath) {
        String fileSavePath = AppInfo.WELCOME_SAVE_PATH;
        try {
            File file = new File(fileSavePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriteRunnable runnable = new FileWriteRunnable(context, filePath, fileSavePath, new WriteSdListener() {

            @Override
            public void writeProgress(int progress) {
                sdCheckView.writeFileProgress(progress);
            }

            @Override
            public void writeSuccess(String savePath) {
                sdCheckView.setThreeClose(context.getString(R.string.write_success) + ": " + savePath);
            }

            @Override
            public void writrFailed(String errorrDesc) {
                sdCheckView.setThreeClose(context.getString(R.string.write_failed) + ": " + errorrDesc);
            }
        });
        EtvService.getInstance().executor(runnable);
    }
}
