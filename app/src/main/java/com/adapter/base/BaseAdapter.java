package com.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;

/**
 * Created by cwj on 16/3/7.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<T> dataList;

    public BaseAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        dataList = new ArrayList<>();
        RoboGuice.getInjector(context).injectMembersWithoutViews(this);
    }

    /**
     * 增加数据
     *
     * @param data
     */
    public void addData(List<T> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 覆盖数据
     *
     * @param data
     */
    public void resetData(List<T> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 得到数据
     *
     * @return
     */
    public List<T> getData() {
        return dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }
}
