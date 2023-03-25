package com.etv.setting.util;

import com.ys.etv.R;
import com.etv.entity.BeanEntity;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static List<BeanEntity> getDataInfo() {
        List<BeanEntity> lists = new ArrayList<BeanEntity>();
        lists.add(new BeanEntity("中文", R.mipmap.icon_cn));
        lists.add(new BeanEntity("英文(English)", R.mipmap.icon_en));
//        lists.add(new BeanEntity("日语(日本語)", R.mipmap.icon_jpn));
//        lists.add(new BeanEntity("韩语()", R.mipmap.icon_kor));
//        lists.add(new BeanEntity("法语(Français)", R.mipmap.icon_fra));
//        lists.add(new BeanEntity("俄语(русский)", R.mipmap.icon_nld));
//        lists.add(new BeanEntity("德语(deutsch)", R.mipmap.icon_deu));
//        lists.add(new BeanEntity("西班牙语(Español)", R.mipmap.icon_spa));
//        lists.add(new BeanEntity("葡萄牙语(Português)", R.mipmap.icon_por));
//        lists.add(new BeanEntity("意大利语(In Italiano)", R.mipmap.icon_ita));
//        lists.add(new BeanEntity("阿拉伯语(العربية)", R.mipmap.icon_ara));
//        lists.add(new BeanEntity("泰语()", R.mipmap.icon_tha));
//        lists.add(new BeanEntity("希腊语(Ελληνική γλώσσα)", R.mipmap.icon_ell));
//        lists.add(new BeanEntity("荷兰语(De Nederlandse)", R.mipmap.icon_nld));
//        lists.add(new BeanEntity("波兰语(w języku polskim)", R.mipmap.icon_pol));
//        lists.add(new BeanEntity("丹麦语(dansk)", R.mipmap.icon_dan));
//        lists.add(new BeanEntity("芬兰语(suomen)", R.mipmap.icon_fin));
//        lists.add(new BeanEntity("捷克语(česky)", R.mipmap.icon_cn));
//        lists.add(new BeanEntity("罗马尼亚语(în limba română)", R.mipmap.icon_ron));
//        lists.add(new BeanEntity("瑞典语(svenska)", R.mipmap.icon_swe));
//        lists.add(new BeanEntity("匈牙利语(magyar)", R.mipmap.icon_hun));
        return lists;
    }
}
