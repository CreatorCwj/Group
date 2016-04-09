package com.group;

import com.adapter.PointRecordAdapter;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseAVQueryListActivity;
import com.model.Merchant;
import com.model.RewardPointRecord;
import com.model.Voucher;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/6.
 * 积分记录列表
 */
public class PointRecordActivity extends BaseAVQueryListActivity<RewardPointRecord> {

    @Override
    protected RecyclerViewAdapter<RewardPointRecord> getAdapter() {
        return new PointRecordAdapter(this);
    }

    @Override
    protected String getTitleText() {
        return "积分记录";
    }

    @Override
    protected AVQuery<RewardPointRecord> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        AVQuery<RewardPointRecord> query = AVQuery.getQuery(RewardPointRecord.class);
        query.whereEqualTo(RewardPointRecord.USER, AVUser.getCurrentUser());//已登陆状态
        query.orderByDescending(RewardPointRecord.UPDATED_AT);//最近日期排序
        query.include(RewardPointRecord.REWARD_POINT);
        query.include(RewardPointRecord.VOUCHER);
        query.include(RewardPointRecord.VOUCHER + "." + Voucher.MERCHANT);
        query.include(RewardPointRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        query.include(RewardPointRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        query.include(RewardPointRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        query.include(RewardPointRecord.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        return query;
    }
}
