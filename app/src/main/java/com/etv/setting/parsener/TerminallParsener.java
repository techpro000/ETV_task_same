package com.etv.setting.parsener;

import android.content.Context;

import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.etv.service.util.EtvServerModule;
import com.etv.service.util.EtvServerModuleImpl;
import com.etv.service.util.TaskServiceView;
import com.etv.setting.view.TerminallView;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;

import org.json.JSONObject;

import okhttp3.Call;

public class TerminallParsener {

    TerminallView terminallView;
    Context context;

    public TerminallParsener(Context context, TerminallView terminallView) {
        this.context = context;
        this.terminallView = terminallView;
    }

    /***
     * 修改昵称
     * @param nickName
     */
    public void modifyNickName(String nickName) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            terminallView.shotToastView("NetWork Error ,Please Check !");
            return;
        }
        terminallView.showWaitDialog(true);
        String requestUrl = ApiInfo.MODIFY_NICK_NAME();
        MyLog.i("cdl", "======修改昵称===" + requestUrl);
        String clNO = CodeUtil.getUniquePsuedoID();
        MyLog.i("cdl", "======clNO===" + clNO);
//        http://192.168.0.103:8080/etv/webservice/updateClientName?clNo=fffffffffb4de64f000000006957b677&clName=123456
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", clNO)
                .addParams("clName", nickName)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        terminallView.showWaitDialog(false);
                        terminallView.shotToastView("NetWork Error ,Please Check !");
                        MyLog.i("cdl", "=====修改昵称requestFailed==" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.i("cdl", "=====修改昵称success==" + json);
                        terminallView.showWaitDialog(false);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String mag = jsonObject.getString("msg");
                            if (code == 0) {
                                queryNickName();
                            } else {
                                terminallView.shotToastView("Modify Device Name Failed : " + mag);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    EtvServerModule etvServerModule = null;

    /***
     * 查询昵称
     */
    public void queryNickName() {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            terminallView.shotToastView("NetWork Error ,Please Check !");
            return;
        }
        if (etvServerModule == null) {
            etvServerModule = new EtvServerModuleImpl();
        }
        etvServerModule.queryDeviceInfoFromWeb(context, new TaskServiceView() {

            @Override
            public void getDevInfoFromWeb(boolean isSuccess, String errorDesc) {
                if (!isSuccess) {
                    terminallView.shotToastView("Request Failed :" + errorDesc);
                    return;
                }
                String nickName = SharedPerManager.getDevNickName();
                terminallView.queryNickName(true, nickName);
            }
        });
    }


    /**
     * 把设备的定位信息提交给服务器
     *
     * @param context
     */
    public void updateDevInfoToWeb(Context context) {
        try {
            if (!AppConfig.isOnline) {
                return;
            }
            if (!NetWorkUtils.isNetworkConnected(context)) { //网络未连接
                return;
            }

            String clLatitude = SharedPerManager.getmLatitude();
            String clLongitude = SharedPerManager.getmLongitude();

            String url = ApiInfo.UPDATE_DEV_INFO();
            String clNo = CodeUtil.getUniquePsuedoID();
            String address = SharedPerManager.getAllAddress();   //获取设备详细地址

            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("clNo", clNo)
                    .addParams("clAddress", address)
                    .addParams("clLatitude", clLatitude + "")
                    .addParams("clLongitude", clLongitude + "")
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, String errorDesc, int id) {
                            MyLog.http("====修改设备定位==" + errorDesc);

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            MyLog.http("====修改设备定位=" + response);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
