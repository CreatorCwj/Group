package com.group;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseActivity;
import com.leancloud.SafeRequestMobileCodeCallback;
import com.leancloud.SafeUpdPwdCallback;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.CancelableEditView;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_forget_pwd)
public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final int COUNTDOWN_TIME = 60;//倒计时60秒

    @InjectView(R.id.sign_up_username_et)
    private CancelableEditView usernameEt;

    @InjectView(R.id.sign_up_code_et)
    private CancelableEditView codeEt;

    @InjectView(R.id.sign_up_pwd_et)
    private CancelableEditView pwdEt;

    @InjectView(R.id.sign_up_pwd2_et)
    private CancelableEditView pwd2Et;

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
                //重置密码
                resetPwd();
                break;
        }
    }

    private void resetPwd() {
        String code = codeEt.getText();
        if (TextUtils.isEmpty(code)) {
            Utils.showToast(ForgetPwdActivity.this, "请输入验证码");
            return;
        }
        final String pwd = pwdEt.getText();
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6 || pwd.length() > 18) {
            Utils.showToast(ForgetPwdActivity.this, "请输入正确6-18位密码");
            return;
        }
        String pwd2 = pwd2Et.getText();
        if (TextUtils.isEmpty(pwd2)) {
            Utils.showToast(ForgetPwdActivity.this, "请输入确认密码");
            return;
        }
        if (!pwd2.equals(pwd)) {
            Utils.showToast(ForgetPwdActivity.this, "两次密码不同");
            return;
        }
        showLoadingDialog("重置密码...");
        AVUser.resetPasswordBySmsCodeInBackground(code, pwd, new SafeUpdPwdCallback(this) {
            @Override
            public void update(AVException e) {
                if (e != null) {
                    Utils.showToast(ForgetPwdActivity.this, "重置密码失败");
                } else {
                    Utils.showToast(ForgetPwdActivity.this, "重置密码成功");
                    finish();
                }
                cancelLoadingDialog();
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
        AVUser.requestPasswordResetBySmsCodeInBackground(phone, new SafeRequestMobileCodeCallback(this) {
            @Override
            public void sendResult(AVException e) {
                if (e == null) {
                    Utils.showToast(ForgetPwdActivity.this, "验证码发送成功");
                    //发送成功,开始倒计时
                    setCodeTime(COUNTDOWN_TIME);
                    handler.sendEmptyMessageDelayed(1, 1000);//1秒1发,what代表进行了几秒
                } else {
                    //发送失败重置状态
                    changeCodeState(true);
                    Utils.showToast(ForgetPwdActivity.this, "验证码发送失败");
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
