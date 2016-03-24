package com.group;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adapter.MerchantAdapter;
import com.adapter.RemarkAdapter;
import com.adapter.VoucherAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeCountCallback;
import com.leancloud.SafeFindCallback;
import com.leancloud.SafeGetCallback;
import com.model.Merchant;
import com.model.Remark;
import com.model.Voucher;
import com.util.Utils;
import com.widget.AdapterLinearLayout;
import com.widget.CustomToolBar;
import com.widget.dialog.SelectDialog;
import com.widget.pullToZoomView.PullToZoomScrollViewEx;
import com.widget.viewPagers.imageViewPager.PointView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_merchant_detail)
public class MerchantDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String MERCHANT_KEY = "merchant";

    @InjectView(R.id.merchant_detail_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.merchant_detail_scrollView)
    private PullToZoomScrollViewEx scrollView;

    @InjectView(R.id.merchant_detail_iv)
    private ImageView imageView;

    @InjectView(R.id.merchant_detail_pv)
    private PointView pointView;

    @InjectView(R.id.merchant_detail_name)
    private TextView nameTv;

    @InjectView(R.id.merchant_detail_rating)
    private RatingBar ratingBar;

    @InjectView(R.id.merchant_detail_point)
    private TextView pointTv;

    @InjectView(R.id.merchant_detail_average)
    private TextView averageTv;

    @InjectView(R.id.merchant_detail_address_layout)
    private LinearLayout addressLayout;

    @InjectView(R.id.merchant_detail_address_tv)
    private TextView addressTv;

    @InjectView(R.id.merchant_detail_call)
    private ImageView callIv;

    @InjectView(R.id.merchant_detail_voucher_view)
    private AdapterLinearLayout<Voucher> voucherView;

    @InjectView(R.id.merchant_detail_remark_rating)
    private RatingBar remarkRating;

    @InjectView(R.id.merchant_detail_remark_point)
    private TextView remarkPointTv;

    @InjectView(R.id.merchant_detail_remarkView)
    private AdapterLinearLayout<Remark> remarkView;

    @InjectView(R.id.merchant_detail_all_remark_tv)
    private TextView allRemarkTv;

    @InjectView(R.id.merchant_detail_services_tv)
    private TextView servicesTv;

    @InjectView(R.id.merchant_detail_nearby_view)
    private AdapterLinearLayout<Merchant> nearbyView;

    private VoucherAdapter voucherAdapter;
    private RemarkAdapter remarkAdapter;
    private MerchantAdapter merchantAdapter;

    private Merchant merchant;

    private SelectDialog callDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setToolbar();
        setListener();
        setInfo();
    }

    private void setListener() {
        imageView.setOnClickListener(this);
        pointView.setOnClickListener(this);//click to view image
        addressLayout.setOnClickListener(this);//地址
        callIv.setOnClickListener(this);//电话
        allRemarkTv.setOnClickListener(this);//全部评论
    }

    private void receiveIntent() {
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
    }

    private void setToolbar() {
        setAlpha();
        toolBar.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(MerchantDetailActivity.this, "分享");
            }
        });
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要根据是否已经收藏进行处理
                Utils.showToast(MerchantDetailActivity.this, "收藏");
            }
        });
    }

    private void setAlpha() {
        setAlpha(0);//一开始为透明,文字不显示
        scrollView.setOnScrollToTopListener(new PullToZoomScrollViewEx.OnScrollToTopListener() {
            @Override
            public void onScrollToTop(int moved, int total) {
                if (moved < 0)//向下滑不变
                    return;
                int realTotal = total - toolBar.getHeight();
                int alpha = (int) (((float) moved / realTotal) * 255);
                if (alpha > 255)//防止越界,小于0不会发生
                    alpha = 255;
                setAlpha(alpha);
            }
        });
    }

    private void setAlpha(int alpha) {
        if (toolBar.getBackground().getAlpha() != alpha) {
            toolBar.getBackground().setAlpha(alpha);
        }
        int textColor = Color.argb(alpha, 255, 255, 255);
        if (toolBar.getTitleTextView().getCurrentTextColor() != textColor) {
            toolBar.getTitleTextView().setTextColor(textColor);
        }
    }

    private void setInfo() {
        if (merchant != null) {
            //title
            toolBar.setTitleText(merchant.getName());
            nameTv.setText(merchant.getName());
            //image
            pointView.setText(merchant.getImages().size() + "张");//imagesNum
            if (merchant.getImages().size() > 0)//load first img
                ImageLoader.displayImage(imageView, merchant.getImages().get(0));
            //point
            ratingBar.setRating((float) merchant.getPoint());
            pointTv.setText(ratingBar.getRating() + "分");
            //average
            averageTv.setText("人均¥" + merchant.getAverage());
            //address
            addressTv.setText(merchant.getAddress());
            //voucher
            loadVoucher();
            //remark
            setRemark();
            //services
            String services = Utils.getListStr(merchant.getServices(), "    ");
            if (TextUtils.isEmpty(services)) {//无服务不显示
                servicesTv.setVisibility(View.GONE);
            } else {
                servicesTv.setVisibility(View.VISIBLE);
                servicesTv.setText(services);
            }
            //nearby group
            loadNearby();
        }
    }

    private void loadNearby() {
        merchantAdapter = new MerchantAdapter(this);
        nearbyView.setAdapter(merchantAdapter);
        nearbyView.setOnItemClickListener(merchantAdapter);
        //load
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereNear(Merchant.LOCATION, merchant.getLocation());//附近的(排好序的)
        query.limit(10);
        query.include(Merchant.CATEGORY);
        query.include(Merchant.SUB_CATEGORY);
        query.include(Merchant.AREA);
        query.include(Merchant.SUB_AREA);
        query.findInBackground(new SafeFindCallback<Merchant>(this) {
            @Override
            public void findResult(List<Merchant> objects, AVException e) {
                if (e != null || objects == null || objects.size() <= 0) {//有错或空数据不显示
                    nearbyView.clearData();
                    return;
                }
                //reset到adapter里
                for (Merchant tmp : objects) {//取消重复当前的商家(附近肯定包含当前的)
                    if (tmp.getObjectId().equals(merchant.getObjectId())) {
                        objects.remove(tmp);
                        break;
                    }
                }
                nearbyView.resetData(objects);
            }
        });
    }

    private void setRemark() {
        //view
        remarkRating.setRating((float) merchant.getPoint());
        remarkPointTv.setText(remarkRating.getRating() + "分");
        //adapter view
        remarkAdapter = new RemarkAdapter(this);
        remarkView.setAdapter(remarkAdapter);
        loadRemark();
    }

    private void loadRemark() {
        AVQuery<Voucher> voucherQuery = AVQuery.getQuery(Voucher.class);
        voucherQuery.whereEqualTo(Voucher.MERCHANT, merchant);//该商家的优惠券
        AVQuery<Remark> mainQuery = AVQuery.getQuery(Remark.class);
        mainQuery.whereMatchesQuery(Remark.VOUCHER, voucherQuery);//这些优惠券的评论
        mainQuery.orderByDescending(Remark.UPDATED_AT);//更新评论时间排序
        mainQuery.include(Remark.USER);
        mainQuery.include(Remark.VOUCHER);
        mainQuery.include(Remark.IMAGES);
        mainQuery.getFirstInBackground(new SafeGetCallback<Remark>(this) {
            @Override
            public void getResult(Remark object, AVException e) {
                if (e != null || object == null) {
                    Utils.showToast(MerchantDetailActivity.this, "获取评论失败");
                } else {
                    List<Remark> objs = new ArrayList<>();
                    objs.add(object);
                    remarkView.resetData(objs);
                }
            }
        });
        mainQuery.countInBackground(new SafeCountCallback(this) {
            @Override
            public void getCount(int i, AVException e) {
                int count = i;
                if (e != null) {
                    count = 0;
                }
                allRemarkTv.setText("查看全部" + count + "条评论");
            }
        });
    }

    private void loadVoucher() {
        voucherAdapter = new VoucherAdapter(this);
        voucherView.setAdapter(voucherAdapter);
        AVQuery<Voucher> query = AVQuery.getQuery(Voucher.class);
        query.whereEqualTo(Voucher.MERCHANT, merchant);
        query.orderByDescending(Voucher.TAG);//降序排列,满减券在团购券前面
        query.include(Voucher.MERCHANT);
        query.include(Voucher.REWARD_LOTTERY);
        query.include(Voucher.REWARD_POINT);
        query.findInBackground(new SafeFindCallback<Voucher>(this) {
            @Override
            public void findResult(List<Voucher> objects, AVException e) {
                if (e != null || objects == null) {//error
                    Utils.showToast(MerchantDetailActivity.this, "获取优惠券失败");
                } else {
                    voucherView.resetData(objects);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_detail_iv:
            case R.id.merchant_detail_pv://打开大图模式
                openBigBitmap();
                break;
            case R.id.merchant_detail_address_layout://进入地图
                Utils.showToast(MerchantDetailActivity.this, "地图");
                break;
            case R.id.merchant_detail_call://打电话(选择)
                call();
                break;
            case R.id.merchant_detail_all_remark_tv:
                Utils.showToast(MerchantDetailActivity.this, "全部评论");
                break;
        }
    }

    private void call() {
        if (merchant == null || merchant.getPhones() == null || merchant.getPhones().size() <= 0)
            return;
        if (callDialog == null) {
            callDialog = new SelectDialog(this, merchant.getPhones());
            callDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String phone = callDialog.getItems().get(position);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });
        }
        callDialog.show();
    }

    private void openBigBitmap() {
        if (merchant == null)
            return;
        Intent intent = new Intent(MerchantDetailActivity.this, BigBitmapActivity.class);
        ArrayList<String> urls = new ArrayList<>();
        for (String url : merchant.getImages())
            urls.add(url);
        intent.putStringArrayListExtra(BigBitmapActivity.INTENT_URL_KEY, urls);
        startActivity(intent);
    }
}
