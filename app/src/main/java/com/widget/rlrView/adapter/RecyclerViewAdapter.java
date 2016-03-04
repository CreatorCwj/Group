package com.widget.rlrView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.group.R;
import com.widget.rlrView.view.LoadMoreRecyclerView;
import com.widget.rlrView.viewHolder.LoadViewHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cwj on 16/1/17.
 * 刷新加载界面用的adapter基类
 * RecyclerView使用的adapter的基类,可以添加FOOTER
 * 自己处理基本的逻辑,暴露出自定义的接口来实现adapter
 * 提供选中方法
 */
public abstract class RecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> {

    private LoadMoreRecyclerView loadMoreView;

    private Map<Integer, Boolean> selected = new HashMap<>();

    public static final int FOOTER = Integer.MIN_VALUE;
    public static final int HEADER = Integer.MAX_VALUE;

    public RecyclerViewAdapter(Context context) {
        super(context);
    }

    /**
     * 依赖的LoadMoreRecyclerView
     *
     * @param loadMoreView
     */
    public void attachLoadMoreView(LoadMoreRecyclerView loadMoreView) {
        this.loadMoreView = loadMoreView;
    }

    /**
     * viewholder已绑定
     * 确保传下去的position是数据里对应的position
     *
     * @param holder
     * @param position
     */
    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        //算出数据中的实际位置
        final int realDataPosition = hasHeader() ? position - 1 : position;
        if (type != FOOTER && type != HEADER) {//normal view
            //点击事件
            View v = holder.itemView;
            if (v != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loadMoreView != null) {
                            loadMoreView.performItemClick(realDataPosition);
                        }
                    }
                });
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (loadMoreView != null) {
                            return loadMoreView.performItemLongClick(realDataPosition);
                        }
                        return false;
                    }
                });
            }
            //自己的逻辑
            onHolderBind(holder, realDataPosition);
        } else if (type == FOOTER) {//footer
            //如果可以加载且有数据则可见,否则不可见
            if (hasFooter() && getDataCount() > 0) {
                holder.itemView.setVisibility(View.VISIBLE);
                //加载中则显示圈,否则显示文字
                if (holder instanceof LoadViewHolder) {
                    if (loadMoreView.isLoading()) {
                        ((LoadViewHolder) holder).setLoadState(true);
                    } else {
                        ((LoadViewHolder) holder).setLoadState(false);
                    }
                }
            } else {
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 处理后的viewholder已绑定
     * 确保position是数据里对应的position
     *
     * @param holder
     * @param position
     */
    abstract public void onHolderBind(RecyclerView.ViewHolder holder, int position);

    /**
     * 得到item总数(可能有header和footer)
     *
     * @return
     */
    @Override
    final public int getItemCount() {
        int extra = 0;
        if (hasHeader())
            ++extra;
        if (hasFooter())
            ++extra;
        return dataList.size() + extra;
    }

    /**
     * item类型
     *
     * @param position
     * @return
     */
    @Override
    final public int getItemViewType(int position) {

        if (hasHeader() && position == 0) {//有header且是第一个
            return HEADER;
        } else if (hasFooter() && ((!hasHeader() && position == dataList.size())
                || (hasHeader() && position == dataList.size() + 1))) {//有footer且可以位置正确(有无header)
            return FOOTER;
        } else {//normal
            return getItemType(position);
        }
    }

    //是否有header
    private boolean hasHeader() {
        return loadMoreView != null && loadMoreView.getHeader() != null;
    }

    //是否有footer
    private boolean hasFooter() {
        return loadMoreView != null && loadMoreView.isCanLoadMore();
    }

    /**
     * 处理后的item类型
     *
     * @param position
     * @return
     */
    public int getItemType(int position) {
        return 0;
    }

    /**
     * 根据类型创建viewholder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER) {
            return new LoadViewHolder(layoutInflater.inflate(R.layout.load_view, parent, false));
        }
        if (viewType == HEADER) {
            return loadMoreView.getHeader();
        }
        return onCreateHolder(parent, viewType);
    }

    /**
     * 根据类型创建处理后的viewholder
     *
     * @param parent
     * @param viewType
     * @return
     */
    abstract public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);

    public boolean isSelected(int position) {
        Boolean b = selected.get(position);
        return b == null ? false : b;
    }

    public void setSelected(int position, boolean isSelected) {
        selected.put(position, isSelected);
    }

    public void clearSelected() {
        selected.clear();
    }
}
