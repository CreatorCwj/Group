package com.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.adapter.base.BaseFilterRecyclerAdapter;
import com.constant.CategoryEnum;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.generate.Category;
import com.google.inject.Inject;
import com.group.R;

/**
 * Created by cwj on 16/3/14.
 * 品类过滤器adapter
 */
public class CategoryFilterAdapter extends BaseFilterRecyclerAdapter<Category> {

    @Inject
    private CategoryHelper categoryHelper;

    public CategoryFilterAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setIcon(ImageView iconIv, Category dataItem, boolean selected) {
        //根据品类类别设置图标
        if (dataItem.isAllFirstFilter()) {//一级全部
            iconIv.setImageResource(R.drawable.all_icon);
        } else {//一级品类
            CategoryEnum categoryEnum = CategoryEnum.getEnumMap(dataItem);
            if (categoryEnum == null) {
                iconIv.setImageDrawable(null);
                return;
            }
            iconIv.setImageResource(categoryEnum.getSmallIconId());
        }
    }

    @Override
    protected boolean hasSubFilters(Category dataItem) {
        return categoryHelper.hasSubCategories(dataItem.getCategoryId());
    }
}
