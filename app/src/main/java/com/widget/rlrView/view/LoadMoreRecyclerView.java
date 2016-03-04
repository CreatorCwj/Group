package com.widget.rlrView.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.group.R;
import com.imageLoader.ImageLoader;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.other.Divider;
import com.widget.rlrView.viewHolder.HeaderViewHolder;

import java.util.List;

/**
 * Created by cwj on 16/1/17.
 * 可上拉加载更多的RecyclerView
 * 支持刷新不加载,加载不刷新
 * 滑动时图片加载模式
 * 支持横向或垂直的布局
 * 支持自定义选中item的方法
 * 支持置顶item的方法
 * 与{@link RLRView}联合使用,不用单独使用这个view
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private Boolean canLoadMoreInit = null;
    private boolean canLoadMore = true;//默认可加载
    private boolean isLoading = false;

    private HeaderViewHolder headerViewHolder;//头部

    private OnLoadListener onLoadListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    /**
     * 线性
     */
    public static final int LINEAR = 0;

    /**
     * 网格
     */
    public static final int GRID = 1;

    /**
     * 瀑布流
     */
    public static final int WATER_FALL = 2;

    /**
     * 垂直
     */
    public static final int VERTICAL = 0;

    /**
     * 水平
     */
    public static final int HORIZONTAL = 1;

    private int layoutType = -1;
    private int orientation = -1;

    /**
     * 由RLRView控制的load回调
     */
    public interface OnLoadListener {
        void onLoad();
    }

    /**
     * itemClick
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * itemLongClick
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);//初始化布局属性
        initProps();//初始化其他属性
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RLRView);
            //布局样式
            int layoutType = typedArray.getInt(R.styleable.RLRView_layoutType, LINEAR);//默认线性
            int columnCount = typedArray.getInt(R.styleable.RLRView_columnCount, 2);//需要的话默认2列
            int orientation = typedArray.getInt(R.styleable.RLRView_orientation, VERTICAL);//默认为垂直
            setLayoutType(layoutType, columnCount, orientation);
            //加载状态
            boolean canLoadMoreTmp = typedArray.getBoolean(R.styleable.RLRView_canLoadMore, true);//默认可加载
            setCanLoadMore(canLoadMoreTmp);//改变可否加载状态
            //divider
            int height = typedArray.getDimensionPixelSize(R.styleable.RLRView_dividerHeight, 0);
            int color = typedArray.getColor(R.styleable.RLRView_dividerColor, Color.TRANSPARENT);
            setDivider(height, color);
            typedArray.recycle();
        }
    }

    /**
     * 设置divider
     */
    public void setDivider(int height, int color) {
        addItemDecoration(new Divider(height, color));
    }

    /**
     * 设置layoutManager和列数
     * 通过new创建时必须得设置
     *
     * @param layoutType  布局样式
     * @param columnCount 列数(线性布局样式时无效)
     */
    public void setLayoutType(int layoutType, int columnCount, int orientation) {
        if (this.layoutType != layoutType && this.orientation != orientation) {//和原来相同则不变
            this.layoutType = layoutType;
            this.orientation = orientation;
            switch (this.layoutType) {
                case LINEAR:
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    if (this.orientation == VERTICAL)
                        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
                    else if (this.orientation == HORIZONTAL)
                        linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
                    setLayoutManager(linearLayoutManager);
                    break;
                case GRID:
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columnCount);
                    if (this.orientation == VERTICAL)
                        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
                    else if (this.orientation == HORIZONTAL)
                        gridLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
                    setLayoutManager(gridLayoutManager);
                    break;
                case WATER_FALL:
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                    manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                    if (this.orientation == VERTICAL)
                        manager.setOrientation(OrientationHelper.VERTICAL);
                    else if (this.orientation == HORIZONTAL)
                        manager.setOrientation(OrientationHelper.HORIZONTAL);
                    setLayoutManager(manager);
                    break;
            }
        }
    }

    //初始化增加自定义的滑动监听
    private void initProps() {
        this.addOnScrollListener(onLoadScrollListener);
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void stopLoadMore() {
        this.isLoading = false;
        notifyFooterState();//加载状态改变会影响footer的显示
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public Boolean getCanLoadMoreInit() {
        return canLoadMoreInit;
    }

    /**
     * 置顶
     */
    public void backToTop() {
        scrollTo(0);
    }

    /**
     * 滚动到某项
     */
    public void scrollTo(int position) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);//置顶
        } else {
            scrollToPosition(position);
        }
    }

    /**
     * 是否可以加载更多
     * 默认为加载
     *
     * @param canLoadMore 是否可以加载
     */
    public void setCanLoadMore(boolean canLoadMore) {
        //第一次设置要记录是否可以加载的状态,一边后续恢复
        if (canLoadMoreInit == null)
            canLoadMoreInit = canLoadMore;
        this.canLoadMore = canLoadMore;
    }

    /**
     * 1.一定要是继承RecyclerViewAdapter的adapter
     * 2.view绑定到adapter上
     * 3.如果是grid的,那么footer要跨所有列
     */
    @Override
    public void setAdapter(final Adapter adapter) {
        if (adapter instanceof RecyclerViewAdapter) {
            //1
            ((RecyclerViewAdapter) adapter).attachLoadMoreView(this);
            //2
            if (getLayoutManager() instanceof GridLayoutManager) {
                final GridLayoutManager manager = (GridLayoutManager) getLayoutManager();
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (adapter.getItemViewType(position) == RecyclerViewAdapter.FOOTER)
                            return manager.getSpanCount();
                        return 1;
                    }
                });
            }
            super.setAdapter(adapter);
        }
    }

    public void performItemClick(int position) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(position);
    }

    /**
     * 外部(adapter)调用,返回true说明已消费,否则为未消费
     */
    public boolean performItemLongClick(int position) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(position);
            return true;
        }
        return false;
    }

    private OnScrollListener onLoadScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //空闲状态,可以加载更多,没有在加载和刷新的情况下调用加载
            if (newState == RecyclerView.SCROLL_STATE_IDLE && isCanLoadMore() && !isLoading() && !isRefreshing()) {
                handleLoadMore();
            }
            //根据滑动状态来暂停/开始图片加载
            switch (newState) {
                case RecyclerView.SCROLL_STATE_SETTLING://快速滑动时暂停
                    if (ImageLoader.isInitial())
                        ImageLoader.pause();
                    break;
                default:
                    if (ImageLoader.isInitial())
                        ImageLoader.resume();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    //父view是否在刷新(父view只能是RLRView)
    private boolean isRefreshing() {
        ViewParent parent = getParent();
        return parent instanceof RLRView && ((RLRView) parent).isRefreshing();
    }

    //最后一个view显示时加载
    private void handleLoadMore() {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            int count = adapter.getItemCount();
            if (count > 0 && adapter.getDataCount() > 0) {//没数据不让加载
                LayoutManager manager = getLayoutManager();
                View lastView = manager.findViewByPosition(count - 1);
                if (lastView != null && lastView.getTop() > 0) {
                    this.isLoading = true;
                    notifyFooterState();//加载状态改变会影响footer的显示
                    if (onLoadListener != null)
                        onLoadListener.onLoad();
                }
            }
        }
    }

    /**
     * 通知footer刷新,根据加载状态改变显示内容
     */
    private void notifyFooterState() {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            int count = adapter.getItemCount();
            if (count > 0) {//更新footer(仅更新一个item)
                adapter.notifyItemChanged(count - 1);
            }
        }
    }

    /**
     * 选中某一项
     * 位置,是否选中,是否滚动到该项,是否清除其它选中项
     */
    public void setSelected(int position, boolean isSelected, boolean scrollTo, boolean clearOtherSelected) {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            if (clearOtherSelected) {//清除其它选中项
                clearSelected();
            }
            adapter.setSelected(position, isSelected);//设置选中项
            if (scrollTo) {//置顶该项
                scrollTo(position);
            }
            adapter.notifyDataSetChanged();//更新
        }
    }

    /**
     * 清除选中项
     */
    public void clearSelected() {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            adapter.clearSelected();
        }
    }

    /**
     * 添加头部
     *
     * @param headerViewHolder
     */
    public <T extends HeaderViewHolder> void addHeader(T headerViewHolder) {
        this.headerViewHolder = headerViewHolder;
    }

    /**
     * 得到头部view
     *
     * @return
     */
    public HeaderViewHolder getHeader() {
        return headerViewHolder;
    }

    /**
     * 添加进数据
     *
     * @param dataList
     */
    @SuppressWarnings("unchecked")
    public void addData(List dataList) {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            adapter.addData(dataList);
        }
    }

    /**
     * 重置数据
     *
     * @param dataList
     */
    @SuppressWarnings("unchecked")
    public void resetData(List dataList) {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            adapter.resetData(dataList);
            adapter.clearSelected();
        }
    }

    /**
     * 清空数据
     */
    public void clearData() {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) getAdapter();
        if (adapter != null) {
            adapter.clearData();
            adapter.clearSelected();
        }
    }

}
