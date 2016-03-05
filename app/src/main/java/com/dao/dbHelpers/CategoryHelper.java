package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.Category;
import com.google.inject.Singleton;

/**
 * Created by cwj on 16/3/5.
 */
@Singleton
public class CategoryHelper extends BaseDBHelper<Category, Long> {

    public CategoryHelper() {
        super(getDaoSession().getCategoryDao(), Category.class);
    }
}
