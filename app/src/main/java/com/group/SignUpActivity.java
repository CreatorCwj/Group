package com.group;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseActivity;
import com.leancloud.SafeLogInCallback;
import com.leancloud.SafeRequestMobileCodeCallback;
import com.leancloud.SafeSaveCallback;
import com.model.User;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.CancelableEditView;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_sign_up)
public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private static final int COUNTDOWN_TIME = 5;//倒计时5秒

    @InjectView(R.id.sign_up_username_et)
    private CancelableEditView usernameEt;

    @InjectView(R.id.sign_up_code_et)
    private CancelableEditView codeEt;

    @InjectView(R.id.sign_up_pwd_et)
    private CancelableEditView pwdEt;

    @InjectView(R.id.sign_up_code_tv)
    private TextView codeText;

    @InjectView(R.id.sign_up_btn)
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCodeText();
        setListener();
    }

    private void initCodeText() {
        int radius = UIUtils.dp2px(this, 3);
        int strokeWidth = UIUtils.dp2px(this, 1);
        int strokeColor = getResources().getColor(R.color.colorPrimary);
        int unEnabledColor = getResources().getColor(R.color.dividerColor);
        codeText.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_UNENABLED}, radius, unEnabledColor)
                , new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, radius, getResources().getColor(R.color.iconPressed), strokeWidth, strokeColor)
                , new DrawableUtils.CornerStateDrawable(new int[]{}, radius, Color.TRANSPARENT, strokeWidth, strokeColor)));
    }

    private void setListener() {
        codeText.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_code_tv:
                //发送验证码
                sendCode();
                break;
            case R.id.sign_up_btn:
                //注册
                signUp();
                break;
        }
    }

    //已有账号也可能成功(相当于重置密码,不影响正常逻辑)
    private void signUp() {
        final String phone = usernameEt.getText();
        if (!Utils.isCorrectPhone(phone)) {
            Utils.showToast(this, "请输入正确手机号");
            return;
        }
        String code = codeEt.getText();
        if (TextUtils.isEmpty(code)) {
            Utils.showToast(SignUpActivity.this, "请输入验证码");
            return;
        }
        final String pwd = pwdEt.getText();
        if (TextUtils.isEmpty(pwd)) {
            Utils.showToast(SignUpActivity.this, "请输入密码");
            return;
        }
        showLoadingDialog("正在注册...");
        AVUser.signUpOrLoginByMobilePhoneInBackground(phone, code, User.class, new SafeLogInCallback<User>(this) {

            @Override
            public void logIn(User user, AVException e) {
                if (AVUser.getCurrentUser() != null)//***注册时不自动登录
                    AVUser.logOut();
                if (e == null) {
                    user.setPassword(pwd);
                    user.saveInBackground(new SafeSaveCallback(SignUpActivity.this) {
                        @Override
                        public void save(AVException e) {
                            if (AVUser.getCurrentUser() != null)//***注册时不自动登录
                                AVUser.logOut();
                            if (e == null) {
                                Utils.showToast(SignUpActivity.this, "注册成功");
                                //返回登录界面,带回信息
                                Intent intent = new Intent();
                                intent.putExtra(USERNAME, phone);
                                intent.putExtra(PASSWORD, pwd);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Utils.showToast(SignUpActivity.this, "注册失败");
                            }
                            cancelLoadingDialog();
                        }
                    });
                } else {
                    Utils.showToast(SignUpActivity.this, "注册失败");
                    cancelLoadingDialog();
                }
            }
        });
    }

    private void sendCode() {
        String phone = usernameEt.getText();
        if (!Utils.isCorrectPhone(phone)) {
            Utils.showToast(this, "请输入正确手机号");
            return;
        }
        //先改变按钮状态,确保不能再点
        changeCodeState(false);
        //发送
        showLoadingDialog("正在发送验证码...");
        AVOSCloud.requestSMSCodeInBackground(phone, new SafeRequestMobileCodeCallback(this) {

            @Override
            public void sendResult(AVException e) {
                if (e == null) {
                    Utils.showToast(SignUpActivity.this, "验证码发送成功");
                    //发送成功,开始倒计时
                    setCodeTime(COUNTDOWN_TIME);
                    handler.sendEmptyMessageDelayed(1, 1000);//1秒1发,what代表进行了几秒
                } else {
                    //发送失败重置状态
                    changeCodeState(true);
                    Utils.showToast(SignUpActivity.this, "验证码发送失败");
                }
                cancelLoadingDialog();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int spare = COUNTDOWN_TIME - msg.what;//剩余多少秒
            if (spare == 0) {//倒计时结束,改变状态
                changeCodeState(true);
                return;
            }
            setCodeTime(spare);
            codeText.setText(spare + "秒后重新发送");
            this.sendEmptyMessageDelayed(msg.what + 1, 1000);//1秒1发,what代表进行了几秒
        }
    };

    private void changeCodeState(boolean enabled) {
        codeText.setEnabled(enabled);
        codeText.setClickable(enabled);
        if (enabled)
            codeText.setText("发送验证码");
    }

    private void setCodeTime(int seconds) {
        codeText.setText(seconds + "秒后重新发送");
    }
}
