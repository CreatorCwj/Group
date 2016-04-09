package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseLotteryListActivity;
import com.model.Merchant;
import com.model.RewardLottery;
import com.model.RewardLotteryRecord;
import com.model.Voucher;

/**
 * Created by cwj on 16/4/6.
 * 抵用券列表(可选择和取消选择)
 */
public class SelectLotteryActivity extends BaseLotteryListActivity {

    public static final String SELECT_LOTTERY_KEY = "selectedLottery";
    public static final String SUB_TOTAL_PRICE_KEY = "subTotalPrice";

    private double subTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subTotalPrice = getIntent().getDoubleExtra(SUB_TOTAL_PRICE_KEY, 0);
    }

    @Override
    protected void onClickRightText(View v) {
        Intent intent = new Intent();
        intent.putExtra(SELECT_LOTTERY_KEY, (RewardLotteryRecord) null);//没选择,为null
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected String getRightText() {
        return "取消选择";
    }

    @Override
    public void onItemClick(int position) {
        super.onItemClick(position);
        //回传
        Intent intent = new Intent();
        intent.putExtra(SELECT_LOTTERY_KEY, adapter.getDataItem(position));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected AVQuery<RewardLotteryRecord> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        //查询满足最低消费的抵用券
        AVQuery<RewardLotteryRecord> query = AVQuery.getQuery(RewardLotteryRecord.class);
        query.whereEqualTo(RewardLotteryRecord.USER, AVUser.getCurrentUser());
        query.whereEqualTo(RewardLotteryRecord.STATUS, state.getId());//传入的抵用券状态
        query.whereMatchesQuery(RewardLotteryRecord.REWARD_LOTTERY, getConditionQuery());//满足最低消费
        query.orderByDescending(RewardLotteryRecord.UPDATED_AT);//获得时间排序
        query.include(RewardLotteryRecord.REWARD_LOTTERY);
        query.include(RewardLotteryRecord.VOUCHER);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        return query;
    }

    private AVQuery<RewardLottery> getConditionQuery() {
        AVQuery<RewardLottery> query = AVQuery.getQuery(RewardLottery.class);
        query.whereLessThanOrEqualTo(RewardLottery.PRICE_TO_USE, subTotalPrice);
        return query;
    }
}
