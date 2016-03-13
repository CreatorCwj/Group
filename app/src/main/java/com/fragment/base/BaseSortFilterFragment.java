package com.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.base.BaseFilterRecyclerAdapter;
import com.dao.base.BaseSortFilterModelInter;
import com.group.R;
import com.widget.rlrView.view.LoadMoreRecyclerView;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by cwj on 16/2/29.
 * 筛选器fragment基类,继承后只需重写获取数据的方法即可
 */
public abstract class BaseSortFilterFragment<T extends BaseSortFilterModelInter> extends BaseFragment {

    @InjectView(R.id.overlay_view)
    private View overlay;

    @InjectView(R.id.rlrViews_container)
    private LinearLayout container;

    @InjectView(R.id.left_rlrView)
    private RLRView leftRlrView;

    @InjectView(R.id.right_rlrView)
    private RLRView rightRlrView;

    @InjectView(R.id.complete_layout)
    private RelativeLayout completeLayout;

    @InjectView(R.id.complete_tv)
    private TextView completeTv;

    private BaseFilterRecyclerAdapter<T> firstAdapter;
    private BaseFilterRecyclerAdapter<T> subAdapter;

    private TextView controlView;//控制其开关的view

    private OnSelectListener<T> selectListener;
    private OnMultiSelectListener<T> multiSelectListener;

    public interface OnSelectListener<T> {
        void onSelect(T obj);
    }

    public interface OnMultiSelectListener<T> {
        void onMultiSelect(List<T> obj);
    }

    public void setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
    }

    public void setOnMultiSelectListener(OnMultiSelectListener<T> multiSelectListener) {
        this.multiSelectListener = multiSelectListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_sort_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //第一次进入的时候就是第一次展示的时候,关联view要选中
        if (controlView != null)
            controlView.setSelected(true);
        setOverlay();//设置遮盖view点击事件
        initAdapter();//获取adapter
        setCheckable();//多选设置
        setRLRView();//设置rlrView数据
        updRLRView();//更新rlrView高度,个数
    }

    private void setCheckable() {
        if (isCheckable()) {
            completeLayout.setVisibility(View.VISIBLE);
            completeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideFragment();
                    //调用回调,返回数组
                    if (multiSelectListener != null && firstAdapter != null) {
                        List<T> selected = new ArrayList<>();
                        for (int i = 0; i < firstAdapter.getDataCount(); i++) {
                            if (firstAdapter.isSelected(i))
                                selected.add(firstAdapter.getDataItem(i));
                        }
                        multiSelectListener.onMultiSelect(selected);
                    }
                }
            });
        } else {
            completeLayout.setVisibility(View.GONE);
        }
    }

    private void initAdapter() {
        firstAdapter = getAdapter();
        subAdapter = getAdapter();
    }

    private void updRLRView() {
        //高度(两层的则用weight,单层的用wrap的高度),只设置一次,假定过滤筛选条件不会动态变
        LinearLayout.LayoutParams params;
        if (isTwoLayers()) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            params.weight = 4;
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getRLRViewHeight());
            params.weight = 0;
        }
        container.setLayoutParams(params);
        //个数
        if (!isTwoLayers()) {//只有一级
            rightRlrView.setVisibility(View.GONE);
        }
    }

    private int getRLRViewHeight() {
        if (firstAdapter.getDataCount() <= 0)
            return 0;
        return leftRlrView.measure(0) * firstAdapter.getDataCount();//假定每个item一样高
    }

    protected abstract BaseFilterRecyclerAdapter<T> getAdapter();//获取自定义adapter

    public abstract boolean isTwoLayers();//是否是两级筛选

    public abstract boolean isCheckable();//是否是多选

    @SuppressWarnings("unchecked")
    private void setRLRView() {
        leftRlrView.setAdapter(firstAdapter);
        rightRlrView.setAdapter(subAdapter);
        //一级品类
        List<T> firstFilters = getFirstFilters();
        if (firstFilters == null || firstFilters.size() < 1)//无数据不显示
            return;
        T t = (T) firstFilters.get(0).getAllFirstFilter();
        if (t != null)//全部选项
            firstFilters.add(0, t);
        leftRlrView.addData(firstFilters);
        leftRlrView.setOnItemClickListener(new LoadMoreRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //多选时可多次点击进行自己的逻辑
                if (isCheckable()) {
                    //改变自己的选中状态,不清除其他的选中状态
                    leftRlrView.setSelected(position, !firstAdapter.isSelected(position), false, false);
                    return;
                }
                //selected
                leftRlrView.setSelected(position, true, false, true);
                rightRlrView.clearData();
                //二级品类
                List<T> subFilters = getSubFilters(firstAdapter.getDataItem(position).getFilterId());
                //无二级品类
                if (subFilters == null || subFilters.size() < 1) {
                    hideFragment();
                    if (selectListener != null)
                        selectListener.onSelect(firstAdapter.getDataItem(position));
                    return;
                }
                //有二级品类
                T t = (T) subFilters.get(0).getAllSubFilter(firstAdapter.getDataItem(position).getFilterId());
                if (t != null)
                    subFilters.add(0, t);//全部选项
                rightRlrView.resetData(subFilters);
            }
        });
        rightRlrView.setOnItemClickListener(new LoadMoreRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                rightRlrView.setSelected(position, true, false, true);
                hideFragment();
                if (selectListener != null) {
                    T t = subAdapter.getDataItem(position);
                    if (t.isAllSubFilter()) {//二级全部要返回相应的一级对象
                        for (T first : firstAdapter.getData()) {
                            if (first.getFilterId() == t.getFilterParentId()) {
                                t = first;
                                break;
                            }
                        }
                    }
                    selectListener.onSelect(t);
                }
            }
        });
        //initFirst
        //一定要注意size可能为0
        if (!isCheckable())//多选时默认没有选中项
            initSelect(firstFilters.get(0));
    }

    /**
     * 复选时调用的初始化方法
     */
    @SuppressWarnings("unchecked")
    public void initSelect(List<T> filters) {
        if (firstAdapter.getDataCount() < 1)//无数据不显示
            return;
        //无数据默认不用处理,清除选中状态
        if (filters == null || filters.size() <= 0) {
            leftRlrView.clearSelected();
            return;
        }
        for (T filter : filters) {//选中每一个
            for (int i = 0; i < firstAdapter.getDataCount(); i++) {
                T target = firstAdapter.getDataItem(i);
                if (filter.equalFilter(target)) {
                    leftRlrView.setSelected(i, true, false, false);
                    break;
                }
            }
        }
    }

    /**
     * 单选时调用的初始化方法
     */
    @SuppressWarnings("unchecked")
    public void initSelect(T filter) {
        if (firstAdapter.getDataCount() < 1)//无数据不显示
            return;
        //null默认为全部
        if (filter == null) {
            leftRlrView.setSelected(0, true, true, true);
            rightRlrView.clearData();
            return;
        }
        if (filter.isAllSubFilter() || filter.isSubFilter()) {//有父品类
            //父品类
            for (int i = 0; i < firstAdapter.getDataCount(); i++) {
                T t = firstAdapter.getDataItem(i);
                if (t.getFilterId() == filter.getFilterParentId()) {//应该选中的父品类
                    leftRlrView.setSelected(i, true, true, true);
                    break;
                }
            }
            //子品类
            List<T> sub = getSubFilters(filter.getFilterParentId());
            if (sub == null || sub.size() < 1)
                return;
            T subAll = (T) sub.get(0).getAllSubFilter(filter.getFilterParentId());
            if (subAll != null)
                sub.add(0, subAll);//全部选项
            rightRlrView.resetData(sub);
            for (int i = 0; i < subAdapter.getDataCount(); i++) {
                T t = subAdapter.getDataItem(i);
                if (filter.equalFilter(t)) {//应该选中的
                    rightRlrView.setSelected(i, true, true, true);
                    break;
                }
            }
        } else {//本身是父品类,还应默认选中子品类第一个(有子品类的话)
            rightRlrView.clearData();
            for (int i = 0; i < firstAdapter.getDataCount(); i++) {
                T t = firstAdapter.getDataItem(i);
                if (filter.equalFilter(t)) {//应该选中的父品类
                    leftRlrView.setSelected(i, true, true, true);
                    break;
                }
            }
            //子品类
            List<T> sub = getSubFilters(filter.getFilterId());
            if (sub == null || sub.size() < 1)//无子品类不显示
                return;
            T subAll = (T) sub.get(0).getAllSubFilter(filter.getFilterId());
            if (subAll != null)
                sub.add(0, subAll);//全部选项
            rightRlrView.resetData(sub);
            rightRlrView.setSelected(0, true, true, true);
        }
    }

    protected abstract List<T> getSubFilters(int parentFilterId);

    protected abstract List<T> getFirstFilters();

    public void setControlView(TextView controlView) {
        this.controlView = controlView;
    }

    public TextView getControlView() {
        return controlView;
    }

    @Override
    public void hideFragment() {
        super.hideFragment();
        if (controlView != null)
            controlView.setSelected(false);
    }

    @Override
    public void showFragment() {
        super.showFragment();
        if (controlView != null)
            controlView.setSelected(true);
    }

    private void setOverlay() {
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFragment();
            }
        });
    }

    @Override
    public boolean onBackPress() {
        hideFragment();
        return true;
    }
}
