package com.etv.setting.framenew.util;

import android.content.Context;

import com.ys.etv.R;

import java.util.ArrayList;
import java.util.List;

public class SettingFragmentUtil {

    public static List<String> getAnimType(Context context) {
        List<String> lists = new ArrayList<>();

        lists.add(context.getString(R.string.default_anim));  //默认效果
        lists.add(context.getString(R.string.trans_change));  //透明渐变

        lists.add(context.getString(R.string.right_left));  //右到左
        lists.add(context.getString(R.string.left_right));  //左到右
        lists.add(context.getString(R.string.up_bottom));  //上到下
        lists.add(context.getString(R.string.bottom_up));  //下到上
        lists.add(context.getString(R.string.hand_qin));  //手风琴挤压
        lists.add(context.getString(R.string.big_change));  //放大渐变
        lists.add(context.getString(R.string.small_change));  //缩小渐变
        lists.add(context.getString(R.string.threed_in));  //3D旋转进
        lists.add(context.getString(R.string.threed_out));  //3D旋转出

        lists.add(context.getString(R.string.front_change));  //景深渐变
        lists.add(context.getString(R.string.left_riroate));  //左右旋转
        lists.add(context.getString(R.string.topbom_riroate));  //上下旋转
        lists.add(context.getString(R.string.nishz_riroate));  //逆时针旋转
        lists.add(context.getString(R.string.shunsz_riroate));  //顺时针旋转
        lists.add(context.getString(R.string.topbom_small));  //上下缩放
        lists.add(context.getString(R.string.three_paint));  //3D画框效果

        lists.add(context.getString(R.string.small_changesize));  //缩放渐变
        lists.add(context.getString(R.string.big_changesize));  //放大消隐动画
        lists.add(context.getString(R.string.trans_changesize));  //渐变透明
        lists.add(context.getString(R.string.change_small));  //渐变缩放
        return lists;
    }


}
