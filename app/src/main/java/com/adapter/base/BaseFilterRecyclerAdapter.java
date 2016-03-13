package com.adapter.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dao.base.BaseSortFilterModelInter;
import com.group.R;
import com.util.DrawableUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;


/**
 * Created by cwj on 15/12/11.
 * 筛选器的adapter基类
 */
@SuppressWarnings("unchecked")
public abstract class BaseFilterRecyclerAdapter<T extends BaseSortFilterModelInter> extends RecyclerViewAdapter<T> {

    protected Drawable tickDrawable;

    public BaseFilterRecyclerAdapter(Context context) {
        super(context);
        tickDrawable = context.getResources().getDrawable(R.drawable.tick);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder != null) {
            FilterViewHolder filterViewHolder = (FilterViewHolder) viewHolder;
            filterViewHolder.textView.setText(getDataItem(position).getFilterName());
            //设置背景(二级的不变选中的背景,按下时的变)
            if (getDataItem(position).isFirstFilter() || getDataItem(position).isAllFirstFilter()) {//一级
                Drawable drawable = DrawableUtils.getStateDrawable(new DrawableUtils.RectStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, Color.parseColor("#F2F2F2"))
                        , new DrawableUtils.RectStateDrawable(new int[]{DrawableUtils.STATE_SELECTED}, Color.parseColor("#F2F2F2"))
                        , new DrawableUtils.RectStateDrawable(new int[]{}, Color.TRANSPARENT));
                filterViewHolder.itemView.setBackground(drawable);
            } else {//二级
                Drawable drawable = DrawableUtils.getStateDrawable(new DrawableUtils.RectStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, Color.parseColor("#F2F2F2"))
                        , new DrawableUtils.RectStateDrawable(new int[]{}, Color.TRANSPARENT));
                filterViewHolder.itemView.setBackground(drawable);
            }
            //设置文字颜色
            ColorStateList colorStateList = DrawableUtils.getStateColor(new DrawableUtils.StateColor(new int[]{DrawableUtils.STATE_SELECTED}, context.getResources().getColor(R.color.colorPrimary))
                    , new DrawableUtils.StateColor(new int[]{}, context.getResources().getColor(R.color.gray_dark)));
            filterViewHolder.textView.setTextColor(colorStateList);
            //是否选中,设置后相应背景才能起效果
            filterViewHolder.itemView.setSelected(isSelected(position));
            filterViewHolder.textView.setSelected(isSelected(position));
            //custom
            //设置next图片显示与否(一级且有二级才显示)
            if (getDataItem(position).isFirstFilter() && hasSubFilters(getDataItem(position))) {
                filterViewHolder.nextIv.setVisibility(View.VISIBLE);
            } else {
                filterViewHolder.nextIv.setVisibility(View.INVISIBLE);
            }
            //设置icon(二级选中有对勾,一级用户自己定义)
            if (getDataItem(position).isFirstFilter() || getDataItem(position).isAllFirstFilter()) {
                setIcon(filterViewHolder.iconIv, getDataItem(position), isSelected(position));
            } else if (isSelected(position)) {//二级品类如果选中则前面有勾
                filterViewHolder.iconIv.setImageDrawable(tickDrawable);
                filterViewHolder.iconIv.setVisibility(View.VISIBLE);
            } else {//否则不可见
                filterViewHolder.iconIv.setImageDrawable(null);
                filterViewHolder.iconIv.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected abstract void setIcon(ImageView iconIv, T dataItem, boolean selected);//通过选中与否来改变icon显示

    protected abstract boolean hasSubFilters(T dataItem);//是否有二级

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new FilterViewHolder(layoutInflater.inflate(R.layout.filter_item, parent, false));
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView iconIv;
        ImageView nextIv;

        public FilterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.filter_name);
            iconIv = (ImageView) itemView.findViewById(R.id.filter_icon_iv);
            nextIv = (ImageView) itemView.findViewById(R.id.filter_next_iv);
        }
    }
}
