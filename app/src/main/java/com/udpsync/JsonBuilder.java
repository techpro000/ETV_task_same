package com.udpsync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Set;

public class JsonBuilder {

    private final JSONObject obj;

    private JsonBuilder() {
        obj = new JSONObject();
    }

    public static JsonBuilder newBuilder() {
        return new JsonBuilder();
    }

    public JsonBuilder add(String key, Number value) {
        obj.put(key, value);
        return this;
    }

    public JsonBuilder add(String key, boolean value) {
        obj.put(key, value);
        return this;
    }

    public JsonBuilder add(String key, String value) {
        obj.put(key, value);
        return this;
    }

    public JsonBuilder add(String key, Object bean) {
        obj.put(key, bean);
        return this;
    }

    public JsonBuilder add(Object bean) {
        JSONObject object = JSON.parseObject(JSON.toJSONString(bean));
        Set<Map.Entry<String, Object>> entrySet = object.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            obj.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public JSONObject get() {
        return obj;
    }

    @Override
    public String toString() {
        return obj.toJSONString();
    }

    public Object build() {
        return obj;
    }
}
