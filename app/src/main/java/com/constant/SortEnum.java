package com.constant;

import com.dao.base.BaseSortFilterModelInter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/15.
 * 排序项(智能排序(默认),好评优先,离我最近,人均最低)
 */
public enum SortEnum implements BaseSortFilterModelInter<SortEnum> {

    DEFAULT(ALL_FILTER_ID, "智能排序"),//默认的当成全部的
    POINT(100, "好评优先"),
    DISTANCE(101, "离我最近"),
    AVERAGE(102, "人均最低");

    private int sortId;//不要用ALL_FILTER_ID作为id
    private String sortName;

    SortEnum(int sortId, String sortName) {
        this.sortId = sortId;
        this.sortName = sortName;
    }

    public int getId() {
        return sortId;
    }

    public String getName() {
        return sortName;
    }

    /**
     * 不算默认的值集合
     *
     * @return
     */
    public static List<SortEnum> getValues() {
        List<SortEnum> list = new ArrayList<>();
        for (SortEnum sortEnum : SortEnum.values()) {
            if (sortEnum != DEFAULT)
                list.add(sortEnum);
        }
        return list;
    }

    @Override
    public int getFilterId() {
        return sortId;
    }

    @Override
    public int getFilterParentId() {
        return INVALID_PARENT_ID;//都没有父级
    }

    @Override
    public String getFilterName() {
        return sortName;
    }

    @Override
    public SortEnum getAllFirstFilter() {
        return DEFAULT;
    }

    @Override
    public SortEnum getAllSubFilter(int parentFilterId) {
        return null;//无
    }

    @Override
    public boolean equalFilter(SortEnum target) {
        return target != null && (target == this || (getFilterId() == target.getFilterId() && getFilterName().equals(target.getFilterName()) && getFilterParentId() == target.getFilterParentId()));
    }

    @Override
    public boolean isAllFirstFilter() {
        return getFilterId() == ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    @Override
    public boolean isAllSubFilter() {
        return false;
    }

    @Override
    public boolean isFirstFilter() {
        return getFilterId() != ALL_FILTER_ID && getFilterParentId() == INVALID_PARENT_ID;
    }

    @Override
    public boolean isSubFilter() {
        return false;
    }
}
