package com.fragment.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adapter.RouteLineAdapter;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.group.R;
import com.widget.rlrView.view.RLRView;

import roboguice.inject.InjectView;

public abstract class BaseRouteFragment extends BaseViewPagerFragment implements OnGetRoutePlanResultListener {

    @InjectView(R.id.route_fragment_rlrView)
    private RLRView rlrView;

    @InjectView(R.id.route_fragment_load_layout)
    private LinearLayout loadView;

    private RouteLineAdapter adapter;

    protected RoutePlanSearch search;

    private LatLng startLoc, endLoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRLRView();
        setSearch();
        refreshRoute();
    }

    private void setSearch() {
        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);
    }

    private void setRLRView() {
        adapter = new RouteLineAdapter(getActivity());
        rlrView.setAdapter(adapter);
        rlrView.setOnItemClickListener(adapter);
    }

    /**
     * 设置起点和终点坐标,决定是否刷新
     */
    public void setLocAndRefresh(LatLng startLoc, LatLng endLoc, boolean refresh) {
        this.startLoc = startLoc;
        this.endLoc = endLoc;
        if (refresh)
            refreshRoute();
    }

    //加载路线
    private void refreshRoute() {
        if (startLoc == null || endLoc == null)
            return;
        //开始查询路线
        updViewVisible(true);
        searchRoute(startLoc, endLoc);
    }

    protected abstract void searchRoute(LatLng startLoc, LatLng endLoc);

    private void updViewVisible(boolean isRefresh) {
        if (isRefresh) {
            rlrView.setVisibility(View.GONE);
            loadView.setVisibility(View.VISIBLE);
        } else {
            rlrView.setVisibility(View.VISIBLE);
            loadView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (getActivity().isFinishing())
            return;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || result.getRouteLines() == null) {
            rlrView.clearData();
        } else {
            rlrView.resetData(result.getRouteLines());
        }
        updViewVisible(false);
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (getActivity().isFinishing())
            return;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || result.getRouteLines() == null) {
            rlrView.clearData();
        } else {
            rlrView.resetData(result.getRouteLines());
        }
        updViewVisible(false);
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (getActivity().isFinishing())
            return;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || result.getRouteLines() == null) {
            rlrView.clearData();
        } else {
            rlrView.resetData(result.getRouteLines());
        }
        updViewVisible(false);
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        search.destroy();
    }

}
