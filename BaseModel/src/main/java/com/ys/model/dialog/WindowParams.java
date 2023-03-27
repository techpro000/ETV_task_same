package com.ys.model.dialog;

import android.view.WindowManager;


public class WindowParams {
    private final int DEFAULT_INT = Integer.MIN_VALUE;

    public int width = DEFAULT_INT;
    public int height = DEFAULT_INT;
    public int x = DEFAULT_INT;
    public int y = DEFAULT_INT;
    public float dimAmount = -1;
    public int gravity = DEFAULT_INT;
    public int flags = DEFAULT_INT;
    public float alpha = -1;
    public int windowAnimations = DEFAULT_INT;
    public int softInputMode = DEFAULT_INT;
    public float horizontalWeight = DEFAULT_INT;
    public float horizontalMargin = DEFAULT_INT;
    public float verticalWeight = DEFAULT_INT;
    public float verticalMargin = DEFAULT_INT;
    public int rotationAnimation = DEFAULT_INT;

    public final void copyParams(WindowManager.LayoutParams lp) {
        x = lp.x;
        y = lp.y;
        width = lp.width;
        height = lp.height;
        flags = lp.flags;
        alpha = lp.alpha;
        gravity = lp.gravity;
        dimAmount = lp.dimAmount;
        verticalWeight = lp.verticalWeight;
        horizontalWeight = lp.horizontalWeight;
        verticalMargin = lp.verticalMargin;
        horizontalMargin = lp.horizontalMargin;
        softInputMode = lp.softInputMode;
        windowAnimations = lp.windowAnimations;
        rotationAnimation = lp.rotationAnimation;
    }

    public final void pasteParams(WindowManager.LayoutParams lp) {
        if (width != DEFAULT_INT) {
            lp.width = width;
        }
        if (height != DEFAULT_INT) {
            lp.height = height;
        }
        if (x != DEFAULT_INT) {
            lp.x = x;
        }
        if (y != DEFAULT_INT) {
            lp.y = y;
        }
        if (horizontalWeight != DEFAULT_INT) {
            lp.horizontalWeight = horizontalWeight;
        }
        if (verticalWeight != DEFAULT_INT) {
            lp.verticalWeight = verticalWeight;
        }
        if (horizontalMargin != DEFAULT_INT) {
            lp.horizontalMargin = horizontalMargin;
        }
        if (verticalMargin != DEFAULT_INT) {
            lp.verticalMargin = verticalMargin;
        }
        if (flags != DEFAULT_INT) {
            lp.flags = flags;
        }
        if (softInputMode != DEFAULT_INT) {
            lp.softInputMode = softInputMode;
        }
        if (gravity != DEFAULT_INT) {
            lp.gravity = gravity;
        }
        if (windowAnimations != DEFAULT_INT) {
            lp.windowAnimations = windowAnimations;
        }
        if (alpha != -1) {
            lp.alpha = alpha;
        }
        if (dimAmount != -1) {
            lp.dimAmount = dimAmount;
        }
        if (rotationAnimation != DEFAULT_INT) {
            lp.rotationAnimation = rotationAnimation;
        }

    }
}
