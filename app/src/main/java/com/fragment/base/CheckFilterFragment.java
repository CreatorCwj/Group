package com.fragment.base;

import com.dao.base.BaseSortFilterModelInter;

/**
 * Created by cwj on 16/3/14.
 * 多选筛选fragment
 */
public abstract class CheckFilterFragment<T extends BaseSortFilterModelInter> extends BaseSortFilterFragment<T> {

    @Override
    public boolean isTwoLayers() {
        return false;
    }

    @Override
    public boolean isCheckable() {
        return true;
    }

}
