package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.constant.CategoryEnum;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.generate.Category;
import com.google.inject.Inject;
import com.group.R;
import com.util.Utils;
import com.widget.radio.RadioView;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.Arrays;

/**
 * Created by cwj on 16/3/9.
 */
public class ModelViewAdapter extends RecyclerViewAdapter<CategoryEnum> {

    @Inject
    private CategoryHelper categoryHelper;

    public ModelViewAdapter(Context context) {
        super(context);
        addData(Arrays.asList(CategoryEnum.values()));
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, final int position) {
        if (holder != null) {
            CategoryEnum categoryEnum = getDataItem(position);
            ModelViewItemHolder modelViewItemHolder = (ModelViewItemHolder) holder;
            modelViewItemHolder.radioView.setRadioText(categoryEnum.getName());
            setImage(modelViewItemHolder.radioView, categoryEnum);
            modelViewItemHolder.radioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category category = categoryHelper.findById(getDataItem(position).getId());
                    if (category != null)
                        Utils.showToast(context, category.getName());
                    //打开搜索界面,传入相应的品类,null就为全部也要传过去
                }
            });
        }
    }

    private void setImage(RadioView radioView, CategoryEnum categoryEnum) {
        switch (categoryEnum) {
            case FOOD:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.food_big_icon));
                break;
            case MOVIE:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.movie_big_icon));
                break;
            case HOTEL:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.hotel_big_icon));
                break;
            case TRAVEL:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.travel_big_icon));
                break;
            case ENTERTAINMENT:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.entertainment_big_icon));
                break;
            case BEAUTY:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.beauty_big_icon));
                break;
            case CAR:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.car_big_icon));
                break;
            case PHOTO:
                radioView.setImageBackground(context.getResources().getDrawable(R.drawable.photo_big_icon));
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ModelViewItemHolder(layoutInflater.inflate(R.layout.model_view_item, parent, false));
    }

    class ModelViewItemHolder extends RecyclerView.ViewHolder {

        RadioView radioView;

        public ModelViewItemHolder(View itemView) {
            super(itemView);
            radioView = (RadioView) itemView;
        }
    }
}
