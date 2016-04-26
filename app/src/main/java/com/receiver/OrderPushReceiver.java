package com.receiver;

import android.content.Context;
import android.content.Intent;

import com.group.OrderDetailActivity;

/**
 * Created by cwj on 16/4/27.
 * 订单状态改变推送接收器
 */
public class OrderPushReceiver extends BasePushReceiver {

    @Override
    protected Intent getIntent(Context context, Intent intent) {
        String orderId = getInfo(intent, "orderId");
        Intent intent1 = new Intent(context, OrderDetailActivity.class);
        intent1.putExtra(OrderDetailActivity.ORDER_ID_KEY, orderId);
        return intent1;
    }
}
