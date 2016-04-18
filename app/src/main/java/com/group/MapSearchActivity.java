package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.constant.CategoryEnum;
import com.group.base.BaseMapActivity;
import com.location.Location;
import com.location.OnLocationListener;
import com.model.Category;
import com.model.Merchant;
import com.widget.RoundImageView;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_map_search)
public class MapSearchActivity extends BaseMapActivity implements View.OnClickListener {

    public static final String MERCHANTS_KEY = "merchants";

    private final String BUNDLE_KEY = "merchant";

    @InjectView(R.id.map_location_iv)
    private RoundImageView locateIv;

    @InjectView(R.id.map_address_tv)
    private TextView addressTv;

    private LatLng myLocation;
    private Marker myMarker;

    private List<Merchant> merchants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setListener();
        setMerchants();
        locateMySelf();
    }

    private void setMerchants() {
        if (merchants == null)
            return;
        //展示商家
        for (Merchant merchant : merchants) {
            BitmapDescriptor bd = BitmapDescriptorFactory.fromView(getMarkerView(merchant));
            MarkerOptions options = new MarkerOptions().position(new LatLng(merchant.getLocation().getLatitude(), merchant.getLocation().getLongitude()))
                    .icon(bd).animateType(MarkerOptions.MarkerAnimateType.grow);
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_KEY, merchant);
            options.extraInfo(bundle);
            baiduMap.addOverlay(options);
        }
    }

    private View getMarkerView(Merchant merchant) {
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
        merchants = getIntent().getParcelableArrayListExtra(MERCHANTS_KEY);
    }

    private void locateMySelf() {
        Location.requestLocation(this, new OnLocationListener() {
            @Override
            public void onPreExecute() {
                addressTv.setText("定位中...");
            }

            @Override
            public void onSuccess(BDLocation location) {
                //移除原有的当前定位
                if (myMarker != null)
                    myMarker.remove();
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
                MarkerOptions options = new MarkerOptions().position(myLocation).icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow).title("我的位置");
                myMarker = (Marker) baiduMap.addOverlay(options);
                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(myLocation, 14));//设为中心且进行一定缩放
                //位置
                addressTv.setText(location.getAddrStr());
            }

            @Override
            public void onFailed() {
                addressTv.setText("定位失败");
            }

            @Override
            public void onFinally() {

            }
        });
    }

    private void setListener() {
        locateIv.setOnClickListener(this);
        //商家点击
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getExtraInfo() != null) {
                    Merchant merchant = marker.getExtraInfo().getParcelable(BUNDLE_KEY);
                    if (merchant != null) {//跳到商家详情页
                        Intent intent = new Intent(MapSearchActivity.this, MerchantDetailActivity.class);
                        intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, merchant);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_location_iv://定位
                locateMySelf();
                break;
        }
    }

}
