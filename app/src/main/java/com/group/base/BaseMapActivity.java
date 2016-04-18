package com.group.base;

import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.group.R;
import com.widget.RoundImageView;

import roboguice.inject.InjectView;

/**
 * Created by cwj on 16/4/21.
 * 带地图的activity基类
 */
public class BaseMapActivity extends BaseActivity {

    @InjectView(R.id.map_mapView)
    protected MapView mapView;

    @InjectView(R.id.map_back_iv)
    protected RoundImageView backIv;

    protected BaiduMap baiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMap();
        setBack();
    }

    private void setBack() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initMap() {
        baiduMap = mapView.getMap();
        baiduMap.getUiSettings().setCompassEnabled(false);
        mapView.showZoomControls(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
