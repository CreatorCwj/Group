package com.dao.dbHelpers;

import com.dao.base.BaseDBHelper;
import com.dao.generate.Category;
import com.dao.generate.CategoryDao;
import com.google.inject.Singleton;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by cwj on 16/3/5.
 */
@Singleton
public class CategoryHelper extends BaseDBHelper<Category, Long> {

    public CategoryHelper() {
        super(getDaoSession().getCategoryDao(), Category.class);
    }

    public Category findById(int categoryId) {
        QueryBuilder<Category> builder = dao.queryBuilder();
        builder.where(CategoryDao.Properties.CategoryId.eq(categoryId));
        List<Category> list = builder.list();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }
}
