package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.model.base.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
@SuppressWarnings("unchecked")
@AVClassName("Remark")
public class Remark extends BaseModel {

    public static final String CLASS_NAME = "Remark";

    public static final String CONTENT = "content";
    public static final String POINT = "point";
    public static final String IMAGES = "images";
    public static final String VOUCHER = "voucher";
    public static final String USER = "user";

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public void setPoint(int point) {
        put(POINT, point);
    }

    public int getPoint() {
        return getInt(POINT);
    }

    public void addImages(List<AVFile> images) {
        addAll(IMAGES, images);
    }

    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        List<AVFile> list = getList(IMAGES);
        if (list != null && list.size() > 0) {
            for (AVFile file : list) {
                images.add(file.getUrl());
            }
        }
        return images;
    }

    public void setVoucher(String voucherId) {
        try {
            put(VOUCHER, AVObject.createWithoutData(Voucher.class, voucherId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setVoucher(Voucher voucher) {
        put(VOUCHER, voucher);
    }

    public Voucher getVoucher() {
        try {
            return getAVObject(VOUCHER, Voucher.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUser(String userId) {
        try {
            put(USER, AVUser.createWithoutData(User.class, userId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        put(USER, user);
    }

    public User getUser() {
        return getAVUser(USER, User.class);
    }
}
