package com.dao.base;

/**
 * Created by cwj on 16/2/29.
 * 筛选器基类接口
 */
public interface BaseSortFilterModelInter<T extends BaseSortFilterModelInter> {

    int ALL_FILTER_ID = 0;//不应和数据库中已有的id重复
    String ALL_FILTER_NAME = "全部";
    int INVALID_PARENT_ID = -1;//应和数据库中一级品类的parentId相对应

    int getFilterId();

    int getFilterParentId();

    String getFilterName();

    T getAllFirstFilter();

    T getAllSubFilter(int parentFilterId);

    boolean equalFilter(T target);

    boolean isAllFirstFilter();

    boolean isAllSubFilter();

    boolean isFirstFilter();

    boolean isSubFilter();

}
