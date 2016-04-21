package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.group.base.BaseActivity;
import com.leancloud.SafeFunctionCallback;
import com.model.User;
import com.util.Utils;
import com.widget.CancelableEditView;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_upd_display_name)
public class UpdDisplayNameActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.upd_display_name_cet)
    private CancelableEditView displayNameCet;

    @InjectView(R.id.upd_display_name_btn)
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInfo();
        setListener();
    }

    private void setInfo() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            displayNameCet.setText(user.getDisplayName());
        }
    }

    private void setListener() {
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upd_display_name_btn:
                submit();
                break;
        }
    }

    private void submit() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null)
            return;
        String displayName = displayNameCet.getText();
        if (TextUtils.isEmpty(displayName)) {
            Utils.showToast(this, "昵称不能为空");
            return;
        }
        if (displayName.length() < 4 || displayName.length() > 15) {
            Utils.showToast(this, "昵称为4-15位");
            return;
        }
        showLoadingDialog("修改昵称...");
        Map<String, Object> params = new HashMap<>();
        params.put("displayName", displayName);
        AVCloud.rpcFunctionInBackground(CloudFunction.UPD_DISPLAY_NAME, params, new SafeFunctionCallback<User>(this) {
            @Override
            protected void functionBack(User user, AVException e) {
                if (e != null) {
                    Utils.showToast(UpdDisplayNameActivity.this, "修改失败");
                } else {
                    Utils.showToast(UpdDisplayNameActivity.this, "修改成功");
                    Intent intent = new Intent();
                    intent.putExtra(PersonActivity.REFRESH_KEY, true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                cancelLoadingDialog();
            }
        });

    }
}
