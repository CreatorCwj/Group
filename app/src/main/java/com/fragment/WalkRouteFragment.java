package com.fragment;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.fragment.base.BaseRouteFragment;

/**
 * Created by cwj on 16/4/19.
 * 步行线路
 */
public class WalkRouteFragment extends BaseRouteFragment {

    @Override
    protected void searchRoute(LatLng startLoc, LatLng endLoc) {
        search.walkingSearch(new WalkingRoutePlanOption().from(PlanNode.withLocation(startLoc)).to(PlanNode.withLocation(endLoc)));
    }
}
