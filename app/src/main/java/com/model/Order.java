package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("Order")
public class Order extends BaseModel {

    public static final String CLASS_NAME = "Order";

    public static final String NUM = "num";
    public static final String TOTAL_PRICE = "totalPrice";
    public static final String PHONE = "phone";
    public static final String LIMIT_MINUTE = "limitMinute";
    public static final String STATUS = "status";
    public static final String USED_LOTTERY = "usedLottery";

    public static final String VOUCHER = "voucher";
    public static final String USER = "user";

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public String getPhone() {
        return getString(PHONE);
    }

    public void setNum(int num) {
        put(NUM, num);
    }

    public int getNum() {
        return getInt(NUM);
    }

    public void setLimitMinute(int limitMinute) {
        put(LIMIT_MINUTE, limitMinute);
    }

    public int getLimitMinute() {
        return getInt(LIMIT_MINUTE);
    }

    public void setTotalPrice(double totalPrice) {
        put(TOTAL_PRICE, totalPrice);
    }

    public double getTotalPrice() {
        return getDouble(TOTAL_PRICE);
    }

    public void setStatus(int status) {
        put(STATUS, status);
    }

    public int getStatus() {
        return getInt(STATUS);
    }

    public void setUsedLottery(String usedLotteryId) {
        try {
            put(USED_LOTTERY, AVObject.createWithoutData(RewardLotteryRecord.class, usedLotteryId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setUsedLottery(RewardLotteryRecord usedLottery) {
        put(USED_LOTTERY, usedLottery);
    }

    public RewardLotteryRecord getUsedLottery() {
        try {
            return getAVObject(USED_LOTTERY, RewardLotteryRecord.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
