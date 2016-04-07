package com.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.adapter.LotteryRecordAdapter;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.constant.LotteryStateEnum;
import com.model.Merchant;
import com.model.RewardLotteryRecord;
import com.model.Voucher;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

/**
 * Created by cwj on 16/4/6.
 * 抵用券列表基类(展示抵用券记录)
 */
public class BaseLotteryListActivity extends BaseAVQueryListActivity<RewardLotteryRecord> implements LoadMoreRecyclerView.OnItemClickListener, LoadMoreRecyclerView.OnItemLongClickListener {

    public static final String LOTTERY_STATUS_KEY = "status";

    protected LotteryStateEnum state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setToolbarRight();
        setRLRView();//覆盖点击监听器,可用于子类重写
    }

    private void setRLRView() {
        rlrView.setOnItemClickListener(this);
        rlrView.setOnItemLongClickListener(this);
    }

    private void setToolbarRight() {
        String rightText = getRightText();
        if (TextUtils.isEmpty(rightText))
            return;
        toolBar.setRightText(rightText);
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRightText(v);
            }
        });
    }

    protected void onClickRightText(View v) {

    }

    protected String getRightText() {
        return null;
    }

    private void receiveIntent() {
        state = (LotteryStateEnum) getIntent().getSerializableExtra(LOTTERY_STATUS_KEY);
        if (state == null)//默认是待使用的列表
            state = LotteryStateEnum.WAIT_USE;
    }

    @Override
    final protected RecyclerViewAdapter<RewardLotteryRecord> getAdapter() {
        return new LotteryRecordAdapter(this);
    }

    @Override
    final protected String getTitleText() {
        return "抵用券";
    }

    @Override
    protected AVQuery<RewardLotteryRecord> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        AVQuery<RewardLotteryRecord> query = AVQuery.getQuery(RewardLotteryRecord.class);
        query.whereEqualTo(RewardLotteryRecord.USER, AVUser.getCurrentUser());
        query.whereEqualTo(RewardLotteryRecord.STATUS, state.getId());//传入的抵用券状态
        query.orderByDescending(RewardLotteryRecord.CREATED_AT);//获得时间排序
        query.include(RewardLotteryRecord.REWARD_LOTTERY);
        query.include(RewardLotteryRecord.VOUCHER);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        query.include(RewardLotteryRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        return query;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}
