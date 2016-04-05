package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constant.CategoryEnum;
import com.constant.LotteryStateEnum;
import com.group.MerchantDetailActivity;
import com.group.R;
import com.model.Category;
import com.model.RewardLotteryRecord;
import com.util.DateUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.Date;

/**
 * Created by cwj on 16/4/6.
 * 抵用券记录adapter
 */
public class LotteryRecordAdapter extends RecyclerViewAdapter<RewardLotteryRecord> {

    public LotteryRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            final RewardLotteryRecord lotteryRecord = getDataItem(position);
            LotteryRecordViewHolder viewHolder = (LotteryRecordViewHolder) holder;
            viewHolder.merchantNameTv.setText(lotteryRecord.getVoucher().getMerchant().getName());
            viewHolder.priceTv.setText("" + lotteryRecord.getRewardLottery().getPrice());
            viewHolder.priceToUseTv.setText("满" + lotteryRecord.getRewardLottery().getPriceToUse() + "可用");
            viewHolder.createTimeTv.setText("获得时间:" + DateUtils.getDateString(lotteryRecord.getCreatedAt()));
            //品类图标
            setCatIv(viewHolder.categoryIv, lotteryRecord.getVoucher().getMerchant().getCategory());
            //状态
            setStatus(viewHolder.statusTv, lotteryRecord.getStatus());
            //截至日期
            setEndTime(viewHolder.endTimeTv, lotteryRecord.getCreatedAt(), lotteryRecord.getRewardLottery().getLimitDays());
            //click to merchant
            viewHolder.merchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入商家详情页
                    Intent intent = new Intent(context, MerchantDetailActivity.class);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, lotteryRecord.getVoucher().getMerchant());
                    context.startActivity(intent);
                }
            });
        }
    }

    private void setEndTime(TextView endTimeTv, Date createdAt, int limitDays) {
        Date endTime = DateUtils.getFutureDate(createdAt, limitDays);
        endTimeTv.setText("有效期至:" + DateUtils.getDateString(endTime));
    }

    private void setStatus(TextView statusTv, int status) {
        LotteryStateEnum stateEnum = LotteryStateEnum.getEnumMap(status);
        if (stateEnum == null) {
            statusTv.setText("");
            return;
        }
        statusTv.setText(stateEnum.getState());
    }

    private void setCatIv(ImageView categoryIv, Category category) {
        CategoryEnum categoryEnum = CategoryEnum.getEnumMap(category);
        if (categoryEnum == null) {
            categoryIv.setImageDrawable(null);
            return;
        }
        categoryIv.setImageResource(categoryEnum.getSmallIconId());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new LotteryRecordViewHolder(layoutInflater.inflate(R.layout.lottery_record_item, parent, false));
    }

    public class LotteryRecordViewHolder extends RecyclerView.ViewHolder {

        LinearLayout merchantLayout;
        ImageView categoryIv;
        TextView merchantNameTv;
        TextView priceTv;
        TextView priceToUseTv;
        TextView createTimeTv;
        TextView statusTv;
        TextView endTimeTv;

        public LotteryRecordViewHolder(View itemView) {
            super(itemView);
            merchantLayout = (LinearLayout) itemView.findViewById(R.id.lottery_record_merchant_layout);
            categoryIv = (ImageView) itemView.findViewById(R.id.lottery_record_category_iv);
            merchantNameTv = (TextView) itemView.findViewById(R.id.lottery_record_merchant_name_tv);
            priceTv = (TextView) itemView.findViewById(R.id.lottery_record_price_tv);
            priceToUseTv = (TextView) itemView.findViewById(R.id.lottery_record_use_price_tv);
            createTimeTv = (TextView) itemView.findViewById(R.id.lottery_record_time_tv);
            statusTv = (TextView) itemView.findViewById(R.id.lottery_record_status_tv);
            endTimeTv = (TextView) itemView.findViewById(R.id.lottery_record_end_time_tv);
        }
    }
}
