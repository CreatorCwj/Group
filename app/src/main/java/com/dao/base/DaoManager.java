package com.dao.base;

import android.content.Context;

import com.dao.generate.DaoMaster;
import com.dao.generate.DaoSession;

/**
 * Created by cwj on 16/2/16.
 * Dao管理器,获取单例session
 */
public class DaoManager {

    private static final String DB_NAME = "group_db";

    private static DaoSession daoSession;

    /**
     * 一定要初始化
     */
    public static void initGreenDao(Context context) {
        if (daoSession == null) {
            synchronized (DaoManager.class) {
                if (daoSession == null) {
                    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context.getApplicationContext(), DB_NAME, null);
                    daoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
                }
            }
        }
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
