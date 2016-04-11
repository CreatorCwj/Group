package com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.constant.GradeEnum;
import com.constant.LotteryStateEnum;
import com.constant.OrderStateEnum;
import com.fragment.base.BaseSlideFragment;
import com.group.CollectionActivity;
import com.group.LoginActivity;
import com.group.LotteryRecordActivity;
import com.group.MyCouponActivity;
import com.group.MyRemarkActivity;
import com.group.OrderListActivity;
import com.group.PointRecordActivity;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeCountCallback;
import com.leancloud.SafeGetCallback;
import com.model.Order;
import com.model.RewardLotteryRecord;
import com.model.User;
import com.widget.RoundImageView;
import com.widget.functionButton.FunctionButton;
import com.widget.radio.RadioView;

import roboguice.inject.InjectView;

public class MineFragment extends BaseSlideFragment implements View.OnClickListener {

    @InjectView(R.id.mine_info_layout)
    private RelativeLayout infoLayout;

    @InjectView(R.id.mine_iv)
    private RoundImageView myIv;

    @InjectView(R.id.mine_name)
    private TextView myName;

    @InjectView(R.id.mine_lv)
    private TextView myLv;

    @InjectView(R.id.mine_group_voucher_layout)
    private LinearLayout voucherLayout;

    @InjectView(R.id.mine_group_voucher_tv)
    private TextView voucherTv;

    @InjectView(R.id.mine_point_layout)
    private LinearLayout pointLayout;

    @InjectView(R.id.mine_point_tv)
    private TextView pointTv;

    @InjectView(R.id.mine_lottery_layout)
    private LinearLayout lotteryLayout;

    @InjectView(R.id.mine_lottery_tv)
    private TextView lotteryTv;

    @InjectView(R.id.wait_pay_rv)
    private RadioView waitPayRv;

    @InjectView(R.id.wait_use_rv)
    private RadioView waitUseRv;

    @InjectView(R.id.wait_remark_rv)
    private RadioView waitRemarkRv;

    @InjectView(R.id.finish_rv)
    private RadioView finishRv;

    @InjectView(R.id.mine_my_collection_btn)
    private FunctionButton myCollectionBtn;

    @InjectView(R.id.mine_my_remark_btn)
    private FunctionButton myRemarkBtn;

    @InjectView(R.id.mine_my_like_btn)
    private FunctionButton myLikeBtn;

    @InjectView(R.id.mine_my_setting_btn)
    private FunctionButton mySettingBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListener();
    }

    @Override
    public void onSlideFragmentPause() {
        super.onSlideFragmentPause();
    }

    @Override
    public void onSlideFragmentResume() {
        super.onSlideFragmentResume();
        //根据用户登录状态更新显示内容
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            user.fetchInBackground(new SafeGetCallback<AVObject>(getActivity()) {//获取最新的用户对象
                @Override
                public void getResult(AVObject object, AVException e) {
                    setImage();
                    setUsername();
                    setLv();
                    setPointNum();
                }
            });
        } else {
            setImage();
            setUsername();
            setLv();
            setPointNum();
        }
        setVoucherNum();//未使用团购券
        setLotteryNum();//未使用抵用券
    }

    private void setLotteryNum() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {
            lotteryTv.setText("0");
        } else {//获取待使用的抵用券数量
            AVQuery<RewardLotteryRecord> query = AVQuery.getQuery(RewardLotteryRecord.class);
            query.whereEqualTo(RewardLotteryRecord.USER, user);
            query.whereEqualTo(RewardLotteryRecord.STATUS, LotteryStateEnum.WAIT_USE.getId());
            query.countInBackground(new SafeCountCallback(getActivity()) {
                @Override
                public void getCount(int count, AVException e) {
                    if (e == null) {
                        lotteryTv.setText("" + count);
                    } else {
                        lotteryTv.setText("0");
                    }
                }
            });
        }
    }

    private void setPointNum() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {
            pointTv.setText("0");
        } else {//todo 获取该用户的剩余积分(这里应该获取最新的user对象)
            pointTv.setText("" + user.getPoint());
        }
    }

    private void setVoucherNum() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {
            voucherTv.setText("0");
        } else {//获取待使用的团购券数量
            AVQuery<Order> query = AVQuery.getQuery(Order.class);
            query.whereEqualTo(Order.USER, user);
            query.whereEqualTo(Order.STATUS, OrderStateEnum.WAIT_USE.getId());
            query.countInBackground(new SafeCountCallback(getActivity()) {
                @Override
                public void getCount(int count, AVException e) {
                    if (e == null) {
                        voucherTv.setText("" + count);
                    } else {
                        voucherTv.setText("0");
                    }
                }
            });
        }
    }

    private void setLv() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {//未登录不可见
            myLv.setVisibility(View.GONE);
        } else {
            myLv.setVisibility(View.VISIBLE);
            int value = user.getGrowthValue();
            myLv.setText("Lv" + GradeEnum.getGradeByValue(value).getGrade());
        }
    }

    private void setUsername() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {
            myName.setText("请点击登陆");
        } else {
            if (TextUtils.isEmpty(user.getDisplayName())) {//没有昵称用username
                myName.setText(user.getUsername());
            } else {
                myName.setText(user.getDisplayName());
            }
        }
    }

    private void setImage() {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null || TextUtils.isEmpty(user.getImageUrl())) {//无头像
            myIv.setImageResource(R.drawable.no_user_icon);
        } else {
            ImageLoader.displayImage(myIv, user.getImageUrl());
        }
    }

    private void setListener() {
        infoLayout.setOnClickListener(this);
        voucherLayout.setOnClickListener(this);
        pointLayout.setOnClickListener(this);
        lotteryLayout.setOnClickListener(this);
        waitPayRv.setOnClickListener(this);
        waitUseRv.setOnClickListener(this);
        waitRemarkRv.setOnClickListener(this);
        finishRv.setOnClickListener(this);
        myCollectionBtn.setOnClickListener(this);
        myRemarkBtn.setOnClickListener(this);
        myLikeBtn.setOnClickListener(this);
        mySettingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_info_layout://个人设置
                jumpByUserState(null, null);
                break;
            case R.id.mine_group_voucher_layout://团购券
                jumpByUserState(MyCouponActivity.class, null);
                break;
            case R.id.mine_point_layout://积分列表
                jumpByUserState(PointRecordActivity.class, null);
                break;
            case R.id.mine_lottery_layout://抵用券列表
                jumpByUserState(LotteryRecordActivity.class, null);
                break;
            case R.id.mine_my_collection_btn://收藏
                jumpByUserState(CollectionActivity.class, null);
                break;
            case R.id.mine_my_remark_btn://评论
                jumpByUserState(MyRemarkActivity.class, null);
                break;
            case R.id.mine_my_like_btn://我的喜爱
                jumpByUserState(null, null);
                break;
            case R.id.mine_my_setting_btn://个人设置
                infoLayout.performClick();
                break;
            case R.id.wait_pay_rv://待支付订单
                jumpOrderList(OrderStateEnum.WAIT_PAY);
                break;
            case R.id.wait_use_rv://待使用订单
                jumpOrderList(OrderStateEnum.WAIT_USE);
                break;
            case R.id.wait_remark_rv://待评论订单
                jumpOrderList(OrderStateEnum.USED);
                break;
            case R.id.finish_rv://已完成订单
                jumpOrderList(OrderStateEnum.REMARKED);
                break;
        }
    }

    private void jumpOrderList(OrderStateEnum stateEnum) {
        Intent intent = new Intent();
        intent.putExtra(OrderListActivity.ORDER_STATE_KEY, stateEnum);
        jumpByUserState(OrderListActivity.class, intent);
    }

    private void jumpByUserState(@Nullable Class<? extends Activity> userTarget, Intent intent) {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null) {//未登录
            //跳转到登录界面
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {//登录
            if (userTarget != null) {//有要跳转的界面
                if (intent == null)
                    intent = new Intent();
                intent.setClass(getActivity(), userTarget);
                startActivity(intent);
            }
        }
    }

}
