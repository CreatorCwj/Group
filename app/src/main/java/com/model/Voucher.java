package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.model.base.BaseModel;

import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
@SuppressWarnings("unchecked")
@AVClassName("Voucher")
public class Voucher extends BaseModel {

    public static final String CLASS_NAME = "Voucher";

    public static final String NAME = "name";
    public static final String SUGGEST = "suggest";
    public static final String DESCRIBE = "describe";
    public static final String SELL_NUM = "sellNum";
    public static final String LIMIT_NUM = "limitNum";
    public static final String IMAGES = "images";
    public static final String TAG = "tag";
    public static final String SPARE_NUM = "spareNum";

    public static final String POINT = "point";
    public static final String REMARK_NUM = "remarkNum";

    public static final String CURRENT_PRICE = "currentPrice";
    public static final String ORIGIN_PRICE = "originPrice";

    public static final String MERCHANT = "merchant";
    public static final String MOVIE_PLAY = "moviePlay";
    public static final String REWARD_POINT = "rewardPoint";
    public static final String REWARD_LOTTERY = "rewardLottery";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setSuggest(String suggest) {
        put(SUGGEST, suggest);
    }

    public String getSuggest() {
        return getString(SUGGEST);
    }

    public void setDescribe(String describe) {
        put(DESCRIBE, describe);
    }

    public String getDescribe() {
        return getString(DESCRIBE);
    }

    public void setSellNum(int sellNum) {
        put(SELL_NUM, sellNum);
    }

    public int getSellNum() {
        return getInt(SELL_NUM);
    }

    public void setSpareNum(int spareNum) {
        put(SPARE_NUM, spareNum);
    }

    public int getSpareNum() {
        return getInt(SPARE_NUM);
    }

    public void setLimitNum(int limitNum) {
        put(LIMIT_NUM, limitNum);
    }

    public int getLimitNum() {
        return getInt(LIMIT_NUM);
    }

    public void setOriginPrice(double originPrice) {
        put(ORIGIN_PRICE, originPrice);
    }

    public double getOriginPrice() {
        return getDouble(ORIGIN_PRICE);
    }

    public void setCurrentPrice(double currentPrice) {
        put(CURRENT_PRICE, currentPrice);
    }

    public double getCurrentPrice() {
        return getDouble(CURRENT_PRICE);
    }

    public void setImages(List<String> images) {
        put(IMAGES, images);
    }

    public List<String> getImages() {
        return getList(IMAGES);
    }

    public void setTag(int tag) {
        put(TAG, tag);
    }

    public int getTag() {
        return getInt(TAG);
    }

    public void setPoint(double point) {
        put(POINT, point);
    }

    public double getPoint() {
        return getDouble(POINT);
    }

    public void setRemarkNum(int remarkNum) {
        put(REMARK_NUM, remarkNum);
    }

    public int getRemarkNum() {
        return getInt(REMARK_NUM);
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

    public void setMoviePlay(String moviePlayId) {
        try {
            put(MOVIE_PLAY, AVObject.createWithoutData(MoviePlay.class, moviePlayId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setMoviePlay(MoviePlay moviePlay) {
        put(MOVIE_PLAY, moviePlay);
    }

    public MoviePlay getMoviePlay() {
        try {
            return getAVObject(MOVIE_PLAY, MoviePlay.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRewardPoint(String rewardPointId) {
        try {
            put(REWARD_POINT, AVObject.createWithoutData(RewardPoint.class, rewardPointId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setRewardPoint(RewardPoint rewardPoint) {
        put(REWARD_POINT, rewardPoint);
    }

    public RewardPoint getRewardPoint() {
        try {
            return getAVObject(REWARD_POINT, RewardPoint.class);
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
}
