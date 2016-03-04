package com.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
public class User extends AVUser {

    public static final String DISPLAY_NAME = "displayName";
    public static final String LIKES = "likes";
    public static final String IMAGE = "image";

    //做推送时候再说
    public static final String INSTALLATION_ID = "installationId";

    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }

    public String getDisplayName() {
        return getString(DISPLAY_NAME);
    }

    public void addLikesById(List<String> categoryIds) {
        List<Category> categories = new ArrayList<>();
        for (String id : categoryIds) {
            try {
                categories.add(AVObject.createWithoutData(Category.class, id));
            } catch (AVException e) {
                e.printStackTrace();
            }
        }
        addLikes(categories);
    }

    public void addLikes(List<Category> categories) {
        addAllUnique(LIKES, categories);
    }

    public List<Category> getLikes() {
        return getList(LIKES, Category.class);
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
