package com.fragment.base;

import com.dao.base.BaseSortFilterModelInter;

/**
 * Created by cwj on 16/3/14.
 * 两层的过滤器Fragment
 */
public abstract class TwoLayersFilterFragment<T extends BaseSortFilterModelInter> extends BaseSortFilterFragment<T> {

    @Override
    public boolean isTwoLayers() {
        return true;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

}
