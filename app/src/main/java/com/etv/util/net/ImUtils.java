package com.etv.util.net;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.etv.config.ApiInfo;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.ys.http.entry.Resp;
import com.ys.http.network.Body;
import com.ys.http.network.Callback;
import com.ys.http.network.HttpUtils;
import com.ys.http.network.SchedulerProvider;
import com.ys.http.network.Type;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Call;

public class ImUtils {

    public static int sCurrentSdkAppId;
    private static boolean hasOnMessageListener = false;//避免重复监听消息
    private static OnMessageListener mMessageListener;

    public static String signUrl = "/webservice/getUserSign";
    public static String repUrl = "/webservice/callbackCommondState";
    public static String devUrl = "/webservice/modifyClientOnlineStatus";

    public static StringBuilder logString = new StringBuilder();

    public static String getCurrentTimje() {
        return SimpleDateUtil.formatTaskTimeShow(System.currentTimeMillis()) + "   :";
    }

    public static void callbackDeceiveState(String dev_id) {
        if (!isLogin()) {
            return;
        }
        String url = ApiInfo.WEB_BASE_URL() + devUrl;//生产环境使用接口地址
        MyLog.cdl("ImUtils:===回复服务器指令地址==" + url);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("clientNo", dev_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.cdl("ImUtils:===http心跳   指令状态==" + errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLog.cdl("ImUtils:===http心跳   指令状态==" + response);
                    }
                });
    }

    public static void callbackCommondState(String instrucId) {

        String url = ApiInfo.WEB_BASE_URL() + ImUtils.repUrl;//生产环境使用接口地址
        MyLog.cdl("ImUtils:===回复服务器指令地址==" + url);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("instrucId", instrucId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.cdl("ImUtils:===回复服务器指令状态==" + errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLog.cdl("ImUtils:===回复服务器指令状态==" + response);
                    }
                });
    }

    //app 里调用，连接腾讯云im
    public static void initSdk(Context context, InitCallback initCallback) {
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        ImUtils.sCurrentSdkAppId = Constant.getSdkAppId();
        V2TIMManager.getInstance().initSDK(context, sCurrentSdkAppId, config, new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                MyLog.cdl("ImUtils::正在连接到腾讯云服务器...    ", true);
                //logString.append(getCurrentTimje() + "  " + "正在连接到腾讯云服务器...    " + "\t\n");
                if (initCallback != null) {
                    initCallback.onConnecting();
                }

            }

            @Override
            public void onConnectSuccess() {
                MyLog.cdl("ImUtils:已经成功连接到腾讯云服务器    ", true);
                //logString.append(getCurrentTimje() + "已经成功连接到腾讯云服务器    " + "\t\n");
                if (initCallback != null) {
                    initCallback.onConnectSuccess();
                }
            }

            @Override
            public void onConnectFailed(int code, String error) {
                MyLog.cdl("ImUtils:连接腾讯云服务器失败    " + code + "     " + error, true);
                //logString.append(getCurrentTimje() + "连接腾讯云服务器失败    " + "\t\n");
                if (initCallback != null) {
                    initCallback.onConnectError(code, error);
                }
            }

            @Override
            public void onUserSigExpired() {
                MyLog.cdl("ImUtils:签名过期    ", true);
                //logString.append(getCurrentTimje() + "签名过期    ");
                if (initCallback != null) {
                    initCallback.onConnectError(null, "签名过期");
                }
            }


        });
    }

    public static void setMessageListener(OnMessageListener listener) {
        mMessageListener = listener;
        if (!hasOnMessageListener) {
            hasOnMessageListener = true;
            V2TIMManager.getInstance().addSimpleMsgListener(new V2TIMSimpleMsgListener() {
                @Override
                public void onRecvC2CTextMessage(String msgID, V2TIMUserInfo sender, String text) {
                    MyLog.cdl("ImUtils:接收点对点消息    " + sender.getUserID() + "     " + text);
                    //logString.append(getCurrentTimje() + "接收点对点消息    " + sender.getUserID() + "     " + text + "\t\n");
                    if (mMessageListener != null) {
                        mMessageListener.onReceive(text, sender.getUserID());
                    }
                }

            });
        }
    }

    public static void send(String toUserId, String message, SendMsgCallback sendMsgCallback) {
        V2TIMMessage msg = V2TIMManager.getMessageManager().createTextMessage(message);
        // 设置不计入未读消息总数的标记
        msg.setExcludedFromUnreadCount(true);
        V2TIMManager.getMessageManager().sendMessage(msg, toUserId, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                MyLog.cdl("ImUtils:消息发送失败    ");
                if (sendMsgCallback != null) {
                    sendMsgCallback.onSendError(code, desc);
                }
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                MyLog.cdl("ImUtils:消息发送成功    ");
                if (sendMsgCallback != null) {
                    sendMsgCallback.onSendSuccess();
                }
            }

            @Override
            public void onProgress(int progress) {
                // 消息发送进度，用于发送图片文件等比较大的消息时
                MyLog.cdl("ImUtils:消息发送进度    " + progress);
            }
        });

    }


    public static void logout(LogoutCallback callback) {
        //退出登录，如果切换账号，需要 logout 回调成功或者失败后才能再次 login，否则 login 可能会失败。
        V2TIMManager.getInstance().logout(new V2TIMCallback() {
            @Override
            public void onSuccess() {
                //注销成功
                MyLog.cdl("ImUtils:注销成功    ");
                if (callback != null) {
                    callback.onLogoutSuccess();
                }
            }

            @Override
            public void onError(int code, String desc) {
                //注销失败
                MyLog.cdl("ImUtils:注销失败    ");
                if (callback != null) {
                    callback.onLogoutError(code, desc);
                }
            }
        });
    }

    /*
    登陆之前先退出，不管退出成功还是失败，都会进行登陆
     */
    public static void login(String userID, V2TIMCallback callback) {
        String url = ApiInfo.WEB_BASE_URL() + ImUtils.signUrl;//生产环境使用接口地址
        MyLog.cdl("ImUtils:===请求服务器数据 签名==" + url);
        Body body = Body.newFormBody()
                .addParam("userId", userID)
                .build();
        HttpUtils.post(url, body, new Type<Resp<String>>() {
        })
                .compose(SchedulerProvider.applyIO())
                .flatMap((Function<Resp<String>, ObservableSource<Resp<String>>>) resp -> {
                    if (resp.code == 0) {
                        JSONObject jsonObj = JSON.parseObject(resp.data);
                        resp.data = jsonObj.getString("imUserSign");
                        return Observable.create((ObservableOnSubscribe<Resp<String>>) emitter -> {
                            V2TIMManager.getInstance().logout(new V2TIMCallback() {
                                @Override
                                public void onSuccess() {
                                    emitter.onNext(resp);
                                    emitter.onComplete();

                                }

                                @Override
                                public void onError(int code, String desc) {
                                    emitter.onNext(resp);
                                    emitter.onComplete();
                                }
                            });
                        });
                    }
                    return Observable.just(resp);
                }).subscribe(new Callback<Resp<String>>() {
            @Override
            public void onSuccess(Resp<String> resp) {
                if (resp.code == 0) {
                    V2TIMManager.getInstance().login(userID, resp.data, callback);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                callback.onError(-1, e.getMessage());
            }
        });

    }

    public static boolean isLogin() {
        int status = V2TIMManager.getInstance().getLoginStatus();
        switch (status) {
            case V2TIMManager.V2TIM_STATUS_LOGINED:
            case V2TIMManager.V2TIM_STATUS_LOGINING:
                return true;
        }
        return false;
    }

    public static void unInit() {
        V2TIMManager.getInstance().unInitSDK();
    }


    public interface InitCallback {

        void onConnecting();

        void onConnectSuccess();

        void onConnectError(Integer code, String describe);
    }

    public interface LoginCallback {
        void onLoginSuccess();

        void onLoginError(Integer code, String describe);
    }

    public interface LogoutCallback {
        void onLogoutSuccess();

        void onLogoutError(Integer code, String describe);
    }

    public interface OnMessageListener {
        void onReceive(String message, String fromUserId);
    }

    public interface SendMsgCallback {
        void onSendSuccess();

        void onSendError(Integer code, String describe);
    }

}
