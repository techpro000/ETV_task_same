package com.ys.model.util;

import android.content.Context;
import android.content.SharedPreferences;

/***
 * 用来保存Share基类
 */
public class SharedManagerModel {

    Context context;
    private SharedPreferences mSharedPreferences;

    public SharedManagerModel(Context context, String sheredName) {
        this.context = context;
        mSharedPreferences = context.getSharedPreferences(sheredName, 0);
    }

    public void saveData(String key, Object data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        try {
            if (data instanceof Integer) {
                editor.putInt(key, (Integer) data);
            } else if (data instanceof Boolean) {
                editor.putBoolean(key, (Boolean) data);
            } else if (data instanceof String) {
                editor.putString(key, (String) data);
            } else if (data instanceof Float) {
                editor.putFloat(key, (Float) data);
            } else if (data instanceof Long) {
                editor.putLong(key, (Long) data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public Object getData(String key, Object defaultObject) {
        try {
            if (defaultObject instanceof String) {
                return mSharedPreferences.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return mSharedPreferences.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return mSharedPreferences.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return mSharedPreferences.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return mSharedPreferences.getLong(key, (Long) defaultObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
