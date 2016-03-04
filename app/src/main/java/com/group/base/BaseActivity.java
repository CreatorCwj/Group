package com.group.base;

import android.os.Bundle;

import com.volley.Network;
import com.widget.dialog.LoadingDialog;

import roboguice.activity.RoboActivity;

/**
 * Created by cwj on 15/11/25.
 * Activity基类
 */
public abstract class BaseActivity extends RoboActivity {

    protected final Object NETWORK_TAG = this;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);
    }

    public void showLoadingDialog(String msg) {
        loadingDialog.show(msg);
    }

    public void showLoadingDialog() {
        loadingDialog.show();
    }

    public void cancelLoadingDialog() {
        loadingDialog.cancel();
    }

    @Override
    protected void onDestroy() {
        Network.cancelRequest(NETWORK_TAG);
        super.onDestroy();
    }
}
