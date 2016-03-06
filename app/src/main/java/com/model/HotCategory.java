package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("HotCategory")
public class HotCategory extends BaseModel {

    public static final String CLASS_NAME = "HotCategory";

    public static final String TITLE = "title";
    public static final String DESCRIBE = "describe";
    public static final String IMAGE = "image";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CITY_ID = "cityId";

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setDescribe(String describe) {
        put(DESCRIBE, describe);
    }

    public String getDescribe() {
        return getString(DESCRIBE);
    }

    public void setCategoryId(int categoryId) {
        put(CATEGORY_ID, categoryId);
    }

    public int getCategoryId() {
        return getInt(CATEGORY_ID);
    }

    public void setCityId(int cityId) {
        put(CITY_ID, cityId);
    }

    public int getCityId() {
        return getInt(CITY_ID);
    }

    public void setImage(AVFile image) {
        put(IMAGE, image);
    }

    public AVFile getImage() {
        return getAVFile(IMAGE);
    }

    public String getImageUrl() {
        AVFile image = getAVFile(IMAGE);
        if (image != null)
            return image.getUrl();
        return null;
    }
}
