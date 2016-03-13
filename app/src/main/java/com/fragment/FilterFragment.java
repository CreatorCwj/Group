package com.fragment;

import com.adapter.FilterAdapter;
import com.adapter.base.BaseFilterRecyclerAdapter;
import com.constant.TagEnum;
import com.fragment.base.CheckFilterFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cwj on 16/3/15.
 * 过滤器Fragment
 */
public class FilterFragment extends CheckFilterFragment<TagEnum> {

    @Override
    protected BaseFilterRecyclerAdapter<TagEnum> getAdapter() {
        return new FilterAdapter(getActivity());
    }

    @Override
    protected List<TagEnum> getSubFilters(int parentFilterId) {
        return null;
    }

    @Override
    protected List<TagEnum> getFirstFilters() {
        return Arrays.asList(TagEnum.values());//可以用Arrays.asList,因为不会增加别的元素
    }
}
