package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseActivity;
import com.leancloud.SafeLogInCallback;
import com.model.User;
import com.util.Utils;
import com.widget.CancelableEditView;
import com.widget.CustomToolBar;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

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
                Utils.showToast(LoginActivity.this, "忘记密码");
                break;
            case R.id.quick_login_tv:
                Utils.showToast(LoginActivity.this, "快速登录");
                break;
        }
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
                    Utils.showToast(LoginActivity.this, "登录成功");
                    finish();
                } else {
                    Utils.showToast(LoginActivity.this, "登录失败");
                }
                cancelLoadingDialog();
            }
        }, User.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SIGN_UP_CODE) {//自动填写信息
                usernameEt.setText(data.getStringExtra(SignUpActivity.USERNAME));
                pwdEt.setText(data.getStringExtra(SignUpActivity.PASSWORD));
            }
        }
    }
}
