package com.etv.http;

import com.alibaba.fastjson.JSON;
import com.etv.config.ApiInfo;
import com.etv.entity.Resp;
import com.etv.entity.SameScreen;
import com.etv.util.CodeUtil;
import com.etv.util.SharedPerManager;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;

public class SameScreenHelper {

    public static void getRowCowByNetwork(OnSameScreenListener listener) {
        if (!SharedPerManager.getAutoRowCow()){
            return;
        }
        OkHttpUtils.post()
                .url(ApiInfo.getScreenLayoutUrl())
                .addParams("clNo", CodeUtil.getUniquePsuedoID())
                .build()
                .execute(new MyCallback() {
                    @Override
                    public void onError(Call call, String s, int i) {

                    }

                    @Override
                    public void onResponse(Resp resp, int code) {
                        if (resp.code == 0) {
                            SameScreen screen = JSON.parseObject(resp.data, SameScreen.class);
                            SameScreen.Data data = screen.clientSameScreen;
                            if (data != null) {
                                SharedPerManager.setTaskSameMain(data.isMaster -1);
                                SharedPerManager.setShowScreenRowCow(data.rowNum, data.columnNum, data.serialNum);
                                if (listener != null) {
                                    listener.onResult(data);
                                }
                            }
                        }
                    }
                });
    }

    public interface OnSameScreenListener{
        void onResult(SameScreen.Data data);
    }
}
