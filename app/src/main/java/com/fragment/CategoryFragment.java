package com.fragment;

import com.adapter.CategoryFilterAdapter;
import com.adapter.base.BaseFilterRecyclerAdapter;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.generate.Category;
import com.fragment.base.TwoLayersFilterFragment;
import com.google.inject.Inject;

import java.util.List;

/**
 * Created by cwj on 16/2/29.
 * 品类过滤器
 */
public class CategoryFragment extends TwoLayersFilterFragment<Category> {

    @Inject
    private CategoryHelper categoryHelper;

    @Override
    protected BaseFilterRecyclerAdapter<Category> getAdapter() {
        return new CategoryFilterAdapter(getActivity());
    }

    @Override
    protected List<Category> getSubFilters(int parentFilterId) {
        return categoryHelper.findByParentId(parentFilterId);
    }

    @Override
    protected List<Category> getFirstFilters() {
        return categoryHelper.findAllFirstCategories();
    }

}
