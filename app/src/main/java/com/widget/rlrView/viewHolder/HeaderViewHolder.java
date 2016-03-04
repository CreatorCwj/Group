package com.widget.rlrView.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by cwj on 16/1/23.
 * Header的viewHolder基类
 * 将header作为一个headerView处理有利于分离headerView的处理
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    protected Context context;

    public HeaderViewHolder(Context context, int layoutId) {
        super(LayoutInflater.from(context).inflate(layoutId, null));
        this.context = context;
    }

    public HeaderViewHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }
}
