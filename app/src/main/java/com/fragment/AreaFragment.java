package com.fragment;

import com.adapter.AreaFilterAdapter;
import com.adapter.base.BaseFilterRecyclerAdapter;
import com.dao.dbHelpers.AreaHelper;
import com.dao.generate.Area;
import com.dao.generate.City;
import com.dao.generate.NearbyArea;
import com.fragment.base.TwoLayersFilterFragment;
import com.google.inject.Inject;
import com.util.AppSetting;

import java.util.List;

/**
 * Created by cwj on 16/2/29.
 * 商圈过滤器,要手动加入附近的item
 */
public class AreaFragment extends TwoLayersFilterFragment<Area> {

    @Inject
    private AreaHelper areaHelper;

    @Override
    protected BaseFilterRecyclerAdapter<Area> getAdapter() {
        return new AreaFilterAdapter(getActivity());
    }

    @Override
    protected List<Area> getSubFilters(int parentFilterId) {
        if (parentFilterId == NearbyArea.NEARBY_ID)//附近的二级
            return NearbyArea.getDistanceItems();
        return areaHelper.findAreasByParentId(parentFilterId);
    }

    @Override
    protected List<Area> getFirstFilters() {
        City city = AppSetting.getCity();
        if (city == null)
            return null;
        List<Area> areas = areaHelper.findFirstAreasByCityId(city.getCityId());
        if (areas == null || areas.size() <= 0)//没数据则不添加附近选项
            return null;
        //添加附近的选项
        areas.add(0, NearbyArea.getNearbyItem());
        return areas;
    }
}
