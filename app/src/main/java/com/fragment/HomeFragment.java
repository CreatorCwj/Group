package com.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapter.ModelViewAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.baidu.location.BDLocation;
import com.dao.dbHelpers.AreaHelper;
import com.dao.dbHelpers.CityHelper;
import com.dao.generate.City;
import com.fragment.base.BaseFragment;
import com.google.inject.Inject;
import com.group.R;
import com.group.ScannerActivity;
import com.group.SelectCityActivity;
import com.leancloud.SafeFindCallback;
import com.location.Location;
import com.location.OnLocationListener;
import com.model.Area;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.CustomToolBar;
import com.widget.dialog.MessageDialog;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class HomeFragment extends BaseFragment {

    private final int SELECT_CITY = 1;

    @InjectView(R.id.home_custom_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.home_model_view)
    private RLRView modelView;

    @Inject
    private CityHelper cityHelper;

    @Inject
    private AreaHelper areaHelper;

    private City city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置位置及其它信息
        refresh();
        //定一次位,看当前城市和选择城市是否一致,不一致提示是否切换
        location();
        //设置Title
        setTitleClick();
        //设置Search
        setSearch();
        //设置扫一扫
        setScanner();
        //加载modelView
        setModelView();
    }

    private void setModelView() {
        modelView.setAdapter(new ModelViewAdapter(getActivity()));
        modelView.resetHeight();
    }

    private void location() {
        Location.requestLocation(getActivity(), new OnLocationListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onSuccess(BDLocation location) {
                if (!TextUtils.isEmpty(location.getCity())) {
                    final City newCity = cityHelper.getByName(Utils.getRealCityName(location.getCity()));
                    //不相同时提示切换
                    if (newCity != null && (city == null || newCity.getCityId() != city.getCityId())) {
                        String tip = "当前定位城市为:" + newCity.getName() + "\n是否切换?";
                        MessageDialog dialog = new MessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage(tip);
                        dialog.setPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //切换城市并刷新
                                AppSetting.updCity(newCity);//update
                                refresh();//更新位置及其它信息
                            }
                        });
                        dialog.show();
                    }
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onFinally() {

            }
        });
    }

    private void refresh() {
        //获取城市
        city = AppSetting.getCity();
        //title
        if (city != null)
            toolBar.setTitleText(city.getName());
        //根据城市获取商圈信息
        if (city != null) {
            loadArea();
        }
        //更新其它有关信息
    }

    private void loadArea() {
        if (!areaHelper.isEmptyOfCity(city.getCityId()))//该城市商圈没有被缓存
            return;
        AVQuery<Area> query = AVQuery.getQuery(Area.class);
        query.whereEqualTo(Area.CITY_ID, city.getCityId());
        query.setLimit(1000);
        query.findInBackground(new SafeFindCallback<Area>(getActivity()) {

            @Override
            public void findResult(List<Area> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.Area> areas = new ArrayList<>();
                    for (Area area : objects) {
                        areas.add(new com.dao.generate.Area(area.getAreaId(), area.getName(), area.getParentId(), area.getCityId()));
                    }
                    //放入数据库
                    areaHelper.insertData(areas);
                }
            }

        });
    }

    private void setScanner() {
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSearch() {
        toolBar.setSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(getActivity(), "搜索");
            }
        });
    }

    private void setTitleClick() {
        toolBar.setTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                startActivityForResult(intent, SELECT_CITY);//城市选择
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_CITY) {
                City city = (City) data.getSerializableExtra(SelectCityActivity.SELECT_CITY_KEY);
                AppSetting.updCity(city);//update
                refresh();//更新位置及其它信息
            }
        }
    }

}
