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

    /**
     * 通过id找品类
     */
    public Category findById(int categoryId) {
        QueryBuilder<Category> builder = dao.queryBuilder();
        builder.where(CategoryDao.Properties.CategoryId.eq(categoryId));
        List<Category> list = findByQuery(builder);
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    /**
     * 通过id找子品类
     */
    public List<Category> findByParentId(int parentId) {
        QueryBuilder<Category> builder = dao.queryBuilder();
        builder.where(CategoryDao.Properties.ParentId.eq(parentId));
        return findByQuery(builder);
    }

    /**
     * 查找是否有子品类
     */
    public boolean hasSubCategories(int categoryId) {
        return findByParentId(categoryId).size() > 0;
    }

    /**
     * 找到所有一级品类
     */
    public List<Category> findAllFirstCategories() {
        QueryBuilder<Category> builder = dao.queryBuilder();
        builder.where(CategoryDao.Properties.ParentId.eq(-1));
        return findByQuery(builder);
    }
}
