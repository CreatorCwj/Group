package com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.adapter.base.BaseFilterRecyclerAdapter;
import com.constant.TagEnum;

/**
 * Created by cwj on 16/3/15.
 * 过滤器adapter
 */
public class FilterAdapter extends BaseFilterRecyclerAdapter<TagEnum> {

    public FilterAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setIcon(ImageView iconIv, TagEnum dataItem, boolean selected) {
        //选中的左边显示对勾,未选中的不显示
        if (selected) {
            iconIv.setImageDrawable(tickDrawable);
            iconIv.setVisibility(View.VISIBLE);
        } else {
            iconIv.setImageDrawable(null);
            iconIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected boolean hasSubFilters(TagEnum dataItem) {
        return false;
    }
}
