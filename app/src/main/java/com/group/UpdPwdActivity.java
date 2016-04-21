package com.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseActivity;
import com.leancloud.SafeUpdPwdCallback;
import com.model.User;
import com.util.Utils;
import com.widget.CancelableEditView;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_upd_pwd)
public class UpdPwdActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.upd_pwd_ori_pwd_cet)
    private CancelableEditView oriPwdCet;

    @InjectView(R.id.upd_pwd_new_pwd_cet)
    private CancelableEditView newPwdCet;

    @InjectView(R.id.upd_pwd_new_pwd2_cet)
    private CancelableEditView newPwdCet2;

    @InjectView(R.id.upd_pwd_btn)
    private Button updBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListener();
    }

    private void setListener() {
        updBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upd_pwd_btn:
                updPwd();
                break;
        }
    }

    private void updPwd() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null)
            return;
        String oriPwd = oriPwdCet.getText();
        if (TextUtils.isEmpty(oriPwd)) {
            Utils.showToast(UpdPwdActivity.this, "请输入原密码");
            return;
        }
        String newPwd = newPwdCet.getText();
        if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6 || newPwd.length() > 18) {
            Utils.showToast(UpdPwdActivity.this, "请输入正确6-18位新密码");
            return;
        }
        String newPwd2 = newPwdCet2.getText();
        if (TextUtils.isEmpty(newPwd2)) {
            Utils.showToast(UpdPwdActivity.this, "请输入确认密码");
            return;
        }
        if (!newPwd2.equals(newPwd)) {
            Utils.showToast(UpdPwdActivity.this, "两次密码不同");
            return;
        }
        showLoadingDialog("修改密码...");
        user.updatePasswordInBackground(oriPwd, newPwd, new SafeUpdPwdCallback(this) {
            @Override
            public void update(AVException e) {
                if (e != null) {
                    Utils.showToast(UpdPwdActivity.this, "修改密码失败");
                } else {
                    Utils.showToast(UpdPwdActivity.this, "修改密码成功");
                    finish();
                }
                cancelLoadingDialog();
            }
        });
    }
}
