package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("CinemaHall")
public class CinemaHall extends BaseModel {

    public static final String CLASS_NAME = "CinemaHall";

    public static final String SEATS = "seats";
    public static final String NUM = "num";
    public static final String MERCHANT = "merchant";

    public void setSeats(String seats) {
        put(SEATS, seats);
    }

    public String getSeats() {
        return getString(SEATS);
    }

    public void setNum(int num) {
        put(NUM, num);
    }

    public int getNum() {
        return getInt(NUM);
    }

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

}
