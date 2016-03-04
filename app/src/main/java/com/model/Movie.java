package com.model;

import com.avos.avoscloud.AVClassName;
import com.model.base.BaseModel;

import java.util.Date;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("Movie")
public class Movie extends BaseModel {

    public static final String CLASS_NAME = "Movie";

    public static final String NAME = "name";
    public static final String LIKE = "like";
    public static final String TIME = "time";
    public static final String SPECIAL_EFFECT = "specialEffect";
    public static final String TYPE = "type";
    public static final String WEB_URL = "webUrl";
    public static final String IMAGE = "image";
    public static final String RELEASE_TIME = "releaseTime";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setLike(int like) {
        put(LIKE, like);
    }

    public int getLike() {
        return getInt(LIKE);
    }

    public void setTime(int minute) {
        put(TIME, minute);
    }

    public int getTime() {
        return getInt(TIME);
    }

    public void setSpecialEffect(String specialEffect) {
        put(SPECIAL_EFFECT, specialEffect);
    }

    public String getSpecialEffect() {
        return getString(SPECIAL_EFFECT);
    }

    public void setType(String type) {
        put(TYPE, type);
    }

    public String getType() {
        return getString(TYPE);
    }

    public void setWebUrl(String webUrl) {
        put(WEB_URL, webUrl);
    }

    public String getWebUrl() {
        return getString(WEB_URL);
    }

    public void setImage(String imageUrl) {
        put(IMAGE, imageUrl);
    }

    public String getImage() {
        return getString(IMAGE);
    }

    public void setReleaseTime(Date releaseTime) {
        put(RELEASE_TIME, releaseTime);
    }

    public Date getReleaseTime() {
        return getDate(RELEASE_TIME);
    }

}
