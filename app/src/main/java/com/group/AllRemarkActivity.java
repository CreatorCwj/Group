package com.group;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.adapter.base.BaseFragmentPagerAdapter;
import com.avos.avoscloud.AVQuery;
import com.fragment.ImagesRemarkListFragment;
import com.fragment.RemarkListFragment;
import com.group.base.BaseFragmentActivity;
import com.model.Merchant;
import com.model.Remark;
import com.model.Voucher;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by cwj on 16/4/11.
 * 商家/商品评论列表页
 */
@ContentView(R.layout.activity_all_remark)
public class AllRemarkActivity extends BaseFragmentActivity {

    public static final String MERCHANT_KEY = "merchant";
    public static final String VOUCHER_KEY = "voucher";

    @InjectView(R.id.all_remark_tabLayout)
    private TabLayout tabLayout;

    @InjectView(R.id.all_remark_viewPager)
    private ViewPager viewPager;

    private List<RemarkListFragment> list;
    private BaseFragmentPagerAdapter<RemarkListFragment> pagerAdapter;

    private Merchant merchant;
    private Voucher voucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        initViewPager();
        initTabLayout();
        setQuery();
    }

    private void receiveIntent() {
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
        voucher = getIntent().getParcelableExtra(VOUCHER_KEY);
    }

    private void setQuery() {
        for (RemarkListFragment fragment : list) {
            fragment.setQuery(getQuery());
        }
    }

    private AVQuery<Remark> getQuery() {
        if (merchant == null && voucher == null)
            return null;
        if (merchant != null) {//商家评论
            AVQuery<Voucher> voucherQuery = AVQuery.getQuery(Voucher.class);
            voucherQuery.whereEqualTo(Voucher.MERCHANT, merchant);//该商家的优惠券
            AVQuery<Remark> query = AVQuery.getQuery(Remark.class);
            query.whereMatchesQuery(Remark.VOUCHER, voucherQuery);//这些优惠券的评论
            query.orderByDescending(Remark.UPDATED_AT);//更新评论时间排序
            query.include(Remark.USER);
            query.include(Remark.VOUCHER);
            query.include(Remark.IMAGES);
            return query;
        } else {//voucher评论
            AVQuery<Remark> query = AVQuery.getQuery(Remark.class);
            query.whereEqualTo(Remark.VOUCHER, voucher);
            query.orderByDescending(Remark.UPDATED_AT);//更新评论时间排序
            query.include(Remark.USER);
            query.include(Remark.VOUCHER);
            query.include(Remark.IMAGES);
            return query;
        }
    }

    private void initViewPager() {
        list = new ArrayList<>();
        list.add(new RemarkListFragment());
        list.add(new ImagesRemarkListFragment());
        pagerAdapter = new BaseFragmentPagerAdapter<>(this, viewPager, list);
    }

    private void initTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }
}
