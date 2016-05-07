package com.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.constant.GradeEnum;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeFunctionCallback;
import com.leancloud.SafeGetCallback;
import com.model.User;
import com.util.Utils;
import com.widget.functionButton.FunctionButton;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_person)
public class PersonActivity extends BaseActivity implements View.OnClickListener {

    public static final String REFRESH_KEY = "isRefresh";

    private final int UPD_USER_CODE = 1;

    @InjectView(R.id.person_iv)
    private ImageView userIv;

    @InjectView(R.id.person_lv_tv)
    private TextView lvTv;

    @InjectView(R.id.person_growth_tv)
    private TextView growthTv;

    @InjectView(R.id.person_point_tv)
    private TextView pointTv;

    @InjectView(R.id.person_username_tv)
    private TextView usernameTv;

    @InjectView(R.id.person_icon_fb)
    private FunctionButton iconFb;

    @InjectView(R.id.person_username_fb)
    private FunctionButton usernameFb;

    @InjectView(R.id.person_phone_fb)
    private FunctionButton phoneFb;

    @InjectView(R.id.person_pwd_fb)
    private FunctionButton pwdFb;

    @InjectView(R.id.person_logout_btn)
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListener();
        setInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPD_USER_CODE) {
                boolean isRefresh = data.getBooleanExtra(REFRESH_KEY, false);
                if (isRefresh)//需要刷新获取用户对象
                    fetch();
            }
        }
    }

    private void fetch() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            user.fetchInBackground(new SafeGetCallback<AVObject>(this) {//获取最新的用户对象
                @Override
                public void getResult(AVObject object, AVException e) {
                    setInfo();
                }
            });
        } else {
            setInfo();
        }
    }

    private void setInfo() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            //user
            if (TextUtils.isEmpty(user.getImageUrl())) {
                userIv.setImageResource(R.drawable.no_user_icon);
            } else {
                ImageLoader.displayImage(userIv, user.getImageUrl());
            }
            lvTv.setText("Lv" + GradeEnum.getGradeByValue(user.getGrowthValue()).getGrade());
            growthTv.setText("成长值:" + user.getGrowthValue());
            AVCloud.rpcFunctionInBackground(CloudFunction.GET_POINT, null, new SafeFunctionCallback<Map>(this) {
                @Override
                protected void functionBack(Map map, AVException e) {
                    if (e != null) {
                        pointTv.setText("积分:0");
                    } else {
                        pointTv.setText("积分:" + map.get("point"));
                    }
                }
            });
            if (TextUtils.isEmpty(user.getDisplayName())) {//没有昵称用username
                usernameTv.setText(user.getUsername());
            } else {
                usernameTv.setText(user.getDisplayName());
            }
            //fb
            if (TextUtils.isEmpty(user.getDisplayName())) {
                usernameFb.setName("用户昵称");
            } else {
                usernameFb.setName(user.getDisplayName());
            }
            phoneFb.setName(getPhone(user.getUsername()));
        } else {
            //user
            userIv.setImageResource(R.drawable.no_user_icon);
            lvTv.setText("Lv0");
            growthTv.setText("成长值:0");
            pointTv.setText("积分:0");
            usernameTv.setText("");
            //fb
            usernameFb.setName("用户昵称");
            phoneFb.setName("绑定手机号");
        }
    }

    private String getPhone(String username) {
        if (TextUtils.isEmpty(username) || username.length() != 11)
            return "";
        return username.substring(0, 3) + "****" + username.substring(7);
    }

    private void setListener() {
        iconFb.setOnClickListener(this);
        usernameFb.setOnClickListener(this);
        phoneFb.setOnClickListener(this);
        pwdFb.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_icon_fb://更换头像
                gotoActivity(UpdUserIconActivity.class);
                break;
            case R.id.person_username_fb://更改昵称
                gotoActivity(UpdDisplayNameActivity.class);
                break;
            case R.id.person_phone_fb://换绑手机号
                Utils.showToast(this, "该功能暂未开启");
                break;
            case R.id.person_pwd_fb://修改密码
                gotoActivity(UpdPwdActivity.class);
                break;
            case R.id.person_logout_btn://注销
                logOut();
                break;
        }
    }

    private void gotoActivity(Class<? extends Activity> clazz) {
        if (!isLogin())
            return;
        Intent intent = new Intent(PersonActivity.this, clazz);
        startActivityForResult(intent, UPD_USER_CODE);
    }

    private void logOut() {
        if (!isLogin())
            return;
        setPushId();
        AVUser.logOut();
        Utils.showToast(this, "已注销");
        finish();
    }

    private void setPushId() {
        Map<String, Object> params = new HashMap<>();
        params.put("pushId", "");
        AVCloud.rpcFunctionInBackground(CloudFunction.SET_PUSH_ID, params, new SafeFunctionCallback<Object>(this) {
            @Override
            protected void functionBack(Object s, AVException e) {
                if (e != null) {
                    Log.i("setPushId", "用户注册失败");
                } else {
                    Log.i("setPushId", "用户注册成功");
                }
            }
        });
    }

    private boolean isLogin() {
        return AVUser.getCurrentUser() != null;
    }
}
