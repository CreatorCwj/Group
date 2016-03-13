package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.Area;
import com.dao.generate.AreaDao;
import com.google.inject.Singleton;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by cwj on 16/3/5.
 */
@Singleton
public class AreaHelper extends BaseDBHelper<Area, Long> {

    public AreaHelper() {
        super(getDaoSession().getAreaDao(), Area.class);
    }

    /**
     * 查询该city的商圈是否有数据
     */
    public boolean isEmptyOfCity(int cityId) {
        QueryBuilder<Area> builder = dao.queryBuilder();
        builder.where(AreaDao.Properties.CityId.eq(cityId));
        return findByQuery(builder).size() == 0;
    }

    /**
     * 查询某个城市的一级商圈
     */
    public List<Area> findFirstAreasByCityId(int cityId) {
        QueryBuilder<Area> builder = dao.queryBuilder();
        builder.where(AreaDao.Properties.CityId.eq(cityId), AreaDao.Properties.ParentId.eq(-1));
        return findByQuery(builder);
    }

    /**
     * 查询某个商圈的子商圈
     */
    public List<Area> findAreasByParentId(int parentId) {
        QueryBuilder<Area> builder = dao.queryBuilder();
        builder.where(AreaDao.Properties.ParentId.eq(parentId));
        return findByQuery(builder);
    }

    /**
     * 查询某个商圈有无子商圈
     */
    public boolean hasSubAreas(int areaId) {
        return findAreasByParentId(areaId).size() > 0;
    }
}
