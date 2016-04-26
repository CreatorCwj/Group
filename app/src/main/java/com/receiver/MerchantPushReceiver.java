package com.receiver;

import android.content.Context;
import android.content.Intent;

import com.group.MerchantDetailActivity;

/**
 * Created by cwj on 16/4/26.
 * 商家活动推荐
 */
public class MerchantPushReceiver extends BasePushReceiver {

    @Override
    protected Intent getIntent(Context context, Intent intent) {
        String merchantId = getInfo(intent, "merchantId");
        Intent intent1 = new Intent(context, MerchantDetailActivity.class);
        intent1.putExtra(MerchantDetailActivity.MERCHANT_ID_KEY, merchantId);
        return intent1;
    }
}
