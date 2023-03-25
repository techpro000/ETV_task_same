package com.etv.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.etv.entity.Resp;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

public abstract class MyCallback extends Callback<Resp> {

    @Override
    public Resp parseNetworkResponse(Response response, int i) throws Exception {
        String sss = response.body().string();
        return JSON.parseObject(sss, Resp.class);
    }

    @Override
    public abstract void onResponse(Resp resp, int code);
}
