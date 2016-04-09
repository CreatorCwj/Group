package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.group.base.BaseActivity;
import com.leancloud.SafeFunctionCallback;
import com.model.Merchant;
import com.model.Order;
import com.model.RewardLottery;
import com.model.RewardLotteryRecord;
import com.model.RewardPoint;
import com.model.User;
import com.model.Voucher;
import com.util.JsonUtils;
import com.util.Utils;
import com.widget.CancelableEditView;
import com.widget.dialog.MessageDialog;
import com.widget.functionButton.FunctionButton;
import com.widget.functionButton.SelectNumFunctionButton;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_submit_order)
public class SubmitOrderActivity extends BaseActivity implements View.OnClickListener {

    public static final int LOGIN_CODE = 1;
    public static final int SELECT_LOTTERY_CODE = 2;

    public static final String VOUCHER_KEY = "voucher";
    public static final String MERCHANT_KEY = "merchant";

    @InjectView(R.id.submit_order_price_fb)
    private FunctionButton priceFb;

    @InjectView(R.id.submit_order_num_fb)
    private SelectNumFunctionButton numFb;

    @InjectView(R.id.submit_order_subtotal_fb)
    private FunctionButton subTotalFb;

    @InjectView(R.id.submit_order_used_lottery_fb)
    private FunctionButton usedLotteryFb;

    @InjectView(R.id.submit_order_total_fb)
    private FunctionButton totalFb;

    @InjectView(R.id.submit_order_phone_et)
    private CancelableEditView phoneEt;

    @InjectView(R.id.submit_order_send_lottery_fb)
    private FunctionButton sendLotteryFb;

    @InjectView(R.id.submit_order_send_point_fb)
    private FunctionButton sendPointFb;

    @InjectView(R.id.submit_order_submit_tv)
    private TextView submitTv;

    private Voucher voucher;
    private Merchant merchant;

    private RewardLotteryRecord usedLottery;

    private MessageDialog payDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setInfo();
        setListener();
        initDialog();
    }

    private void initDialog() {
        loadingDialog.setCancelable(false);//不可取消
        payDialog = new MessageDialog(this);
        payDialog.setCanceledOnTouchOutside(false);
        payDialog.setCancelable(false);
    }

    private void setListener() {
        usedLotteryFb.setOnClickListener(this);
        submitTv.setOnClickListener(this);
        numFb.setOnNumChangedListener(new SelectNumFunctionButton.OnNumChangedListener() {
            @Override
            public void onNumChanged(int newNum) {
                updatePrice();
            }
        });
    }

    private void updatePrice() {
        int newNum = numFb.getNum();//最新数量
        float singlePrice = 0;
        if (voucher != null)
            singlePrice = (float) voucher.getCurrentPrice();
        //小计
        float subTotal = singlePrice * newNum;
        subTotalFb.setDescribe("¥" + subTotal);
        //总计(小计减去抵用券抵用的价格,拿到的抵用券一定是符合要求的)
        if (usedLottery == null) {
            totalFb.setDescribe("¥" + subTotal);
        } else {
            totalFb.setDescribe("¥" + (subTotal - usedLottery.getRewardLottery().getPrice()));
        }
    }

    private void receiveIntent() {
        voucher = getIntent().getParcelableExtra(VOUCHER_KEY);
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
    }

    private void setInfo() {
        if (voucher != null && merchant != null) {
            //name price
            priceFb.setName(voucher.getName());
            priceFb.setDescribe("¥" + voucher.getCurrentPrice());
            //max num,默认100
            if (voucher.getLimitNum() > 0)
                numFb.setMaxNum(voucher.getLimitNum());
            //小计
            subTotalFb.setDescribe("¥0");
            //总计
            totalFb.setDescribe("¥0");
            //phone
            if (AVUser.getCurrentUser(User.class) != null)
                phoneEt.setText(AVUser.getCurrentUser(User.class).getMobilePhoneNumber());
            //send
            RewardLottery lottery = voucher.getRewardLottery();
            if (lottery != null) {
                sendLotteryFb.setDescribe("满" + lottery.getPriceToUse() + "减" + lottery.getPrice());
            } else {
                sendLotteryFb.setDescribe("无");
            }
            RewardPoint point = voucher.getRewardPoint();
            if (point != null) {
                sendPointFb.setDescribe("" + point.getPoint());
            } else {
                sendPointFb.setDescribe("无");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (voucher == null || merchant == null)
            return;
        //先考虑未登陆情况
        switch (v.getId()) {
            case R.id.submit_order_used_lottery_fb://使用抵用券
                if (checkLogin())
                    selectLottery();
                break;
            case R.id.submit_order_submit_tv://提交
                if (checkLogin())
                    submit();
                break;
        }
    }

    private void submit() {
        //num
        int num = numFb.getNum();
        if (num <= 0 || num > voucher.getLimitNum()) {
            Utils.showToast(this, "请选择正确的购买数量");
            return;
        }
        //phone
        String phone = phoneEt.getText();
        if (!Utils.isCorrectPhone(phone)) {
            Utils.showToast(this, "请填写正确手机号");
            return;
        }
        showLoadingDialog("提交订单中...");
        //submit(只能用map包装,model类的话无法在为保存的情况下get里面的属性)
        Map<String, Object> params = new HashMap<>();
        params.put(Order.NUM, num);
        params.put(Order.USED_LOTTERY, usedLottery);
        params.put(Order.PHONE, phone);
        params.put(Order.TOTAL_PRICE, Double.parseDouble(totalFb.getDescribe().substring(1)));//记录总价
        params.put(Order.VOUCHER, voucher);
        params.put(Voucher.MERCHANT, merchant);//特殊字段(无法直接获取Voucher的Merchant的子类字段)
        params.put(Order.USER, AVUser.getCurrentUser());
        //自定义提交订单方法
        AVCloud.rpcFunctionInBackground(CloudFunction.SUBMIT_ORDER, params, new SafeFunctionCallback<Order>(this) {
            @Override
            protected void functionBack(Order newOrder, AVException e) {
                if (e != null) {
                    String reason = JsonUtils.getStrValueOfJsonStr(e.getMessage(), "error");
                    Utils.showToast(SubmitOrderActivity.this, "订单提交失败" + (reason == null ? "" : ":" + reason));
                } else {
                    Utils.showToast(SubmitOrderActivity.this, "订单提交成功");
                    //tip and pay
                    tipAndPay(newOrder);
                }
                cancelLoadingDialog();
            }
        });
    }

    private void tipAndPay(final Order order) {
        payDialog.setTitle("支付提示");
        StringBuilder msg = new StringBuilder("订单提交成功,您可以选择立即支付或稍后支付,选择稍后支付请前往订单页面进行支付");
        if (order.getLimitMinute() > 0)
            msg.append("\n该订单有效支付时间为").append(order.getLimitMinute()).append("分钟");
        payDialog.setMessage(msg.toString());
        payDialog.setNegativeText("稍后支付");
        payDialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//退出界面,稍后支付
            }
        });
        payDialog.setPositiveText("立即支付");
        payDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tip pay
                tipPay(order);
            }
        });
        payDialog.show();
    }

    private void tipPay(final Order order) {
        payDialog.setTitle("确认支付");
        String msg = "当前支付金额:" + order.getTotalPrice() + "元\n请确认支付";
        payDialog.setMessage(msg);
        payDialog.setNegativeText("稍后支付");
        payDialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//退出界面,稍后支付
            }
        });
        payDialog.setPositiveText("继续支付");
        payDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pay
                pay(order);
            }
        });
        payDialog.show();
    }

    private void pay(Order order) {
        //采用模拟支付,调用自定义云函数检验相关信息,可以支付后直接支付相应价钱即可
        showLoadingDialog("正在支付中...");
        //自定义支付方法
        AVCloud.rpcFunctionInBackground(CloudFunction.PAY_ORDER, order, new SafeFunctionCallback<Object>(this) {
            @Override
            protected void functionBack(Object obj, AVException e) {
                if (e != null) {
                    String reason = JsonUtils.getStrValueOfJsonStr(e.getMessage(), "error");
                    Utils.showToast(SubmitOrderActivity.this, "支付失败" + (reason == null ? "" : ":" + reason));
                } else {
                    Utils.showToast(SubmitOrderActivity.this, "支付成功,团购券可在我的界面里查看");
                    finish();
                }
                cancelLoadingDialog();
            }
        });
    }

    private void selectLottery() {
        //选用抵用券
        Intent intent = new Intent(SubmitOrderActivity.this, SelectLotteryActivity.class);
        intent.putExtra(SelectLotteryActivity.SUB_TOTAL_PRICE_KEY, Double.parseDouble(subTotalFb.getDescribe().substring(1)));
        startActivityForResult(intent, SELECT_LOTTERY_CODE);
    }

    private boolean checkLogin() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null)
            return true;
        Intent intent = new Intent(SubmitOrderActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_CODE);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGIN_CODE) {//登陆成功
                User user = AVUser.getCurrentUser(User.class);
                if (user != null)//填写手机号(会覆盖)
                    phoneEt.setText(user.getMobilePhoneNumber());
            } else if (requestCode == SELECT_LOTTERY_CODE) {//选择抵用券完成
                usedLottery = data.getParcelableExtra(SelectLotteryActivity.SELECT_LOTTERY_KEY);
                //更新view
                if (usedLottery == null) {
                    usedLotteryFb.setDescribe("使用抵用券");
                } else {
                    usedLotteryFb.setDescribe("满" + usedLottery.getRewardLottery().getPriceToUse() + "减" + usedLottery.getRewardLottery().getPrice());
                }
                //更新价格
                updatePrice();
            }
        }
    }
}
