package com.etv.util.location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.activity.BaseActivity;
import com.etv.adapter.PopStringAdapter;
import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.util.Biantai;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.location.entity.AreaEntity;
import com.etv.util.location.entity.CityEntity;
import com.etv.util.location.entity.ProvinceEntity;
import com.etv.util.location.util.CityParsenerUtil;
import com.etv.util.sdcard.MySDCard;
import com.etv.view.dialog.CustomPopWindow;
import com.ys.etv.R;
import com.ys.model.dialog.WaitDialogUtil;
import com.ys.model.util.KeyBoardUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ProCityDialogActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    ArrayList<ProvinceEntity> proList = new ArrayList<ProvinceEntity>();//省
    private List<String> list_show = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_pro_city);
        initView();
        getDatePro();
        initPopWindow();
    }

    private void getDatePro() {
        waitDialogUtil.show("获取数据");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CityParsenerUtil cityParsenerUtil = new CityParsenerUtil(ProCityDialogActivity.this);
                proList = cityParsenerUtil.getProCityInfo(handler);
            }
        };
        EtvService.getInstance().executor(runnable);
    }

    public static final int MESSAGE_GET_PRO_OVER = 4513;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_GET_PRO_OVER:
                    waitDialogUtil.dismiss();
                    initData();
                    break;
            }
        }
    };

    private void initData() {
        String provinceName = SharedPerManager.getProvince();
        for (int i = 0; i < proList.size(); i++) {
            String jsonPro = proList.get(i).getName();
            if (jsonPro.contains(provinceName)) {
                currentPosition = i;
            }
        }
    }

    Button btn_province, btn_city, btn_area;
    private EditText et_detail_address;
    private Button btn_submit;
    WaitDialogUtil waitDialogUtil;
    private int currentPosition;

    private void initView() {
        waitDialogUtil = new WaitDialogUtil(ProCityDialogActivity.this);
        btn_province = (Button) findViewById(R.id.btn_province);
        btn_city = (Button) findViewById(R.id.btn_city);
        btn_area = (Button) findViewById(R.id.btn_area);

        btn_province.setText(SharedPerManager.getProvince());
        btn_city.setText(SharedPerManager.getLocalCiti());
        btn_area.setText(SharedPerManager.getArea());

        btn_province.setOnClickListener(this);
        btn_city.setOnClickListener(this);
        btn_area.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);
        et_detail_address.setText(SharedPerManager.getDetailAddress());
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        et_detail_address.setOnFocusChangeListener(this);
        rela_bgg = (RelativeLayout) findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardUtil.hiddleBord(rela_bgg);
            }
        });
    }

    RelativeLayout rela_bgg;
    private LinearLayout lin_exit;
    private TextView tv_exit;
    CustomPopWindow pop_province;
    ListView pop_lv_pro;
    PopStringAdapter adapter_pop;

    private void initPopWindow() {
        View popView = View.inflate(ProCityDialogActivity.this, R.layout.view_pop_procityarea, null);
        pop_province = new CustomPopWindow.PopupWindowBuilder(ProCityDialogActivity.this)
                .setView(popView)
                .size(960, 400)
                .create();
        pop_lv_pro = (ListView) popView.findViewById(R.id.lv_content);
        adapter_pop = new PopStringAdapter(ProCityDialogActivity.this, list_show);
        pop_lv_pro.setAdapter(adapter_pop);
        pop_lv_pro.setOnItemClickListener(onItemClienListener);
    }

    AdapterView.OnItemClickListener onItemClienListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int positon, long l) {
            pop_province.dissmiss();
            if (clickId == R.id.btn_province) {
                currentPosition = positon;
                String province = list_show.get(positon);
                btn_province.setText(province);
                btn_city.setText("");
                btn_area.setText("");
                et_detail_address.setText("");
            } else if (clickId == R.id.btn_city) {
                String cityName = list_show.get(positon);
                btn_city.setText(cityName);
                btn_area.setText("");
                et_detail_address.setText("");
            } else if (clickId == R.id.btn_area) {
                String areaName = list_show.get(positon);
                btn_area.setText(areaName);
                et_detail_address.setText("");
            }
        }
    };

    int clickId = -1;

    @Override
    public void onClick(View view) {
        if (Biantai.isOneClick()) {
            showToastView("您点击的太快啦");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_province:
                clickId = R.id.btn_province;
                showProListShow();
                break;
            case R.id.btn_city:
                clickId = R.id.btn_city;
                showCityListShow();
                break;
            case R.id.btn_area:
                clickId = R.id.btn_area;
                showAreaListShow();
                break;
            case R.id.btn_submit:
                SharedPerManager.setAutoLocation(false);
                toModifyLocationToWeb();
                break;
            case R.id.lin_exit:   //退出
            case R.id.tv_exit:  //退出
                finish();
                break;
        }
    }

    private void toModifyLocationToWeb() {
        String province = btn_province.getText().toString();
        String city = btn_city.getText().toString();
        String area = btn_area.getText().toString();
        String detailAddress = et_detail_address.getText().toString().trim();
        if (province.contains("请选择") || TextUtils.isEmpty(province)) {
            showToastView("请选择省");
            return;
        }
        if (city.contains("请选择") || TextUtils.isEmpty(city)) {
            showToastView("请选择市");
            return;
        }
        if (area.contains("请选择") || TextUtils.isEmpty(area)) {
            showToastView("请选择区域");
            return;
        }
        SharedPerManager.setProvince(province);
        SharedPerManager.setLocalCity(city);
        SharedPerManager.setArea(area);
        SharedPerManager.setDetailAddress(detailAddress);
        updateDevInfoToWeb();
    }

    /***
     * 显示区域
     */
    private void showAreaListShow() {
        String cityNameShow = btn_city.getText().toString();
        if (cityNameShow.contains("请选择") || TextUtils.isEmpty(cityNameShow)) {
            showToastView("请选择市");
            return;
        }
        list_show.clear();
        ProvinceEntity entntyPro = proList.get(currentPosition);
        List<CityEntity> cityList = entntyPro.getCityList();
        String cityName = btn_city.getText().toString();
        List<AreaEntity> listArea = new ArrayList<>();
        for (int i = 0; i < cityList.size(); i++) {
            String saveCityName = cityList.get(i).getName();
            MyLog.cdl("===比对==" + cityName + " / " + saveCityName);
            if (saveCityName.contains(cityName)) {
                listArea = cityList.get(i).getAreaList();
            }
        }
        MyLog.cdl("===比对listArea==" + listArea.size());
        for (int k = 0; k < listArea.size(); k++) {
            list_show.add(listArea.get(k).getName());
        }
        adapter_pop.setList(list_show);
        pop_province.showAsDropDown(btn_area);
    }

    /***
     * 显示省份列表
     */
    private void showProListShow() {
        list_show.clear();
        for (int i = 0; i < proList.size(); i++) {
            list_show.add(proList.get(i).getName());
        }
        adapter_pop.setList(list_show);
        pop_province.showAsDropDown(btn_province);
    }

    /***
     * 显示城市
     */
    private void showCityListShow() {
        list_show.clear();
        ProvinceEntity entntyPro = proList.get(currentPosition);
        List<CityEntity> cityList = entntyPro.getCityList();
        for (int i = 0; i < cityList.size(); i++) {
            list_show.add(cityList.get(i).getName());
        }
        adapter_pop.setList(list_show);
        pop_province.showAsDropDown(btn_city);
    }

    /***
     * 修改信息给服务器
     */
    public void updateDevInfoToWeb() {
        if (!NetWorkUtils.isNetworkConnected(ProCityDialogActivity.this)) {
            showToastView("网络连接失败，提交设备信息终止");
            return;
        }
        waitDialogUtil.show("修改中");
        String url = ApiInfo.UPDATE_DEV_INFO();
        String clNo = CodeUtil.getUniquePsuedoID();
        MySDCard mySDCard = new MySDCard(ProCityDialogActivity.this);
        String clMac = CodeUtil.getEthMAC();
        String clIp = CodeUtil.getIpAddress(ProCityDialogActivity.this, "城市旋转器界面");
        String sdcardPath = AppInfo.BASE_SD_PATH();
        long sizeLast = mySDCard.getAvailableExternalMemorySize(sdcardPath, 1024 * 1024);
        String sysCodeVersion = CodeUtil.getSystCodeVersion(ProCityDialogActivity.this);
        String address = SharedPerManager.getAllAddress();   //获取设备详细地址
        MyLog.http("提交设备sysCodeVersion信息=" + sysCodeVersion);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("clNo", clNo)
                .addParams("clDisk", sizeLast + "M")
                .addParams("clMac", clMac)
                .addParams("clAddress", address)
                .addParams("clIp", clIp)
                .addParams("clSystemVersion", sysCodeVersion + "")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.http("提交设备信息failed=" + errorDesc);
                        waitDialogUtil.dismiss();
                        showToastView("修改失败：网络异常");
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.http("提交设备信息success=" + json);
                        waitDialogUtil.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code == 0) {
                                showToastView("修改成功");
                                finish();
                            } else {
                                showToastView(msg);
                            }
                        } catch (Exception e) {
                            showToastView("解析异常:" + e.toString());
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(MESSAGE_GET_PRO_OVER);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            KeyBoardUtil.showKeyBord(view);
        } else {
            KeyBoardUtil.hiddleBord(view);
        }
    }
}
