package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group.R;
import com.imageLoader.ImageLoader;
import com.model.Order;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/14.
 * 团购券adapter
 */
public class CouponAdapter extends RecyclerViewAdapter<Order> {

    public CouponAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Order order = getDataItem(position);
            CouponViewHolder viewHolder = (CouponViewHolder) holder;
            viewHolder.merchantNameTv.setText(order.getVoucher().getMerchant().getName());
            viewHolder.voucherNameTv.setText(order.getVoucher().getName());
            viewHolder.numTv.setText("数量:" + order.getNum());
            viewHolder.orderIdTv.setText(getOrderId(order.getObjectId()));
            if (!TextUtils.isEmpty(order.getQrCodeImage())) {
                ImageLoader.displayImage(viewHolder.qrCodeIv, order.getQrCodeImage());
            } else {
                viewHolder.qrCodeIv.setImageDrawable(null);
            }
        }
    }

    private String getOrderId(String orderId) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < orderId.length(); i += 4) {
            sb.append(orderId.substring(i, i + 4));
            if (i + 4 < orderId.length())
                sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new CouponViewHolder(layoutInflater.inflate(R.layout.coupon_item, parent, false));
    }

    public class CouponViewHolder extends RecyclerView.ViewHolder {

        ImageView qrCodeIv;
        TextView merchantNameTv;
        TextView voucherNameTv;
        TextView numTv;
        TextView orderIdTv;

        public CouponViewHolder(View itemView) {
            super(itemView);
            qrCodeIv = (ImageView) itemView.findViewById(R.id.coupon_item_qr_code_iv);
            merchantNameTv = (TextView) itemView.findViewById(R.id.coupon_item_merchant_name_tv);
            voucherNameTv = (TextView) itemView.findViewById(R.id.coupon_item_voucher_name_tv);
            numTv = (TextView) itemView.findViewById(R.id.coupon_item_num_tv);
            orderIdTv = (TextView) itemView.findViewById(R.id.coupon_item_order_id_tv);
        }
    }
}
