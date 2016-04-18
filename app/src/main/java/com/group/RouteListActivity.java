package com.group;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.RouteFragmentPagerAdapter;
import com.baidu.mapapi.model.LatLng;
import com.constant.RouteTypeEnum;
import com.fragment.DriveRouteFragment;
import com.fragment.TransitRouteFragment;
import com.fragment.WalkRouteFragment;
import com.fragment.base.BaseRouteFragment;
import com.group.base.BaseFragmentActivity;
import com.model.Merchant;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_route_list)
public class RouteListActivity extends BaseFragmentActivity implements View.OnClickListener {

    public static final String MERCHANT_KEY = "merchant";
    public static final String MY_LOCATION_KEY = "myLocation";
    public static final String FIRST_TYPE_KEY = "firstType";

    @InjectView(R.id.route_list_my_location_tv)
    private TextView myLocationTv;

    @InjectView(R.id.route_list_merchant_location_tv)
    private TextView merchantLocationTv;

    @InjectView(R.id.route_list_reverse_layout)
    private RelativeLayout reverseLayout;

    @InjectView(R.id.route_list_tabLayout)
    private TabLayout tabLayout;

    @InjectView(R.id.route_list_viewPager)
    private ViewPager viewPager;

    private Merchant merchant;
    private LatLng merchantLocation;
    private LatLng myLocation;
    private RouteTypeEnum firstRouteType;

    private LatLng startLoc, endLoc;

    private RouteFragmentPagerAdapter pagerAdapter;
    private List<BaseRouteFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setListener();
        setInfo();
        initViewPager();
        setLocAndRefresh(startLoc, endLoc, false);
    }

    private void setLocAndRefresh(LatLng startLoc, LatLng endLoc, boolean refresh) {
        for (BaseRouteFragment routeFragment : fragments) {
            routeFragment.setLocAndRefresh(startLoc, endLoc, refresh);
        }
    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new TransitRouteFragment());
        fragments.add(new DriveRouteFragment());
        fragments.add(new WalkRouteFragment());
        pagerAdapter = new RouteFragmentPagerAdapter(this, tabLayout, viewPager, fragments, firstRouteType.ordinal());
    }

    private void setListener() {
        reverseLayout.setOnClickListener(this);
    }

    private void setInfo() {
        if (myLocation != null) {
            myLocationTv.setText("我的位置");
        }
        if (merchant != null) {
            merchantLocationTv.setText(merchant.getName());
        }
    }

    private void receiveIntent() {
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
        endLoc = merchantLocation = new LatLng(merchant.getLocation().getLatitude(), merchant.getLocation().getLongitude());
        startLoc = myLocation = getIntent().getParcelableExtra(MY_LOCATION_KEY);
        firstRouteType = (RouteTypeEnum) getIntent().getSerializableExtra(FIRST_TYPE_KEY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_list_reverse_layout://颠倒
                reverseLocation();
                break;
        }
    }

    private void reverseLocation() {
        //文字
        String myText = myLocationTv.getText().toString();
        myLocationTv.setText(merchantLocationTv.getText());
        merchantLocationTv.setText(myText);
        //exchange,refresh route
        LatLng tmp = startLoc;
        startLoc = endLoc;
        endLoc = tmp;
        setLocAndRefresh(startLoc, endLoc, true);
    }
}
