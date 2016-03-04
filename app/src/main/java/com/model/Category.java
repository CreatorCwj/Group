package com.model;

import com.avos.avoscloud.AVClassName;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/3.
 */
@AVClassName("Category")
public class Category extends BaseModel {

    public static final String CLASS_NAME = "Category";

    public static final String NAME = "name";
    public static final String CATEGORY_ID = "categoryId";
    public static final String PARENT_ID = "parentId";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setCategoryId(int id) {
        put(CATEGORY_ID, id);
    }

    public int getCategoryId() {
        return getInt(CATEGORY_ID);
    }

    public int getParentId() {
        return getInt(PARENT_ID);
    }

    public void setParentId(int parentId) {
        put(PARENT_ID, parentId);
    }

}
