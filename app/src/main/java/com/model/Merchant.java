package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.model.base.BaseModel;

import java.util.List;

/**
 * Created by cwj on 16/3/3.
 */
@SuppressWarnings("unchecked")
@AVClassName("Merchant")
public class Merchant extends BaseModel {

    public static final String CLASS_NAME = "Merchant";

    public static final String NAME = "name";
    public static final String AVERAGE = "average";
    public static final String LOCATION = "location";

    public static final String POINT = "point";
    public static final String REMARK_NUM = "remarkNum";

    public static final String TAGS = "tags";
    public static final String PHONES = "phones";
    public static final String IMAGES = "images";
    public static final String SERVICES = "services";

    public static final String CATEGORY = "category";
    public static final String SUB_CATEGORY = "subCategory";
    public static final String AREA = "area";
    public static final String SUB_AREA = "subArea";
    public static final String CITY_ID = "cityId";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setAverage(int average) {
        put(AVERAGE, average);
    }

    public int getAverage() {
        return getInt(AVERAGE);
    }

    public void setLocation(AVGeoPoint location) {
        put(LOCATION, location);
    }

    public AVGeoPoint getLocation() {
        return getAVGeoPoint(LOCATION);
    }

    public void setPoint(int point) {
        put(POINT, point);
    }

    public int getPoint() {
        return getInt(POINT);
    }

    public void setRemarkNum(int remarkNum) {
        put(REMARK_NUM, remarkNum);
    }

    public int getRemarkNum() {
        return getInt(REMARK_NUM);
    }

    public void setTags(List<Integer> tags) {
        put(TAGS, tags);
    }

    public List<Integer> getTags() {
        return getList(TAGS);
    }

    public void setPhones(List<String> phones) {
        put(PHONES, phones);
    }

    public List<String> getPhones() {
        return getList(PHONES);
    }

    public void setImages(List<String> images) {
        put(IMAGES, images);
    }

    public List<String> getImages() {
        return getList(IMAGES);
    }

    public void setServices(List<String> services) {
        put(SERVICES, services);
    }

    public List<String> getServices() {
        return getList(SERVICES);
    }

    public void setCategory(String categoryId) {
        try {
            put(CATEGORY, AVObject.createWithoutData(Category.class, categoryId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setCategory(Category category) {
        put(CATEGORY, category);
    }

    public Category getCategory() {
        try {
            return getAVObject(CATEGORY, Category.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSubCategory(String subCategoryId) {
        try {
            put(SUB_CATEGORY, AVObject.createWithoutData(Category.class, subCategoryId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setSubCategory(Category subCategory) {
        put(SUB_CATEGORY, subCategory);
    }

    public Category getSubCategory() {
        try {
            return getAVObject(SUB_CATEGORY, Category.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setArea(String areaId) {
        try {
            put(AREA, AVObject.createWithoutData(Area.class, areaId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setArea(Area area) {
        put(AREA, area);
    }

    public Area getArea() {
        try {
            return getAVObject(AREA, Area.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSubArea(String subAreaId) {
        try {
            put(SUB_AREA, AVObject.createWithoutData(Area.class, subAreaId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setSubArea(Area subArea) {
        put(SUB_AREA, subArea);
    }

    public Area getSubArea() {
        try {
            return getAVObject(SUB_AREA, Area.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCityId(int cityId) {
        put(CITY_ID, cityId);
    }

    public int getCityId() {
        return getInt(CITY_ID);
    }
}
