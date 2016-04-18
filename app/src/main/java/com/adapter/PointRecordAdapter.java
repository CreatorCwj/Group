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
import com.group.MerchantDetailActivity;
import com.group.R;
import com.model.Category;
import com.model.RewardPointRecord;
import com.util.DateUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/6.
 * 积分记录adapter
 */
public class PointRecordAdapter extends RecyclerViewAdapter<RewardPointRecord> {

    public PointRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            final RewardPointRecord pointRecord = getDataItem(position);
            PointRecordViewHolder viewHolder = (PointRecordViewHolder) holder;
            viewHolder.merchantNameTv.setText(pointRecord.getVoucher().getMerchant().getName());
            viewHolder.voucherNameTv.setText(pointRecord.getVoucher().getName());
            viewHolder.numTv.setText("购买数量:" + pointRecord.getNum());
            viewHolder.totalPointTv.setText("" + (pointRecord.getRewardPoint().getPoint() * pointRecord.getNum()));
            viewHolder.timeTv.setText("获得时间:" + DateUtils.getDateString(pointRecord.getCreatedAt()));
            //品类图标
            setCatIv(viewHolder.categoryIv, pointRecord.getVoucher().getMerchant().getCategory());
            //click to merchant
            viewHolder.merchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入商家详情页
                    Intent intent = new Intent(context, MerchantDetailActivity.class);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, pointRecord.getVoucher().getMerchant());
                    context.startActivity(intent);
                }
            });
        }
    }

    private void setCatIv(ImageView categoryIv, Category category) {
        CategoryEnum categoryEnum = CategoryEnum.getEnumMap(category);
        if (categoryEnum == null) {
            categoryIv.setImageDrawable(null);
            return;
        }
        categoryIv.setImageResource(categoryEnum.getBigIconId());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new PointRecordViewHolder(layoutInflater.inflate(R.layout.point_record_item, parent, false));
    }

    public class PointRecordViewHolder extends RecyclerView.ViewHolder {

        LinearLayout merchantLayout;
        ImageView categoryIv;
        TextView merchantNameTv;
        TextView totalPointTv;
        TextView voucherNameTv;
        TextView numTv;
        TextView timeTv;

        public PointRecordViewHolder(View itemView) {
            super(itemView);
            merchantLayout = (LinearLayout) itemView.findViewById(R.id.point_record_merchant_layout);
            categoryIv = (ImageView) itemView.findViewById(R.id.point_record_category_iv);
            merchantNameTv = (TextView) itemView.findViewById(R.id.point_record_merchant_name_tv);
            totalPointTv = (TextView) itemView.findViewById(R.id.point_record_total_point_tv);
            voucherNameTv = (TextView) itemView.findViewById(R.id.point_record_voucher_name_tv);
            numTv = (TextView) itemView.findViewById(R.id.point_record_num_tv);
            timeTv = (TextView) itemView.findViewById(R.id.point_record_time_tv);
        }
    }
}
