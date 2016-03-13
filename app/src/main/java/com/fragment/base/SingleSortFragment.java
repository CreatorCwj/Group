package com.fragment.base;

import com.dao.base.BaseSortFilterModelInter;

/**
 * Created by cwj on 16/3/14.
 * 单选排序fragment
 */
public abstract class SingleSortFragment<T extends BaseSortFilterModelInter> extends BaseSortFilterFragment<T> {

    @Override
    public boolean isTwoLayers() {
        return false;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }
}
