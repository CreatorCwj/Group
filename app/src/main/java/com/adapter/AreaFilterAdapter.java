package com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.adapter.base.BaseFilterRecyclerAdapter;
import com.dao.dbHelpers.AreaHelper;
import com.dao.generate.Area;
import com.dao.generate.NearbyArea;
import com.google.inject.Inject;

/**
 * Created by cwj on 16/3/14.
 * 商圈筛选adapter
 */
public class AreaFilterAdapter extends BaseFilterRecyclerAdapter<Area> {

    @Inject
    private AreaHelper areaHelper;

    public AreaFilterAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setIcon(ImageView iconIv, Area dataItem, boolean selected) {
        iconIv.setVisibility(View.GONE);//商圈前面不显示icon
    }

    @Override
    protected boolean hasSubFilters(Area dataItem) {
        //附近的item有二级
        return dataItem.getFilterId() == NearbyArea.NEARBY_ID || areaHelper.hasSubAreas(dataItem.getAreaId());
    }
}
