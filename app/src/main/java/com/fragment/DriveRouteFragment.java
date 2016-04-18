package com.fragment;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.fragment.base.BaseRouteFragment;

/**
 * Created by cwj on 16/4/19.
 * 驾车线路
 */
public class DriveRouteFragment extends BaseRouteFragment {

    @Override
    protected void searchRoute(LatLng startLoc, LatLng endLoc) {
        search.drivingSearch(new DrivingRoutePlanOption().from(PlanNode.withLocation(startLoc)).to(PlanNode.withLocation(endLoc)));
    }
}
