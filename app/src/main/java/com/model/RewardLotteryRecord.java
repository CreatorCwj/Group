package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("RewardLotteryRecord")
public class RewardLotteryRecord extends BaseModel {

    public static final String CLASS_NAME = "RewardLotteryRecord";

    public static final String VOUCHER = "voucher";
    public static final String USER = "user";
    public static final String REWARD_LOTTERY = "rewardLottery";

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

    public void setRewardLottery(String rewardLotteryId) {
        try {
            put(REWARD_LOTTERY, AVObject.createWithoutData(RewardLottery.class, rewardLotteryId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setRewardLottery(RewardLottery rewardLottery) {
        put(REWARD_LOTTERY, rewardLottery);
    }

    public RewardLottery getRewardLottery() {
        try {
            return getAVObject(REWARD_LOTTERY, RewardLottery.class);
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
