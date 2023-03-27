package com.ys.model.util;

import android.widget.TextView;

public class TextUtils {
    public static boolean isEmpty(TextView text) {
        if (text == null) {
            return true;
        }
        if (text.getText() == null) {
            return true;
        }
        if (!hasText(text)) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String text) {
        if (text == null) {
            return true;
        }
        if (text.trim().length() < 1) {
            return true;
        }
        return false;
    }

    public static boolean hasText(TextView text) {
        return hasText(text.getText().toString());
    }

    public static boolean hasText(String text) {
        return !isEmpty(text);
    }
}
