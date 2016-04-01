package com.constant;

/**
 * Created by cwj on 16/3/22.
 * 抵用券状态枚举(过期自己判断吧。。。)
 */
public enum LotteryStateEnum {

    WAIT_USE(0, "待使用"),
    USED(1, "已使用"),
    OVERDUE(2, "已过期");

    private int id;
    private String state;

    LotteryStateEnum(int id, String state) {
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }
}
