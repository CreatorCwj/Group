package com.group;

import com.adapter.CouponAdapter;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.constant.OrderStateEnum;
import com.group.base.BaseAVQueryListActivity;
import com.model.Order;
import com.model.Voucher;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/14.
 * 我的团购券列表界面
 */
public class MyCouponActivity extends BaseAVQueryListActivity<Order> {

    @Override
    protected RecyclerViewAdapter<Order> getAdapter() {
        return new CouponAdapter(this);
    }

    @Override
    protected String getTitleText() {
        return "团购券";
    }

    @Override
    protected AVQuery<Order> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        AVQuery<Order> query = AVQuery.getQuery(Order.class);
        query.whereEqualTo(Order.USER, AVUser.getCurrentUser());
        query.whereEqualTo(Order.STATUS, OrderStateEnum.WAIT_USE.getId());//待使用的订单
        query.orderByDescending(Order.UPDATED_AT);
        query.include(Order.VOUCHER);
        query.include(Order.VOUCHER + "." + Voucher.MERCHANT);
        return query;
    }
}
