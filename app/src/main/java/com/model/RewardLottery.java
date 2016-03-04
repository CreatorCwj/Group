package com.model;

import com.avos.avoscloud.AVClassName;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("RewardLottery")
public class RewardLottery extends BaseModel {

    public static final String CLASS_NAME = "RewardLottery";

    public static final String PRICE = "price";
    public static final String PRICE_TO_USE = "priceToUse";
    public static final String LIMIT_DAYS = "limitDays";

    public void setPrice(int price) {
        put(PRICE, price);
    }

    public int getPrice() {
        return getInt(PRICE);
    }

    public void setPriceToUse(int priceToUse) {
        put(PRICE_TO_USE, priceToUse);
    }

    public int getPriceToUse() {
        return getInt(PRICE_TO_USE);
    }

    public void setLimitDays(int limitDays) {
        put(LIMIT_DAYS, limitDays);
    }

    public int getLimitDays() {
        return getInt(LIMIT_DAYS);
    }
}
