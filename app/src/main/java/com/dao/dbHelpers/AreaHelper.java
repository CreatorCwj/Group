package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.Area;
import com.google.inject.Singleton;

/**
 * Created by cwj on 16/3/5.
 */
@Singleton
public class AreaHelper extends BaseDBHelper<Area, Long> {

    public AreaHelper() {
        super(getDaoSession().getAreaDao(), Area.class);
    }
}
