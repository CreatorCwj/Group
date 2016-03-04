package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("Collection")
public class Collection extends BaseModel {

    public static final String CLASS_NAME = "Collection";

    public static final String MERCHANT = "merchant";
    public static final String USER = "user";

    public void setMerchant(String merchantId) {
        try {
            put(MERCHANT, AVObject.createWithoutData(Merchant.class, merchantId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setMerchant(Merchant merchant) {
        put(MERCHANT, merchant);
    }

    public Merchant getMerchant() {
        try {
            return getAVObject(MERCHANT, Merchant.class);
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
