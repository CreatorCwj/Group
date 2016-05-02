package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.constant.CloudFunction;
import com.dao.dbHelpers.CityHelper;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.model.User;
import com.util.AppSetting;
import com.util.JsonUtils;
import com.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {

    private static final long WAIT_TIME = 2500;

    @Inject
    private CityHelper cityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPush();//初始化推送与用户绑定
        loadCity();//从raw文件写入到数据库
        handler.sendEmptyMessageDelayed(0, WAIT_TIME);
    }

    private void initPush() {
        //默认启动界面(也可以用subscribe/unsubscribe来订阅/退订某个频道(名字只能由26字母和数字构成)对应打开的界面,订阅要在保存installation前,退订后也要重新save一下installation)
        PushService.setDefaultPushCallback(getApplicationContext(), HomeActivity.class);
        //注册设备
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.i("InstallationId", "推送初始化成功");
                    setPushId();//与用户绑定
                } else {
                    Log.i("InstallationId", "推送初始化失败");
                }
            }
        });
    }

    private void setPushId() {
        //获取到唯一的注册ID，卸载后id也删除(可以理解为存储在app包的一个数据,不卸载就一直用一个)
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("pushId", installationId);
            AVCloud.rpcFunctionInBackground(CloudFunction.SET_PUSH_ID, params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, AVException e) {
                    if (e != null) {
                        Log.i("setPushId", "用户推送注册失败");
                    } else {
                        Log.i("setPushId", "用户推送注册成功");
                    }
                }
            });
        }
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
