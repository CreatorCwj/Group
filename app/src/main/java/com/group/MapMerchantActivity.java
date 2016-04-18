package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.constant.CategoryEnum;
import com.constant.RouteTypeEnum;
import com.dao.generate.City;
import com.group.base.BaseMapActivity;
import com.location.Location;
import com.location.OnLocationListener;
import com.model.Category;
import com.model.Merchant;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.RoundImageView;
import com.widget.radio.RadioView;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_map_merchant)
public class MapMerchantActivity extends BaseMapActivity implements View.OnClickListener, OnGetRoutePlanResultListener {

    public static final String MERCHANT_KEY = "merchant";

    @InjectView(R.id.map_location_iv)
    private RoundImageView locateIv;

    @InjectView(R.id.map_best_route_layout)
    private LinearLayout bestRouteLayout;

    @InjectView(R.id.map_best_route_rv)
    private RadioView bestRv;

    @InjectView(R.id.map_best_route_tv)
    private TextView bestTv;

    @InjectView(R.id.map_route_rv2)
    private RadioView rv2;

    @InjectView(R.id.map_route_rv3)
    private RadioView rv3;

    private LatLng myLocation;
    private Marker myMarker;

    private Merchant merchant;
    private LatLng merchantLocation;

    private RoutePlanSearch tranSearch;
    private RoutePlanSearch driveSearch;
    private RoutePlanSearch walkSearch;
    private boolean tranFlag, driveFlag, walkFlag;
    private int tranTime, driveTime, walkTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setSearch();
        setListener();
        initMerchantOverlay();
        locateMySelf(false);
    }

    private void initMerchantOverlay() {
        //标注覆盖物
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
        MarkerOptions options = new MarkerOptions().position(merchantLocation).icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow).title("商家位置");
        baiduMap.addOverlay(options);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(merchantLocation, 14));//设为中心且进行一定缩放
        //弹出窗
        InfoWindow infoWindow = new InfoWindow(getMarkerView(), merchantLocation, -64 * 2);
        baiduMap.showInfoWindow(infoWindow);
    }

    private View getMarkerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.map_merchant_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.map_merchant_item_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.map_merchant_item_name_tv);
        TextView averageTv = (TextView) view.findViewById(R.id.map_merchant_item_average_tv);
        setCatIv(iv, merchant.getCategory());
        nameTv.setText(merchant.getName());
        averageTv.setText("人均:¥" + merchant.getAverage());
        return view;
    }

    private void setCatIv(ImageView categoryIv, Category category) {
        CategoryEnum categoryEnum = CategoryEnum.getEnumMap(category);
        if (categoryEnum == null) {
            categoryIv.setImageDrawable(null);
            return;
        }
        categoryIv.setImageResource(categoryEnum.getBigIconId());
    }

    private void receiveIntent() {
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
        merchantLocation = new LatLng(merchant.getLocation().getLatitude(), merchant.getLocation().getLongitude());
    }

    private void setSearch() {
        tranSearch = RoutePlanSearch.newInstance();
        driveSearch = RoutePlanSearch.newInstance();
        walkSearch = RoutePlanSearch.newInstance();
    }

    private void setListener() {
        locateIv.setOnClickListener(this);
        bestRouteLayout.setOnClickListener(this);
        rv2.setOnClickListener(this);
        rv3.setOnClickListener(this);
        tranSearch.setOnGetRoutePlanResultListener(this);
        driveSearch.setOnGetRoutePlanResultListener(this);
        walkSearch.setOnGetRoutePlanResultListener(this);
    }

    private void locateMySelf(final boolean isManual) {
        //mock
//        if (myMarker != null)
//            myMarker.remove();
//        myLocation = new LatLng(39.073719, 117.114159);
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//        MarkerOptions options = new MarkerOptions().position(myLocation).icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow).title("我的位置");
//        myMarker = (Marker) baiduMap.addOverlay(options);
//        if (isManual)//手动则居中到当前位置
//            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(myLocation, 14));//设为中心且进行一定缩放
//        //重新计算路程
//        searchRoute();

        Location.requestLocation(this, new OnLocationListener() {
            @Override
            public void onPreExecute() {
                Utils.showToast(MapMerchantActivity.this, "正在定位...");
            }

            @Override
            public void onSuccess(BDLocation location) {
                Utils.showToast(MapMerchantActivity.this, "定位成功");
                if (myMarker != null)
                    myMarker.remove();
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
                MarkerOptions options = new MarkerOptions().position(myLocation).icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow).title("我的位置");
                myMarker = (Marker) baiduMap.addOverlay(options);
                if (isManual)//手动则居中到当前位置
                    baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(myLocation, 14));//设为中心且进行一定缩放
                //重新计算路程
                searchRoute();
            }

            @Override
            public void onFailed() {
                Utils.showToast(MapMerchantActivity.this, "定位失败");
            }

            @Override
            public void onFinally() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_location_iv://定位
                locateMySelf(true);
                break;
            case R.id.map_best_route_layout://最佳路线
                gotoRouteListActivity(bestRv.getRadioText());
                break;
            case R.id.map_route_rv2://路线2
                gotoRouteListActivity(rv2.getRadioText());
                break;
            case R.id.map_route_rv3://路线3
                gotoRouteListActivity(rv3.getRadioText());
                break;
        }
    }

    private void gotoRouteListActivity(String typeName) {
        if (myLocation == null) {
            Utils.showToast(this, "定位失败,请定位后重试");
            return;
        }
        RouteTypeEnum typeEnum = RouteTypeEnum.getTypeByName(typeName);
        if (typeEnum != null) {
            //传入并跳到路线列表界面
            Intent intent = new Intent(MapMerchantActivity.this, RouteListActivity.class);
            intent.putExtra(RouteListActivity.MERCHANT_KEY, merchant);
            intent.putExtra(RouteListActivity.MY_LOCATION_KEY, myLocation);
            intent.putExtra(RouteListActivity.FIRST_TYPE_KEY, typeEnum);
            startActivity(intent);
        }
    }

    private void updRadioView() {
        if (isFinishing())
            return;
        if (isSmaller(tranTime, driveTime) && isSmaller(tranTime, walkTime)) {//公交最短
            updRadioView(RouteTypeEnum.TRANSIT, tranTime, RouteTypeEnum.DRIVE, RouteTypeEnum.WALK);
        } else if (isSmaller(driveTime, tranTime) && isSmaller(driveTime, walkTime)) {//驾车最短
            updRadioView(RouteTypeEnum.DRIVE, driveTime, RouteTypeEnum.TRANSIT, RouteTypeEnum.WALK);
        } else if (isSmaller(walkTime, tranTime) && isSmaller(walkTime, driveTime)) {//步行最短
            updRadioView(RouteTypeEnum.WALK, walkTime, RouteTypeEnum.TRANSIT, RouteTypeEnum.DRIVE);
        }
    }

    private boolean isSmaller(int time, int tarTime) {
        return (tarTime == -1) || (time != -1 && time <= tarTime);
    }

    private void updRadioView(RouteTypeEnum bestType, int bestTime, RouteTypeEnum secondType, RouteTypeEnum thirdType) {
        bestRv.setImageBackground(bestType.getResId());
        bestRv.setRadioText(bestType.getName());
        if (bestTime == -1)
            bestTv.setText("");
        else bestTv.setText("约" + bestTime + "分钟");
        rv2.setImageBackground(secondType.getResId());
        rv2.setRadioText(secondType.getName());
        rv3.setImageBackground(thirdType.getResId());
        rv3.setRadioText(thirdType.getName());
    }

    //定位成功后计算路线
    private void searchRoute() {
        //reset flag
        resetFlag();
        //交通
        City city = AppSetting.getCity();
        String cityName = (city == null) ? "" : city.getName();
        tranSearch.transitSearch(new TransitRoutePlanOption().from(PlanNode.withLocation(myLocation)).city(cityName).to(PlanNode.withLocation(merchantLocation)));
        //驾车
        driveSearch.drivingSearch(new DrivingRoutePlanOption().from(PlanNode.withLocation(myLocation)).to(PlanNode.withLocation(merchantLocation)));
        //步行
        walkSearch.walkingSearch(new WalkingRoutePlanOption().from(PlanNode.withLocation(myLocation)).to(PlanNode.withLocation(merchantLocation)));
    }

    private void resetFlag() {
        tranFlag = driveFlag = walkFlag = false;
    }

    private boolean isSearchFinished() {
        return tranFlag && driveFlag && walkFlag;
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        walkFlag = true;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            walkTime = -1;
        } else {
            walkTime = getRoutesTime(result.getRouteLines());
        }
        if (isSearchFinished()) {//完成全部路线计算,开始更新view
            updRadioView();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        tranFlag = true;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            tranTime = -1;
        } else {
            tranTime = getRoutesTime(result.getRouteLines());
        }
        if (isSearchFinished()) {//完成全部路线计算,开始更新view
            updRadioView();
        }
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
            return true;
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        driveFlag = true;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            driveTime = -1;
        } else {
            driveTime = getRoutesTime(result.getRouteLines());
        }
        if (isSearchFinished()) {//完成全部路线计算,开始更新view
            updRadioView();
        }
    }

    private int getRoutesTime(List<? extends RouteLine> routeLines) {
        if (routeLines == null || routeLines.size() <= 0)
            return -1;
        int seconds = routeLines.get(0).getDuration();
        for (RouteLine routeLine : routeLines) {
            if (routeLine.getDuration() < seconds) {
                seconds = routeLine.getDuration();
            }
        }
        //换算成与等于的分钟
        int minute = seconds / 60;
        return minute > 0 ? minute : 1;
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

}
