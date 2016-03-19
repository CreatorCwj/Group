package com.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fragment.base.BaseFragment;
import com.group.R;
import com.util.Utils;
import com.widget.FunctionButton;
import com.widget.RoundImageView;

import roboguice.inject.InjectView;

public class MineFragment extends BaseFragment {

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

    @InjectView(R.id.mine_my_order_btn)
    private FunctionButton myOrderBtn;

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
        setUser();
        setLayout();
        setFunction();
    }

    private void setLayout() {
        voucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(getActivity(), voucherTv.getText().toString() + "张团购券");
            }
        });
    }

    private void setFunction() {
        myOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(getActivity(), "我的订单");
            }
        });
    }

    private void setUser() {
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(getActivity(), "请登录");
            }
        });
    }
}
