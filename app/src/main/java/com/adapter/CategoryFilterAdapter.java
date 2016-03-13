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
            switch (categoryEnum) {
                case FOOD:
                    iconIv.setImageResource(R.drawable.food_small_icon);
                    break;
                case MOVIE:
                    iconIv.setImageResource(R.drawable.movie_small_icon);
                    break;
                case HOTEL:
                    iconIv.setImageResource(R.drawable.hotel_small_icon);
                    break;
                case PHOTO:
                    iconIv.setImageResource(R.drawable.photo_small_icon);
                    break;
                case ENTERTAINMENT:
                    iconIv.setImageResource(R.drawable.entertainment_small_icon);
                    break;
                case TRAVEL:
                    iconIv.setImageResource(R.drawable.travel_small_icon);
                    break;
                case CAR:
                    iconIv.setImageResource(R.drawable.car_small_icon);
                    break;
                case BEAUTY:
                    iconIv.setImageResource(R.drawable.beauty_small_icon);
                    break;
            }
        }
    }

    @Override
    protected boolean hasSubFilters(Category dataItem) {
        return categoryHelper.hasSubCategories(dataItem.getCategoryId());
    }
}
