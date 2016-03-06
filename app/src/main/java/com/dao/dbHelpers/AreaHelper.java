package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.Area;
import com.dao.generate.AreaDao;
import com.google.inject.Singleton;

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
        return builder.list().size() == 0;
    }
}
