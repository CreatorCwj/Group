package com.widget.rlrView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/1/16.
 * Adapter基类(数据层面)
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<T> dataList;

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        dataList = new ArrayList<>();
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

    /**
     * 数组总数(不算data之外的)
     *
     * @return
     */
    public int getDataCount() {
        return dataList.size();
    }

    /**
     * 得到单个数据
     *
     * @param position
     * @return
     */
    public T getDataItem(int position) {
        if (position >= 0 && position < dataList.size())
            return dataList.get(position);
        return null;
    }

}
