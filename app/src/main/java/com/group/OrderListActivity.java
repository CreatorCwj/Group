package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adapter.OrderAdapter;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.constant.OrderStateEnum;
import com.group.base.BaseRpcFunctionListActivity;
import com.model.Merchant;
import com.model.Order;
import com.model.RewardLotteryRecord;
import com.model.Voucher;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cwj on 16/4/7.
 * 订单列表页(指定状态)
 */
public class OrderListActivity extends BaseRpcFunctionListActivity<Order> {

    public static final String ORDER_STATE_KEY = "status";

    private OrderStateEnum state;

    private List<String> includes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        if (state != OrderStateEnum.OVERDUE)//过期列表没有右上角按钮
            setRight();//查看过期订单
    }

    private void setRight() {
        toolBar.setRightText("过期订单");
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderListActivity.this, OrderListActivity.class);
                intent.putExtra(ORDER_STATE_KEY, OrderStateEnum.OVERDUE);
                startActivity(intent);
            }
        });
    }

    private void receiveIntent() {
        state = (OrderStateEnum) getIntent().getSerializableExtra(ORDER_STATE_KEY);
        if (state == null)//默认待支付状态
            state = OrderStateEnum.WAIT_PAY;
        toolBar.setTitleText(state.getState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlrView.refresh();//实时刷新
    }

    @Override
    public void onRefresh() {
        rlrView.clearData();//先清空再刷新
        super.onRefresh();
    }

    @Override
    protected RecyclerViewAdapter<Order> getAdapter() {
        return new OrderAdapter(this);
    }

    @Override
    protected String getTitleText() {
        return "订单";
    }

    @Override
    protected String getRpcFunctionName() {
        if (AVUser.getCurrentUser() == null)
            return null;
        else return CloudFunction.GET_ORDERS;
    }

    @Override
    protected Map<String, Object> getRpcFunctionParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", state.getId());
        params.put("descending", Order.UPDATED_AT);
        params.put("includes", getIncludes());
        return params;
    }

    private List<String> getIncludes() {
        if (includes != null)
            return includes;
        includes = new ArrayList<>();
        includes.add(Order.USED_LOTTERY);
        includes.add(Order.USED_LOTTERY + "." + RewardLotteryRecord.REWARD_LOTTERY);
        includes.add(Order.VOUCHER);
        includes.add(Order.VOUCHER + "." + Voucher.REWARD_LOTTERY);
        includes.add(Order.VOUCHER + "." + Voucher.REWARD_POINT);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        return includes;
    }
}
