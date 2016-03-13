package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.group.R;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/12.
 * 类似listview的通过adapter加载view
 * type也要一样
 */
public class AdapterLinearLayout<T> extends LinearLayout {

    private RecyclerViewAdapter<T> adapter;

    private List<RecyclerView.ViewHolder> holders = new ArrayList<>();
    private List<View> dividers = new ArrayList<>();

    private int dividerHeight;
    private int dividerColor;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecyclerViewAdapter adapter, int pos);
    }

    public AdapterLinearLayout(Context context) {
        this(context, null);
    }

    public AdapterLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AdapterLinearLayout);
            dividerHeight = typedArray.getDimensionPixelSize(R.styleable.AdapterLinearLayout_itemDividerHeight, 0);
            dividerColor = typedArray.getColor(R.styleable.AdapterLinearLayout_itemDividerColor, Color.TRANSPARENT);
            typedArray.recycle();
        }
    }

    public void clearData() {
        if (adapter != null) {
            adapter.clearData();
            notifyRefresh();
        }
    }

    public void resetData(List<T> data) {
        if (adapter != null) {
            adapter.resetData(data);
            notifyRefresh();
        }
    }

    public void addData(List<T> data) {
        if (adapter != null) {
            adapter.addData(data);
            notifyRefresh();
        }
    }

    public void setAdapter(RecyclerViewAdapter<T> adapter) {
        this.adapter = adapter;
        notifyRefresh();
    }

    public void notifyRefresh() {
        if (adapter == null)
            return;
        //循环复用
        for (int i = 0; i < adapter.getDataCount(); i++) {
            RecyclerView.ViewHolder holder;
            View divider;
            if (i < holders.size()) {//有复用的直接拿到
                holder = holders.get(i);
                divider = dividers.get(i);
            } else {//新建holder和divider
                holder = adapter.onCreateHolder(this, adapter.getItemType(i));
                holders.add(holder);
                divider = getDivider();
                dividers.add(divider);
            }
            //如果没有加过就加(包含加过但是后来被移除的item),恢复监听器
            if (holder.itemView.getParent() == null) {
                setClickListener(holder.itemView, i);
                addView(holder.itemView);
            }
            if (divider.getParent() == null)
                addView(divider);
            //设置item
            adapter.onHolderBind(holder, i);
        }
        //多余的holder和divider移除,不从集合移除,稍后可以继续复用(监听器也移除)
        for (int i = adapter.getDataCount(); i < holders.size(); i++) {
            holders.get(i).itemView.setOnClickListener(null);
            removeView(holders.get(i).itemView);
            removeView(dividers.get(i));
        }
    }

    private void setClickListener(View itemView, final int i) {
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(adapter, i);
                }
            }
        });
    }

    private View getDivider() {
        View view = new View(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
        view.setBackgroundColor(dividerColor);
        return view;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
