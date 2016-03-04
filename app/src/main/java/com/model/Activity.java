package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("Activity")
public class Activity extends BaseModel {

    public static final String CLASS_NAME = "Activity";

    public static final String TITLE = "title";
    public static final String DESCRIBE = "describe";
    public static final String WEB_URL = "webUrl";
    public static final String IMAGE = "image";

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

    public void setWebUrl(String webUrl) {
        put(WEB_URL, webUrl);
    }

    public String getWebUrl() {
        return getString(WEB_URL);
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
