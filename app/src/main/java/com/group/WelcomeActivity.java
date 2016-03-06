package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dao.dbHelpers.CityHelper;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.util.AppSetting;
import com.util.JsonUtils;
import com.util.Utils;

import java.util.List;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {

    private static final long WAIT_TIME = 2500;

    @Inject
    private CityHelper cityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCity();//从raw文件写入到数据库
        handler.sendEmptyMessageDelayed(0, WAIT_TIME);
    }

    private void loadCity() {
        if (!cityHelper.isEmpty())
            return;
        String cityJson = Utils.readRawFile(this, R.raw.city);
        List<com.dao.generate.City> cities = JsonUtils.toList(cityJson, com.dao.generate.City.class);
        cityHelper.insertData(cities);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isFinishing())
                return;
            //根据是否是第一次进入(SharedPreference)应用决定跳转界面(选择城市或主界面)
            if (AppSetting.getStartCount() == 0) {
                //city select
                Intent intent = new Intent(WelcomeActivity.this, SelectCityActivity.class);
                intent.putExtra(SelectCityActivity.FIRST_COME_KEY, true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            finish();
        }
    };

}
