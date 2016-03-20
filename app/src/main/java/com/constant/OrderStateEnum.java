package com.constant;

/**
 * Created by cwj on 16/3/22.
 * 订单状态枚举
 */
public enum OrderStateEnum {

    WAIT_PAY(0, "待支付"),
    WAIT_USE(1, "待使用"),
    USED(2, "已使用"),
    OVERDUE(3, "已过期");

    private int id;
    private String state;

    OrderStateEnum(int id, String state) {
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
