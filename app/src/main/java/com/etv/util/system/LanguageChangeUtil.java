package com.etv.util.system;

import android.content.Context;

public class LanguageChangeUtil {

    Context context;

    public LanguageChangeUtil(Context context) {
        this.context = context;
    }

    public static String getLanguageFromResurce(Context context, int resourceId) {
        String desc = "";
        try {
            if (context == null) {
                return null;
            }
            desc = context.getString(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }

    public static String getLanguageFromResurceWithPosition(Context context, int resourceId, String desc) {
        if (context == null) {
            return null;
        }
        String startResult = "";
        try {
            String stringStart = context.getString(resourceId);
            startResult = String.format(stringStart, desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startResult;
    }

}
