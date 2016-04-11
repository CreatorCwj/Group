package com.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.model.Order;
import com.widget.RoundImageView;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_coupon)
public class CouponActivity extends BaseActivity {

    public static final String ORDER_KEY = "order";

    @InjectView(R.id.coupon_merchant_name_tv)
    private TextView merchantNameTv;

    @InjectView(R.id.coupon_voucher_name_tv)
    private TextView voucherNameTv;

    @InjectView(R.id.coupon_num_tv)
    private TextView numTv;

    @InjectView(R.id.coupon_order_id_tv)
    private TextView orderIdTv;

    @InjectView(R.id.coupon_qr_code_iv)
    private RoundImageView qrCodeIv;

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setInfo();
    }

    private void setInfo() {
        if (order != null) {
            merchantNameTv.setText(order.getVoucher().getMerchant().getName());
            voucherNameTv.setText(order.getVoucher().getName());
            numTv.setText("数量:" + order.getNum());
            orderIdTv.setText(getOrderId(order.getObjectId()));
            if (!TextUtils.isEmpty(order.getQrCodeImage())) {
                ImageLoader.displayImage(qrCodeIv, order.getQrCodeImage());
            } else {
                qrCodeIv.setImageDrawable(null);
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

    private void receiveIntent() {
        order = getIntent().getParcelableExtra(ORDER_KEY);
    }

}
