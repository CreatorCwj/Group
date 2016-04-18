package com.constant;

import com.group.R;

/**
 * Created by cwj on 16/4/19.
 * 交通方式枚举
 */
public enum RouteTypeEnum {

    TRANSIT(R.drawable.bus_icon, "公交"),
    DRIVE(R.drawable.drive_icon, "驾车"),
    WALK(R.drawable.walk_icon, "步行");

    private int resId;
    private String name;

    RouteTypeEnum(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }

    public static RouteTypeEnum getTypeByName(String name) {
        for (RouteTypeEnum typeEnum : RouteTypeEnum.values()) {
            if (typeEnum.getName().equals(name))
                return typeEnum;
        }
        return null;
    }
}
