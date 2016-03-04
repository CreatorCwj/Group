package com.dao.base;

/**
 * Created by cwj on 16/2/29.
 * 筛选器基类
 */
public abstract class BaseFilterModel<T extends BaseFilterModel> {

    protected static int ALL_FILTER_ID = 0;//不应和数据库中已有的id重复
    protected static String ALL_FILTER_NAME = "全部";
    protected static int INVALID_PARENT_ID = -1;//应和数据库中一级品类的parentId相对应

    public abstract int getFilterId();

    public abstract int getFilterParentId();

    public abstract String getFilterName();

    public abstract T getAllFirstFilter();

    public abstract T getAllSubFilter(int parentFilterId);

    public boolean equalFilter(T target) {
        return target != null && (target == this || (getFilterId() == target.getFilterId() && getFilterName().equals(target.getFilterName()) && getFilterParentId() == target.getFilterParentId()));
    }

    public boolean isAllFirstFilter() {
        return getFilterId() == ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    public boolean isAllSubFilter() {
        return getFilterId() == ALL_FILTER_ID && getFilterParentId() != INVALID_PARENT_ID;
    }

    public boolean isFirstFilter() {
        return getFilterId() != ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    public boolean isSubFilter() {
        return getFilterId() != ALL_FILTER_ID && getFilterParentId() != INVALID_PARENT_ID;
    }

}
