package com.model;

import com.avos.avoscloud.AVClassName;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/3.
 */
@AVClassName("Area")
public class Area extends BaseModel {

    public static final String CLASS_NAME = "Area";

    public static final String NAME = "name";
    public static final String AREA_ID = "areaId";
    public static final String PARENT_ID = "parentId";
    public static final String CITY_ID = "cityId";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setAreaId(int id) {
        put(AREA_ID, id);
    }

    public int getAreaId() {
        return getInt(AREA_ID);
    }

    public int getParentId() {
        return getInt(PARENT_ID);
    }

    public void setParentId(int parentId) {
        put(PARENT_ID, parentId);
    }

    public void setCityId(int cityId) {
        put(CITY_ID, cityId);
    }

    public int getCityId() {
        return getInt(CITY_ID);
    }

}
