package com.ys.model.util;

import android.util.Log;

public class BaseLog {


    public static void cdl(String s) {
        cdl(s, false);
    }

    public static void cdl(String s, boolean b) {
        e("cdl", s);
    }

    public static void e(String tag, String desc) {
        Log.e(tag, desc);
    }

    public static void guardian(String s) {
        e("projector", s);
    }
}
