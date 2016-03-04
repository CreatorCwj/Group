package com.widget.rlrView.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import com.group.R;
import com.util.UIUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.other.Page;
import com.widget.rlrView.viewHolder.HeaderViewHolder;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by cwj on 16/1/16.
 * 可设置是否刷新、自动刷新与否以及外部手动刷新的刷新控件
 * 支持刷新不加载,加载时可刷新(重置)
 * 支持自定义选中item的方法
 * 支持置顶item的方法
 * 与{@link LoadMoreRecyclerView}联合使用
 */
public class RLRView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadListener {

    private boolean autoRefresh = true;
    private LoadMoreRecyclerView loadMoreRecyclerView;
    private Page page;
    private Drawable emptyDrawable;

    private OnRefreshListener onRefreshListener;
    private OnLoadListener onLoadListener;

    /**
     * 刷新请实现该接口
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 加载请实现该接口
     */
    public interface OnLoadListener {
        void onLoad();
    }

    public RLRView(Context context) {
        this(context, null);
    }

    public RLRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);//初始化布局属性
        initProps();//初始化一些样式
        initPage();//初始化page(把分页逻辑统一)
    }

    private void initPage() {
        page = new Page();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RLRView);
            //是否自动刷新
            autoRefresh = typedArray.getBoolean(R.styleable.RLRView_autoRefresh, true);
            //empty显示
            int id = typedArray.getResourceId(R.styleable.RLRView_emptyView, -1);
            setEmptyView(id);
            typedArray.recycle();
        }
        addChild(attrs);
    }

    private void addChild(AttributeSet attrs) {
        loadMoreRecyclerView = new LoadMoreRecyclerView(getContext(), attrs);
        loadMoreRecyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(loadMoreRecyclerView);
    }

    private void setEmptyView(int id) {
        if (id != -1) {
            try {
                emptyDrawable = getResources().getDrawable(id, null);
                return;
            } catch (Exception e) {
                Log.i("RLRView", "empty不是图片");
            }
            try {
                View view = LayoutInflater.from(getContext()).inflate(id, null);
                getDrawableFromView(view);
            } catch (Exception e) {
                Log.i("RLRView", "empty也不是view");
            }
        }
    }

    private void getDrawableFromView(final View view) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = getWidth();
                int height = getHeight();
                view.setDrawingCacheEnabled(true);
                view.destroyDrawingCache();
                view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                view.layout(0, 0, width, height);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
                emptyDrawable = new BitmapDrawable(getResources(), bitmap);
                RLRView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setDivider(int height, int color) {
        loadMoreRecyclerView.setDivider(height, color);
    }

    private void initProps() {
        this.setOnRefreshListener(this);
        loadMoreRecyclerView.setOnLoadListener(this);
        //加载圈颜色,可在外部自己设置
        this.setColorSchemeResources(
                android.R.color.holo_red_light, android.R.color.holo_blue_light,
                android.R.color.holo_orange_light);
        //一定要设置offset,否则调用setRefreshing(true)时没有效果
        this.setProgressViewOffset(false, -UIUtils.dp2px(getContext(), 24), UIUtils.dp2px(getContext(), 24));
        //onResume回调后view会回调此方法来监听layout的改变
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //可以允许刷新且自动刷新且没有在加载的时候调用刷新
                if (isEnabled() && isAutoRefresh() && !isLoading()) {
                    invokeRefresh();
                }
                //防止后续调用,调用一次后即可取消
                RLRView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 设置布局样式
     *
     * @param layoutType
     * @param columnCount
     */
    public void setLayoutType(int layoutType, int columnCount, int orientation) {
        loadMoreRecyclerView.setLayoutType(layoutType, columnCount, orientation);
    }

    //通过反射拿到listener
    private void invokeRefresh() {
        try {
            Field field = SwipeRefreshLayout.class.getDeclaredField("mListener");
            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj != null && obj instanceof SwipeRefreshLayout.OnRefreshListener) {
                setRefreshing(true);
                ((SwipeRefreshLayout.OnRefreshListener) obj).onRefresh();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置adapter,放入recyclerView里
     *
     * @param adapter
     */
    public void setAdapter(RecyclerViewAdapter adapter) {
        loadMoreRecyclerView.setAdapter(adapter);
    }

    /**
     * recyclerView是否在加载
     *
     * @return
     */
    public boolean isLoading() {
        return loadMoreRecyclerView.isLoading();
    }

    /**
     * 外部主动调用
     */
    public void refresh() {
        //刷新或加载时允许刷新,不允许刷新时不调用
        if (!isEnabled())
            return;
        invokeRefresh();
    }

    /**
     * 是否自动刷新
     *
     * @return
     */
    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    /**
     * 设置是否允许第一次自动刷新
     * 默认为允许自动刷新
     *
     * @param autoRefresh
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    /**
     * 设置自定义刷新监听器
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.onRefreshListener = listener;
    }

    /**
     * 设置自定义加载监听器
     *
     * @param onLoadListener
     */
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    /**
     * itemClick
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(LoadMoreRecyclerView.OnItemClickListener onItemClickListener) {
        loadMoreRecyclerView.setOnItemClickListener(onItemClickListener);
    }

    /**
     * itemLongClick
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(LoadMoreRecyclerView.OnItemLongClickListener onItemLongClickListener) {
        loadMoreRecyclerView.setOnItemLongClickListener(onItemLongClickListener);
    }

    /**
     * 自带listener只能是自己,需要传入自定义的listener来监听刷新
     *
     * @param listener
     */
    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        super.setOnRefreshListener(this);
    }

    @Override
    public void onLoad() {
        page.nextPage();//下一页
        if (onLoadListener != null)
            onLoadListener.onLoad();
    }

    @Override
    final public void onRefresh() {
        //swipe手势刷新时会调用此方法将refresh状态设置为true,如果此时在加载,不可以刷新,而且要讲刷新状态设置为false
        //为了统一写法加上isEnable判断,其实不可刷新时调不到这里
        if (!isEnabled()) {
            setRefreshing(false);
            return;
        }
        //允许加载时刷新(重置)
        //正在加载时取消加载(相关请求在触发刷新时自己调用:比如筛选排序等情况)
        if (isLoading())
            stopLoadMore();
        //重置页数
        page.reset();
        //否则可以调用刷新,记住要恢复加载状态
        resetCanLoadState();
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
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
        loadMoreRecyclerView.scrollTo(position);
    }

    /**
     * 刷新加载失败,页数恢复(如果是刷新失败的话则要清空(防止刷新前没清空导致刷新失败后还能直接加载))
     */
    public void rlError() {
        page.prePage();
        if (isRefreshing())
            clearData();
    }

    /**
     * 添加头部
     *
     * @param headerViewHolder
     */
    public <T extends HeaderViewHolder> void addHeader(T headerViewHolder) {
        loadMoreRecyclerView.addHeader(headerViewHolder);
    }

    /**
     * 得到头部view
     *
     * @return
     */
    public HeaderViewHolder getHeader() {
        return loadMoreRecyclerView.getHeader();
    }

    /**
     * 添加进数据
     *
     * @param dataList
     */
    @SuppressWarnings("unchecked")
    public void addData(List dataList) {
        judgeCanLoadMore(dataList);
        loadMoreRecyclerView.addData(dataList);
    }

    /**
     * 重置数据
     *
     * @param dataList
     */
    @SuppressWarnings("unchecked")
    public void resetData(List dataList) {
        judgeCanLoadMore(dataList);
        loadMoreRecyclerView.resetData(dataList);
    }

    /**
     * 清空数据
     */
    public void clearData() {
        loadMoreRecyclerView.clearData();
    }

    /**
     * 拦截一下添加数据,自动判断是否无法加载更多
     *
     * @param dataList
     */
    private void judgeCanLoadMore(List dataList) {
        if (dataList != null && dataList.size() < page.getPageSize()) {
            setCanLoadMore(false);
        }
    }

    /**
     * 重置加载状态,当初设置可以加载的话,刷新时候应该重置为可加载
     */
    private void resetCanLoadState() {
        Boolean b = loadMoreRecyclerView.getCanLoadMoreInit();
        if (b == null || b) {
            loadMoreRecyclerView.setCanLoadMore(true);
        } else {
            loadMoreRecyclerView.setCanLoadMore(false);
        }
    }

    /**
     * 设置是否可以加载更多
     *
     * @param canLoadMore
     */
    public void setCanLoadMore(boolean canLoadMore) {
        loadMoreRecyclerView.setCanLoadMore(canLoadMore);
    }

    private void stopRefresh() {
        setRefreshing(false);
        //根据数据数量看是否显示空数据界面(0数据时显示),header也隐藏
        //空数据界面可以传入也可以在layout指定
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) loadMoreRecyclerView.getAdapter();
        if (adapter != null && emptyDrawable != null) {
            if (adapter.getDataCount() == 0) {
                setBackground(emptyDrawable);
                if (loadMoreRecyclerView.getHeader() != null && loadMoreRecyclerView.getHeader().itemView.getVisibility() == VISIBLE)
                    loadMoreRecyclerView.getHeader().itemView.setVisibility(View.GONE);
            } else {
                setBackground(null);
                if (loadMoreRecyclerView.getHeader() != null && loadMoreRecyclerView.getHeader().itemView.getVisibility() == GONE)
                    loadMoreRecyclerView.getHeader().itemView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void stopLoadMore() {
        loadMoreRecyclerView.stopLoadMore();
    }

    /**
     * 结束刷新加载
     */
    public void stopRL() {
        stopRefresh();
        stopLoadMore();
    }

    /**
     * 选中某一项
     * 位置,是否选中,是否滚动到该项,是否清除其它选中项
     */
    public void setSelected(int position, boolean isSelected, boolean scrollTo, boolean clearOtherSelected) {
        loadMoreRecyclerView.setSelected(position, isSelected, scrollTo, clearOtherSelected);
    }

    /**
     * 清除选中项
     */
    public void clearSelected() {
        loadMoreRecyclerView.clearSelected();
    }

    /**
     * 获取recyclerView
     *
     * @return
     */
    public LoadMoreRecyclerView getLoadMoreView() {
        return loadMoreRecyclerView;
    }

    /**
     * 返回当前页数
     *
     * @return
     */
    public int getPageNo() {
        return page.getPageNo();
    }

    /**
     * 返回当前页数据多少
     *
     * @return
     */
    public int getPageSize() {
        return page.getPageSize();
    }

    /**
     * 返回从第几个开始
     *
     * @return
     */
    public int getSkipCount() {
        return page.getSkipCount();
    }
}
