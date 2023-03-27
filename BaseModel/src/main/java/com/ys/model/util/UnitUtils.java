package com.ys.model.util;

import android.content.res.Resources;
import android.util.TypedValue;

public final class UnitUtils {

    private UnitUtils() {

    }

    public static int dp2px(int dp) {
        return (int) dp2px(dp * 1f);
    }

    public static int sp2px(int sp) {
        return (int) sp2px(sp * 1f);
    }

    public static int px2dp(int px) {
        return (int) px2dp(px * 1f);
    }


    public static float dp2px(float dp) {
        return applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    public static float sp2px(float sp) {
        return applyDimension(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    private static float applyDimension(int unit, float value) {
        return TypedValue.applyDimension(unit, value, Resources.getSystem().getDisplayMetrics());
    }

    public static float px2dp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density + 0.5f;
    }
}
