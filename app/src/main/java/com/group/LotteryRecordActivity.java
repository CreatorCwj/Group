package com.group;

import android.content.Intent;
import android.view.View;

import com.constant.LotteryStateEnum;
import com.group.base.BaseLotteryListActivity;

/**
 * Created by cwj on 16/4/6.
 * 抵用券记录(右上角可查看过期券)
 */
public class LotteryRecordActivity extends BaseLotteryListActivity {

    @Override
    protected void onClickRightText(View v) {
        Intent intent = new Intent(LotteryRecordActivity.this, BaseLotteryListActivity.class);//过期券
        intent.putExtra(LOTTERY_STATUS_KEY, LotteryStateEnum.OVERDUE);
        startActivity(intent);
    }

    @Override
    protected String getRightText() {
        return "过期券";
    }
}
