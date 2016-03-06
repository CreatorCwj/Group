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

    /**
     * 获取最近使用的城市
     */
    public List<City> getRecentlyUsed() {
        QueryBuilder<City> builder = dao.queryBuilder();
        builder.where(CityDao.Properties.LastUseTime.notEq(0));//最近使用时间不为0的
        builder.orderDesc(CityDao.Properties.LastUseTime);//时间从最近到之前
        return findByQuery(builder);
    }

    /**
     * 更新最近使用的城市
     *
     * @return
     */
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

    /**
     * 根据名字和拼音来搜索城市
     *
     * @return
     */
    public List<City> searchByName(String text) {
        QueryBuilder<City> builder = dao.queryBuilder();
        builder.whereOr(CityDao.Properties.Name.like(text + "%"),
                CityDao.Properties.Pinyin.like(text + "%"));
        return findByQuery(builder);
    }

    /**
     * 通过名字找城市,理论上名字是不重复的
     */
    public City getByName(String name) {
        QueryBuilder<City> builder = dao.queryBuilder();
        builder.where(CityDao.Properties.Name.eq(name));
        List<City> cities = findByQuery(builder);
        if (cities != null && cities.size() > 0)
            return cities.get(0);
        return null;
    }

}
