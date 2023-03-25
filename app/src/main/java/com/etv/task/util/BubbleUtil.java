package com.etv.task.util;

/**
 * Created by jsjm on 2018/10/25.
 */

public class BubbleUtil {

    /***
     * 冒泡算法排序
     * @param arr
     * @return
     * 返回从小到大排序
     */
    public static long[] Bubblesort(long[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    long temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

}
