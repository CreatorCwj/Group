package com.fragment;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.dao.generate.City;
import com.fragment.base.BaseRouteFragment;
import com.util.AppSetting;

/**
 * Created by cwj on 16/4/19.
 * 交通工具线路
 */
public class TransitRouteFragment extends BaseRouteFragment {

    @Override
    protected void searchRoute(LatLng startLoc, LatLng endLoc) {
        City city = AppSetting.getCity();
        String cityName = (city == null) ? "" : city.getName();
        search.transitSearch(new TransitRoutePlanOption().from(PlanNode.withLocation(startLoc)).city(cityName).to(PlanNode.withLocation(endLoc)));
    }
}
