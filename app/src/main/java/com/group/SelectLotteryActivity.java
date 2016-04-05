package com.group;

import android.content.Intent;
import android.view.View;

import com.model.RewardLotteryRecord;

/**
 * Created by cwj on 16/4/6.
 * 抵用券列表(可选择和取消选择)
 */
public class SelectLotteryActivity extends BaseLotteryListActivity {

    @Override
    protected void onClickRightText(View v) {
        Intent intent = new Intent();
        intent.putExtra("", (RewardLotteryRecord) null);
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
        intent.putExtra("", adapter.getDataItem(position));
        setResult(RESULT_OK, intent);
        finish();
    }
}
