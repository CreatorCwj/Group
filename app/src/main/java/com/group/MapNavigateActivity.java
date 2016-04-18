package com.group;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.RouteStepAdapter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.group.base.BaseMapActivity;
import com.util.Utils;
import com.widget.rlrView.view.RLRView;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_map_navigate)
public class MapNavigateActivity extends BaseMapActivity {

    public static final String ROUTE_LINE_KEY = "routeLine";
    public static final String ROUTE_NAME_KEY = "routeName";
    public static final String ROUTE_STATISTIC_KEY = "routeStatistic";

    @InjectView(R.id.map_navigate_name_tv)
    private TextView nameTv;

    @InjectView(R.id.map_navigate_statistics_tv)
    private TextView statisticsTv;

    @InjectView(R.id.map_navigate_rlrView)
    private RLRView rlrView;

    private RouteStepAdapter adapter;

    private RouteLine routeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setInfo();
        setRLRView();
        showLoadingDialog("加载路线中...");
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                navigate();
            }
        }.sendEmptyMessageDelayed(0, 500);
    }

    private void navigate() {
        OverlayManager overlay = null;
        //根据路线类型创建overlay
        if (routeLine instanceof TransitRouteLine) {
            overlay = new MyTransitRouteOverlay(baiduMap);
            ((TransitRouteOverlay) overlay).setData((TransitRouteLine) routeLine);
        } else if (routeLine instanceof DrivingRouteLine) {
            overlay = new MyDrivingRouteOverlay(baiduMap);
            ((MyDrivingRouteOverlay) overlay).setData((DrivingRouteLine) routeLine);
        } else if (routeLine instanceof WalkingRouteLine) {
            overlay = new MyWalkingRouteOverlay(baiduMap);
            ((MyWalkingRouteOverlay) overlay).setData((WalkingRouteLine) routeLine);
        }
        try {
            if (overlay != null) {
                baiduMap.setOnMarkerClickListener(overlay);
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelLoadingDialog();
    }

    private void setRLRView() {
        adapter = new RouteStepAdapter(this);
        rlrView.setAdapter(adapter);
        rlrView.resetData(routeLine.getAllStep());
    }

    private void setInfo() {
        nameTv.setText(getIntent().getStringExtra(ROUTE_NAME_KEY));
        statisticsTv.setText(getIntent().getStringExtra(ROUTE_STATISTIC_KEY));
    }

    private void receiveIntent() {
        routeLine = getIntent().getParcelableExtra(ROUTE_LINE_KEY);
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_start);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_end);
        }

        @Override
        public boolean onRouteNodeClick(int i) {
            Utils.showToast(MapNavigateActivity.this, ((TransitRouteLine.TransitStep) routeLine.getAllStep().get(i)).getInstructions(), Toast.LENGTH_LONG);
            return true;
        }

    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_start);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_end);
        }

        @Override
        public boolean onRouteNodeClick(int i) {
            Utils.showToast(MapNavigateActivity.this, ((DrivingRouteLine.DrivingStep) routeLine.getAllStep().get(i)).getInstructions(), Toast.LENGTH_LONG);
            return true;
        }

    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_start);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_end);
        }

        @Override
        public boolean onRouteNodeClick(int i) {
            Utils.showToast(MapNavigateActivity.this, ((WalkingRouteLine.WalkingStep) routeLine.getAllStep().get(i)).getInstructions(), Toast.LENGTH_LONG);
            return true;
        }
    }

}
