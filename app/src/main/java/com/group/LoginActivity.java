package com.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.group.base.BaseActivity;
import com.leancloud.SafeFunctionCallback;
import com.leancloud.SafeLogInCallback;
import com.model.User;
import com.util.Utils;
import com.widget.CancelableEditView;
import com.widget.CustomToolBar;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final int QUICK_LOGIN_CODE = 0;
    private static final int SIGN_UP_CODE = 1;

    @InjectView(R.id.login_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.login_username_et)
    private CancelableEditView usernameEt;

    @InjectView(R.id.login_pwd_et)
    private CancelableEditView pwdEt;

    @InjectView(R.id.login_btn)
    private Button loginBtn;

    @InjectView(R.id.forget_pwd_tv)
    private TextView forgetPwdTv;

    @InjectView(R.id.quick_login_tv)
    private TextView quickLoginTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        setListener();
    }

    private void initToolbar() {
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开注册,接受注册成功返回的用户名密码自动填写到et上
                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), SIGN_UP_CODE);
            }
        });
    }

    private void setListener() {
        loginBtn.setOnClickListener(this);
        forgetPwdTv.setOnClickListener(this);
        quickLoginTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.forget_pwd_tv:
                gotoActivity(ForgetPwdActivity.class);
                break;
            case R.id.quick_login_tv:
                gotoActivity(QuickLoginActivity.class);
                break;
        }
    }

    private void gotoActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(LoginActivity.this, clazz);
        startActivityForResult(intent, QUICK_LOGIN_CODE);
    }

    private void login() {
        String username = usernameEt.getText();
        if (!Utils.isCorrectPhone(username)) {
            Utils.showToast(LoginActivity.this, "请输入正确手机号");
            return;
        }
        String pwd = pwdEt.getText();
        if (TextUtils.isEmpty(pwd)) {
            Utils.showToast(LoginActivity.this, "请输入密码");
            return;
        }
        //登录
        showLoadingDialog("登录中...");
        AVUser.loginByMobilePhoneNumberInBackground(username, pwd, new SafeLogInCallback<User>(this) {

            @Override
            public void logIn(User user, AVException e) {
                if (e == null && AVUser.getCurrentUser(User.class) != null) {
                    AVUser.getCurrentUser(User.class).setFetchWhenSave(true);
                    setPushId();
                    Utils.showToast(LoginActivity.this, "登录成功");
                    setResult(RESULT_OK);//供startActivityForResult情况(需要知道登陆成功)使用
                    finish();
                } else {
                    Utils.showToast(LoginActivity.this, "登录失败:" + (e == null ? "" : e.getMessage()));
                }
                cancelLoadingDialog();
            }
        }, User.class);
    }

    private void setPushId() {
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
        if (!TextUtils.isEmpty(installationId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("pushId", installationId);
            AVCloud.rpcFunctionInBackground(CloudFunction.SET_PUSH_ID, params, new SafeFunctionCallback<String>(this) {
                @Override
                protected void functionBack(String s, AVException e) {
                    if (e != null) {
                        Log.i("setPushId", "用户注册失败");
                    } else {
                        Log.i("setPushId", "用户注册成功");
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SIGN_UP_CODE) {//自动填写信息
                usernameEt.setText(data.getStringExtra(SignUpActivity.USERNAME));
                pwdEt.setText(data.getStringExtra(SignUpActivity.PASSWORD));
            } else if (requestCode == QUICK_LOGIN_CODE) {//登陆成功,自动关闭界面
                finish();
            }
        }
    }
}
