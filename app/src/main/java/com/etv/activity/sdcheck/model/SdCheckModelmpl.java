package com.etv.activity.sdcheck.model;


import android.content.Context;

import com.etv.config.AppInfo;
import com.etv.http.util.FileWriteRunnable;
import com.etv.listener.WriteSdListener;
import com.etv.service.EtvService;
import com.etv.util.APKUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.sdcard.MySDCard;
import com.etv.util.system.LanguageChangeUtil;
import com.etv.util.zip.test.ZipFileListener;
import com.etv.util.zip.test.ZipUtil;
import com.ys.etv.R;

import java.io.File;

public class SdCheckModelmpl implements SdCheckModel {

    SdCheckListener listener;

    public SdCheckModelmpl(SdCheckListener listener) {
        this.listener = listener;
    }

    ZipUtil zipUtil;
    MySDCard mySdcard;

    /***
     * 解压任务文件
     * @param savePath
     */
    @Override
    public void unZipTaskFile(Context context, final String savePath) {
        MyLog.cdl("====解压文件zip路径==" + savePath);
        FileUtil.creatPathNotExcit("解压文件，这里创建一次");
        final String zipSavePath = AppInfo.BASE_TASK_URL();
        try {
            MyLog.cdl("====ZIP目录==" + savePath + " /压缩包保存的目录== " + zipSavePath);
            String showDesc = LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.parpre_zip_file, zipSavePath);
            listener.addInfoToList(showDesc);
            if (zipUtil == null) {
                zipUtil = new ZipUtil();
            }
            zipUtil.unZipFileWithProgress(savePath, zipSavePath, new ZipFileListener() {

                @Override
                public void zipProgress(int progress) {
                    MyLog.cdl("====zipProgress==" + progress);
                    listener.writeFileProgress(false, zipSavePath, progress);
                }

                @Override
                public void zipError(String error) {
                    MyLog.cdl("====zipError==" + error);
                    listener.setThreeClose(LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.zip_failed, error));
                }

                @Override
                public void zipComplet(String filePath) {
//                FileUtil.deleteDirOrFile(savePath);
                    MyLog.cdl("====zipComplet==解压结束");
                    listener.addInfoToList(LanguageChangeUtil.getLanguageFromResurceWithPosition(context, R.string.zip_parper_parson, zipSavePath));
                    listener.zipTaskSuccess(zipSavePath);
                }

                @Override
                public void checkFileStatues() {
                    listener.addInfoToList(context.getString(R.string.check_file_length));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopZipTask() {
        try {
            if (zipUtil == null) {
                return;
            }
            zipUtil.stopZipFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void installApk(final Context context, String filePath) {
        try {
            FileUtil.creatPathNotExcit("安装APK");
            final String savePath = AppInfo.BASE_PATH_INNER + "/etv.apk";
            File file = new File(savePath);
            file.createNewFile();
            FileWriteRunnable runnable = new FileWriteRunnable(context, filePath, savePath, new WriteSdListener() {
                @Override
                public void writeProgress(int progress) {
                    listener.writeFileProgress(false, savePath, progress);
                    MyLog.cdl("=======写入进度==" + progress);
                }

                @Override
                public void writeSuccess(String savePath) {
                    MyLog.cdl("=======写入路径===" + savePath);
                    File file = new File(savePath);
                    if (!file.exists()) {
                        listener.setThreeClose(context.getString(R.string.no_apk));
                    }
                    APKUtil apkUtil = new APKUtil(context);
                    apkUtil.installApk(savePath);
                }

                @Override
                public void writrFailed(String errorrDesc) {
                    listener.writeFileProgress(false, context.getString(R.string.write_failed), 0);
                    listener.setThreeClose("写入失败: " + errorrDesc);
                }
            });
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            listener.setThreeClose("文件写入失败:" + e.toString());
            MyLog.cdl("=======写入路径===" + e.toString());
        }
    }


    @Override
    public void copyTaskToSdcard(Context context, String filePath) {
        try {
            listener.addInfoToList("准备拷贝");
            FileUtil.creatPathNotExcit("Copy task to sdcard");
            File file = new File(filePath);
            if (!file.exists()) {
                listener.setThreeClose("离线节目文件不存在");
                return;
            }
            if (mySdcard == null) {
                mySdcard = new MySDCard(context);
            }
            String BasePath = AppInfo.BASE_SD_PATH();
            long copyLastSize = file.length() / (1024 * 1024);
            long sdLastSize = mySdcard.getAvailableExternalMemorySize(BasePath, 1024 * 1024);
            MyLog.cdl("离线任务内存: " + copyLastSize + "M, SD卡剩余内存: " + sdLastSize + " M");
            if (sdLastSize - copyLastSize < 100 || sdLastSize < copyLastSize) {
                listener.setThreeClose(context.getString(R.string.mo_cache_size));
                return;
            }
            String name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
            String savePath = AppInfo.BASE_SD_PATH() + "/" + name;
//            FileUtil.deleteDirOrFile(savePath);
            MyLog.cdl("拷贝zip到本地==" + filePath + " / " + savePath);
            writeTaskFileToSd(context, filePath, savePath, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeTaskFileToSd(Context context, String fileCopyPath, final String savePath, final SdCheckListener listener) {
        listener.addInfoToList(context.getString(R.string.copy_file));
        listener.addInfoToList("copyPath: " + fileCopyPath);
        listener.addInfoToList("savePath: " + savePath);
        try {
            FileWriteRunnable runnable = new FileWriteRunnable(context, fileCopyPath, savePath, new WriteSdListener() {
                @Override
                public void writeProgress(int progress) {
                    listener.writeFileProgress(false, savePath, progress);
                    MyLog.cdl("=======写入进度==" + progress);
                }

                @Override
                public void writeSuccess(String savePath) {
                    listener.addInfoToList(context.getString(R.string.write_success) + ":100%==" + savePath);
                    listener.writeFileProgress(true, savePath, 100);
                    unZipTaskFile(context, savePath);
                }

                @Override
                public void writrFailed(String errorrDesc) {
                    listener.writeFileProgress(false, context.getString(R.string.write_failed), 0);
                    listener.setThreeClose(context.getString(R.string.write_failed) + ": " + errorrDesc);
                }
            });
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
