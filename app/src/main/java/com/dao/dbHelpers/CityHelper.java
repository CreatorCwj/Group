package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.City;
import com.dao.generate.CityDao;
import com.google.inject.Singleton;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by cwj on 16/2/16.
 * 提供需要的model的dao类,可以扩展想要的方法
 */
@Singleton
public class CityHelper extends BaseDBHelper<City, Long> {

    public static final int MAX_RECENTLY_USED_NUM = 5;

    public CityHelper() {
        super(getDaoSession().getCityDao(), City.class);
    }

    public List<City> getRecentlyUsed() {
        QueryBuilder<City> builder = dao.queryBuilder();
        builder.where(CityDao.Properties.LastUseTime.notEq(0));//最近使用时间不为0的
        builder.orderDesc(CityDao.Properties.LastUseTime);//时间从最近到之前
        return builder.list();
    }

    public void updateRecentlyUsed(City city) {
        updateData(city);//更新city
        //取出最近使用列表
        List<City> recentlyUsed = getRecentlyUsed();
        //超出个数的重置
        for (int i = MAX_RECENTLY_USED_NUM; i < recentlyUsed.size(); ++i) {
            City c = recentlyUsed.get(i);
            c.setLastUseTime(0L);
            updateData(c);
        }
    }

}
