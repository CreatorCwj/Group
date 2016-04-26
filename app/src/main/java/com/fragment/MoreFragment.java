package com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fragment.base.BaseSlideFragment;
import com.group.R;
import com.group.ScannerActivity;
import com.imageLoader.FileCache;
import com.imageLoader.ImageLoader;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.functionButton.FunctionButton;
import com.widget.functionButton.SwitchFunctionButton;

import java.io.File;
import java.text.DecimalFormat;

import roboguice.inject.InjectView;

public class MoreFragment extends BaseSlideFragment implements View.OnClickListener, SwitchFunctionButton.OnSwitchListener {

    @InjectView(R.id.more_wifi_btn)
    private SwitchFunctionButton wifiBtn;

    @InjectView(R.id.more_push_btn)
    private SwitchFunctionButton pushBtn;

    @InjectView(R.id.more_cache_btn)
    private FunctionButton cacheBtn;

    @InjectView(R.id.more_scanner_btn)
    private FunctionButton scannerBtn;

    @InjectView(R.id.more_update_btn)
    private FunctionButton updateBtn;

    @InjectView(R.id.more_advice_btn)
    private FunctionButton adviceBtn;

    @InjectView(R.id.more_about_btn)
    private FunctionButton aboutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListener();
    }

    @Override
    public void onSlideFragmentResume() {
        super.onSlideFragmentResume();
        //重新获取数据
        refreshCacheSize();
        refreshWifiEnv();
        refreshPush();
    }

    private void refreshPush() {
        boolean push = AppSetting.getPush();
        pushBtn.setSwitch(push, false);
    }

    private void refreshWifiEnv() {
        boolean wifiEnv = AppSetting.getWifiEnv();
        wifiBtn.setSwitch(wifiEnv, false);
    }

    private void refreshCacheSize() {
        File dir = FileCache.getCacheDir();
        if (dir != null) {
            double size = Utils.getDirSize(FileCache.getCacheDir());
            DecimalFormat df = new DecimalFormat("0.00");
            String sizeStr = df.format(size) + "K";
            cacheBtn.setDescribe(sizeStr);
        }
    }

    private void setListener() {
        wifiBtn.setOnSwitchListener(this);
        pushBtn.setOnSwitchListener(this);
        cacheBtn.setOnClickListener(this);
        scannerBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        adviceBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_cache_btn:
                ImageLoader.clearDiskCache();
                refreshCacheSize();
                Utils.showToast(getActivity(), "已清空缓存");
                break;
            case R.id.more_scanner_btn://扫一扫
                startActivity(new Intent(getActivity(), ScannerActivity.class));
                break;
            case R.id.more_update_btn:
                Utils.showToast(getActivity(), updateBtn.getDescribe());
                break;
            case R.id.more_advice_btn:
                break;
            case R.id.more_about_btn:
                break;
        }
    }

    @Override
    public void onSwitch(View v, boolean isOn) {
        switch (v.getId()) {
            case R.id.more_wifi_btn:
                AppSetting.setWifiEnv(isOn);
                Utils.showToast(getActivity(), (isOn ? "省流量功能打开" : "省流量功能关闭"));
                break;
            case R.id.more_push_btn:
                AppSetting.setPush(isOn);
                Utils.showToast(getActivity(), (isOn ? "推送功能打开" : "推送功能关闭"));
                break;
        }
    }
}
