package com.model;

import com.avos.avoscloud.AVClassName;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("RewardPoint")
public class RewardPoint extends BaseModel {

    public static final String CLASS_NAME = "RewardPoint";

    public static final String POINT = "point";

    public void setPoint(int point) {
        put(POINT, point);
    }

    public int getPoint() {
        return getInt(POINT);
    }
}
