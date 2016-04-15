package com.model;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
@SuppressWarnings("unchecked")
public class User extends AVUser {

    public static final String DISPLAY_NAME = "displayName";
    public static final String LIKES = "likes";
    public static final String IMAGE = "image";
    public static final String GROWTH_VALUE = "growthValue";
    public static final String POINT = "point";

    //做推送时候再说
    public static final String INSTALLATION_ID = "installationId";

    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }

    public String getDisplayName() {
        return getString(DISPLAY_NAME);
    }

    public void resetLikes(List<Integer> categoryIds) {
        put(LIKES, categoryIds);
    }

    public List<Integer> getLikes() {
        return getList(LIKES);
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

    public void setGrowthValue(int growthValue) {
        put(GROWTH_VALUE, growthValue);
    }

    public int getGrowthValue() {
        return getInt(GROWTH_VALUE);
    }

    public void setPoint(int point) {
        put(POINT, point);
    }

    public int getPoint() {
        return getInt(POINT);
    }
}
