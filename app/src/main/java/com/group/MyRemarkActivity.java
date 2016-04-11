package com.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adapter.MyRemarkAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.constant.GradeEnum;
import com.group.base.BaseAVQueryListActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeCountCallback;
import com.model.Merchant;
import com.model.Remark;
import com.model.User;
import com.model.Voucher;
import com.widget.RoundImageView;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.viewHolder.HeaderViewHolder;

/**
 * Created by cwj on 16/4/11.
 * 我的评论列表页(有头部)
 */
public class MyRemarkActivity extends BaseAVQueryListActivity<Remark> {

    private MyRemarkHeader header;

    private SafeCountCallback safeCountCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置头部
        addHeader();
        setHeaderInfo();
    }

    private void addHeader() {
        header = new MyRemarkHeader(this, R.layout.my_remark_header);
        rlrView.addHeader(header);
    }

    private void setHeaderInfo() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            ImageLoader.displayImage(header.img, user.getImageUrl());
            header.lvTv.setText("Lv" + GradeEnum.getGradeByValue(user.getGrowthValue()).getGrade());
            if (TextUtils.isEmpty(user.getDisplayName())) {//没有昵称用username
                header.nameTv.setText(user.getUsername());
            } else {
                header.nameTv.setText(user.getDisplayName());
            }
        } else {
            header.img.setImageResource(R.drawable.no_user_icon);
            header.lvTv.setText("Lv0");
            header.nameTv.setText("");
        }
    }

    private void loadRemarkNum() {
        if (safeCountCallback != null)
            safeCountCallback.cancel();
        AVQuery<Remark> query = getQuery();
        if (query == null) {//无用户
            header.remarkNumTv.setText("0");
        } else {//load
            safeCountCallback = new SafeCountCallback(this) {
                @Override
                public void getCount(int i, AVException e) {
                    if (e != null) {//error
                        header.remarkNumTv.setText("0");
                    } else {
                        header.remarkNumTv.setText("" + i);
                    }
                }
            };
            query.countInBackground(safeCountCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyRemarkAdapter.UPD_REMARK_CODE) {//更新评论成功,刷新
                rlrView.refresh();
            }
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        //加载评论数
        loadRemarkNum();
    }

    @Override
    protected RecyclerViewAdapter<Remark> getAdapter() {
        return new MyRemarkAdapter(this);
    }

    @Override
    protected String getTitleText() {
        return "我的评论";
    }

    @Override
    protected AVQuery<Remark> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        AVQuery<Remark> query = AVQuery.getQuery(Remark.class);
        query.whereEqualTo(Remark.USER, AVUser.getCurrentUser());
        query.orderByDescending(Remark.CREATED_AT);//创建时间排序
        query.include(Remark.IMAGES);
        query.include(Remark.VOUCHER);
        query.include(Remark.VOUCHER + "." + Voucher.MERCHANT);
        query.include(Remark.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.AREA);
        query.include(Remark.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_AREA);
        query.include(Remark.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.CATEGORY);
        query.include(Remark.VOUCHER + "." + Voucher.MERCHANT + "." + Merchant.SUB_CATEGORY);
        return query;
    }

    public class MyRemarkHeader extends HeaderViewHolder {

        RoundImageView img;
        TextView lvTv;
        TextView nameTv;
        TextView remarkNumTv;

        public MyRemarkHeader(Context context, int layoutId) {
            super(context, layoutId);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            img = (RoundImageView) itemView.findViewById(R.id.my_remark_header_img);
            lvTv = (TextView) itemView.findViewById(R.id.my_remark_header_lv_tv);
            nameTv = (TextView) itemView.findViewById(R.id.my_remark_header_name_tv);
            remarkNumTv = (TextView) itemView.findViewById(R.id.my_remark_header_remark_num_tv);
        }

    }
}
