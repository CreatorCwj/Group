package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.constant.CloudFunction;
import com.constant.OrderStateEnum;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeFunctionCallback;
import com.model.Merchant;
import com.model.Order;
import com.model.RewardLotteryRecord;
import com.model.Voucher;
import com.util.DateUtils;
import com.util.JsonUtils;
import com.util.Utils;
import com.widget.RoundImageView;
import com.widget.dialog.MessageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_order_detail)
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String ORDER_ID_KEY = "orderId";

    @InjectView(R.id.order_detail_spareTime_layout)
    private LinearLayout spareTimeLayout;

    @InjectView(R.id.order_detail_count_time_tv)
    private TextView countTimeTv;

    @InjectView(R.id.order_detail_merchant_layout)
    private LinearLayout merchantLayout;

    @InjectView(R.id.order_detail_iv)
    private RoundImageView iv;

    @InjectView(R.id.order_detail_merchant_name_tv)
    private TextView merchantNameTv;

    @InjectView(R.id.order_detail_voucher_name_tv)
    private TextView voucherNameTv;

    @InjectView(R.id.order_detail_cur_price_tv)
    private TextView curPriceTv;

    @InjectView(R.id.order_detail_ori_price_tv)
    private TextView oriPriceTv;

    @InjectView(R.id.order_detail_id_tv)
    private TextView idTv;

    @InjectView(R.id.order_detail_createAT_tv)
    private TextView createTimeTv;

    @InjectView(R.id.order_detail_phone_tv)
    private TextView phoneTv;

    @InjectView(R.id.order_detail_type_tv)
    private TextView typeTv;

    @InjectView(R.id.order_detail_num_tv)
    private TextView numTv;

    @InjectView(R.id.order_detail_used_lottery_tv)
    private TextView usedLotteryTv;

    @InjectView(R.id.order_detail_total_price_tv)
    private TextView totalTv;

    @InjectView(R.id.order_detail_func_tv)
    private TextView funcTv;

    private String orderId;

    private Order order;
    private Merchant merchant;
    private Voucher voucher;

    private OrderStateEnum state;

    private long spareSeconds;

    private MessageDialog payDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        getOrder();//获取订单信息
        initDialog();
    }

    private void initDialog() {
        loadingDialog.setCancelable(false);//不可取消
        payDialog = new MessageDialog(this);
        payDialog.setCanceledOnTouchOutside(false);
        payDialog.setCancelable(false);
    }

    private void receiveIntent() {
        orderId = getIntent().getStringExtra(ORDER_ID_KEY);
    }

    private void getOrder() {
        showLoadingDialog("加载订单信息...");
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("includes", getIncludes());
        AVCloud.rpcFunctionInBackground(CloudFunction.GET_ORDER, params, new SafeFunctionCallback<Order>(this) {
            @Override
            protected void functionBack(Order order, AVException e) {
                if (e != null) {//获取失败
                    cancelLoadingDialog();
                    String reason = JsonUtils.getStrValueOfJsonStr(e.getMessage(), "error");
                    Utils.showToast(OrderDetailActivity.this, "获取订单信息失败:" + (reason == null ? "" : ":" + reason));
                    finish();
                } else {
                    OrderDetailActivity.this.order = order;
                    voucher = order.getVoucher();
                    merchant = voucher.getMerchant();
                    setInfo();//设置完信息后关闭加载框体验好
                }
            }
        });
    }

    private List<String> getIncludes() {
        List<String> includes = new ArrayList<>();
        includes.add(Order.USED_LOTTERY);
        includes.add(Order.USED_LOTTERY + "." + RewardLotteryRecord.REWARD_LOTTERY);
        includes.add(Order.VOUCHER);
        includes.add(Order.VOUCHER + "." + Voucher.REWARD_LOTTERY);
        includes.add(Order.VOUCHER + "." + Voucher.REWARD_POINT);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        includes.add(Order.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        return includes;
    }

    private void setInfo() {
        //设置剩余时间(有的话)
        setSpareTime();
        //设置商家信息
        setMerchantLayout();
        //订单信息
        setOrderInfo();
        //设置功能按钮
        setFuncTv();
        //关闭加载框
        cancelLoadingDialog();
    }

    private void setSpareTime() {
        if (order.getStatus() == OrderStateEnum.WAIT_PAY.getId() && order.getLimitMinute() > 0) {//有剩余时间限制
            spareTimeLayout.setVisibility(View.VISIBLE);
            if (order.getSpareSeconds() <= 0) {//没有剩余时间了
                countTimeTv.setText("已过期");
                updFuncTv(OrderStateEnum.OVERDUE);
            } else {//有剩余时间
                spareSeconds = order.getSpareSeconds();
                String spareTime = DateUtils.getTimeStringOfSeconds(spareSeconds);
                countTimeTv.setText(spareTime);
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        spareSeconds = spareSeconds - 1;
                        if (spareSeconds < 0)
                            spareSeconds = 0;
                        if (spareSeconds == 0) {//过期了,更新view状态,不用再发送handle了
                            countTimeTv.setText("已过期");
                            updFuncTv(OrderStateEnum.OVERDUE);
                        } else {//更新,继续倒计时
                            String spareTime = DateUtils.getTimeStringOfSeconds(spareSeconds);
                            countTimeTv.setText(spareTime);
                            this.sendEmptyMessageDelayed(0, 1000);//1秒发送一次
                        }
                    }
                }.sendEmptyMessageDelayed(0, 1000);//1秒发送一次
            }
        } else {
            spareTimeLayout.setVisibility(View.GONE);
        }
    }

    private void setFuncTv() {
        funcTv.setOnClickListener(this);
        updFuncTv(OrderStateEnum.getEnumMap(order.getStatus()));
    }

    private void updFuncTv(OrderStateEnum stateEnum) {
        state = stateEnum;
        //待支付-支付 待使用-查看团购券 待评价-评价 已完成-再来一单 已过期-已过期(不能点)
        if (state == null) {
            funcTv.setText("");
            funcTv.setEnabled(false);
            funcTv.setClickable(false);
            return;
        }
        switch (state) {
            case WAIT_PAY:
                funcTv.setText("支付");
                break;
            case WAIT_USE:
                funcTv.setText("查看团购券");
                break;
            case USED:
                funcTv.setText("评价");
                break;
            case REMARKED:
                funcTv.setText("再来一单");
                break;
            case OVERDUE:
                funcTv.setText("已过期");
                funcTv.setEnabled(false);
                funcTv.setClickable(false);
                break;
        }
    }

    private void setOrderInfo() {
        idTv.setText(order.getObjectId());
        createTimeTv.setText(DateUtils.getTimeString(order.getCreatedAt()));
        phoneTv.setText(order.getPhone());
        typeTv.setText(voucher.getName());
        numTv.setText("" + order.getNum());
        totalTv.setText("¥" + order.getTotalPrice());
        if (order.getUsedLottery() == null) {
            usedLotteryTv.setText("未使用");
        } else {
            usedLotteryTv.setText("满" + order.getUsedLottery().getRewardLottery().getPriceToUse() + "减" + order.getUsedLottery().getRewardLottery().getPrice());
        }
    }

    private void setMerchantLayout() {
        //img
        if (voucher.getImages() != null && voucher.getImages().size() > 0) {
            ImageLoader.displayImage(iv, voucher.getImages().get(0));
        } else if (merchant.getImages() != null && merchant.getImages().size() > 0) {
            ImageLoader.displayImage(iv, merchant.getImages().get(0));
        }
        //info
        merchantNameTv.setText(merchant.getName());
        voucherNameTv.setText(voucher.getName());
        curPriceTv.setText("" + voucher.getCurrentPrice());
        oriPriceTv.setText("¥" + voucher.getOriginPrice());
        //click
        merchantLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_detail_merchant_layout:
                Intent intent = new Intent(OrderDetailActivity.this, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, merchant);
                startActivity(intent);
                break;
            case R.id.order_detail_func_tv:
                clickFunc();
                break;
        }
    }

    private void clickFunc() {
        //支付,查看,评价,再来
        switch (state) {
            case WAIT_PAY://支付(先提示)
                tipPay();
                break;
            case WAIT_USE:
                Utils.showToast(this, "查看团购券");
                break;
            case USED:
                Utils.showToast(this, "评价");
                break;
            case REMARKED://再来一单
                Intent intent = new Intent(OrderDetailActivity.this, VoucherDetailActivity.class);
                intent.putExtra(VoucherDetailActivity.VOUCHER_KEY, voucher);
                intent.putExtra(VoucherDetailActivity.MERCHANT_KEY, merchant);
                startActivity(intent);
                break;
        }
    }

    private void tipPay() {
        payDialog.setTitle("确认支付");
        String msg = "当前支付金额:" + order.getTotalPrice() + "元\n请确认支付";
        payDialog.setMessage(msg);
        payDialog.setNegativeText("取消");
        payDialog.setPositiveText("继续支付");
        payDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pay
                pay();
            }
        });
        payDialog.show();
    }

    private void pay() {
        //采用模拟支付,调用自定义云函数检验相关信息,可以支付后直接支付相应价钱即可
        showLoadingDialog("正在支付中...");
        //自定义支付方法
        AVCloud.rpcFunctionInBackground(CloudFunction.PAY_ORDER, order, new SafeFunctionCallback<Object>(this) {
            @Override
            protected void functionBack(Object obj, AVException e) {
                if (e != null) {
                    String reason = JsonUtils.getStrValueOfJsonStr(e.getMessage(), "error");
                    Utils.showToast(OrderDetailActivity.this, "支付失败" + (reason == null ? "" : ":" + reason));
                } else {
                    Utils.showToast(OrderDetailActivity.this, "支付成功,团购券可在我的界面里查看");
                    finish();
                }
                cancelLoadingDialog();
            }
        });
    }
}
