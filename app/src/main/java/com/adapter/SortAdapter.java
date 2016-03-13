package com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.adapter.base.BaseFilterRecyclerAdapter;
import com.constant.SortEnum;

/**
 * Created by cwj on 16/3/15.
 * 排序adapter
 */
public class SortAdapter extends BaseFilterRecyclerAdapter<SortEnum> {

    public SortAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setIcon(ImageView iconIv, SortEnum dataItem, boolean selected) {
        iconIv.setVisibility(View.GONE);//icon不可见
    }

    @Override
    protected boolean hasSubFilters(SortEnum dataItem) {
        return false;//没有二级
    }
}
