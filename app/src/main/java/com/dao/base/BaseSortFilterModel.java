package com.dao.base;

/**
 * Created by cwj on 16/2/29.
 * 筛选器基类
 */
public abstract class BaseSortFilterModel<T extends BaseSortFilterModel> implements BaseSortFilterModelInter<T> {

    @Override
    public boolean equalFilter(T target) {
        return target != null && (target == this || (getFilterId() == target.getFilterId() && getFilterName().equals(target.getFilterName()) && getFilterParentId() == target.getFilterParentId()));
    }

    @Override
    public boolean isAllFirstFilter() {
        return getFilterId() == ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    @Override
    public boolean isAllSubFilter() {
        return getFilterId() == ALL_FILTER_ID && getFilterParentId() != INVALID_PARENT_ID;
    }

    @Override
    public boolean isFirstFilter() {
        return getFilterId() != ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    @Override
    public boolean isSubFilter() {
        return getFilterId() != ALL_FILTER_ID && getFilterParentId() != INVALID_PARENT_ID;
    }

}
