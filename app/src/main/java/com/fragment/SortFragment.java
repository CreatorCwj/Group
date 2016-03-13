package com.fragment;

import com.adapter.SortAdapter;
import com.adapter.base.BaseFilterRecyclerAdapter;
import com.constant.SortEnum;
import com.fragment.base.SingleSortFragment;

import java.util.List;

/**
 * Created by cwj on 16/3/15.
 * 选择排序fragment
 */
public class SortFragment extends SingleSortFragment<SortEnum> {

    @Override
    protected BaseFilterRecyclerAdapter<SortEnum> getAdapter() {
        return new SortAdapter(getActivity());
    }

    @Override
    protected List<SortEnum> getSubFilters(int parentFilterId) {
        return null;//没有二级
    }

    @Override
    protected List<SortEnum> getFirstFilters() {
        return SortEnum.getValues();
    }
}
