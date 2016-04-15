package com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dao.generate.Category;
import com.group.R;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.viewPagers.imageViewPager.PointView;

/**
 * Created by cwj on 16/4/15.
 * 我的喜爱adapter,用本地缓存数据库的数据即可
 */
public class MyLikeAdapter extends RecyclerViewAdapter<Category> {

    private String[] colors = new String[]{"#7A85F6", "#FB934A", "#6BC2EF", "#F4BA54"};

    public MyLikeAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Category category = getDataItem(position);
            MyLikeViewHolder viewHolder = (MyLikeViewHolder) holder;
            //state
            viewHolder.pointView.setSelected(isSelected(position));
            //text
            viewHolder.pointView.setText(category.getName());
            //根据是否选中来改变text颜色
            if (isSelected(position)) {//选中
                viewHolder.pointView.setTextColor(Color.WHITE);
            } else {
                viewHolder.pointView.setTextColor(Color.parseColor(colors[position % colors.length]));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new MyLikeViewHolder(layoutInflater.inflate(R.layout.my_like_item, parent, false));
    }

    public class MyLikeViewHolder extends RecyclerView.ViewHolder {

        PointView pointView;

        public MyLikeViewHolder(View itemView) {
            super(itemView);
            pointView = (PointView) itemView.findViewById(R.id.my_like_item_pv);
        }
    }
}
