package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adapter.MerchantAdapter;
import com.adapter.RemarkAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.dao.generate.City;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeCountCallback;
import com.leancloud.SafeFindCallback;
import com.leancloud.SafeGetCallback;
import com.model.Merchant;
import com.model.Remark;
import com.model.Voucher;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.AdapterLinearLayout;
import com.widget.CustomToolBar;
import com.widget.pullToZoomView.PullToZoomScrollViewEx;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_voucher_detail)
public class VoucherDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String VOUCHER_KEY = "voucher";
    public static final String MERCHANT_KEY = "merchant";

    @InjectView(R.id.voucher_detail_scrollView)
    private PullToZoomScrollViewEx scrollView;

    @InjectView(R.id.voucher_detail_top_view)
    private LinearLayout topView;

    @InjectView(R.id.voucher_detail_top_cur_price_tv)
    private TextView topCurPriceTv;

    @InjectView(R.id.voucher_detail_top_ori_price_tv)
    private TextView topOriPriceTv;

    @InjectView(R.id.voucher_detail_top_purchase_tv)
    private TextView topPurchaseTv;

    @InjectView(R.id.voucher_detail_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.detail_iv)
    private ImageView imageView;

    @InjectView(R.id.voucher_detail_merchant_name_tv)
    private TextView mNameTv;

    @InjectView(R.id.voucher_detail_name_tv)
    private TextView vNameTv;

    @InjectView(R.id.voucher_detail_cur_price_tv)
    private TextView curPriceTv;

    @InjectView(R.id.voucher_detail_ori_price_tv)
    private TextView oriPriceTv;

    @InjectView(R.id.voucher_detail_purchase_tv)
    private TextView purchaseTv;

    @InjectView(R.id.voucher_detail_sellNum_tv)
    private TextView sellNumTv;

    @InjectView(R.id.voucher_detail_send_lottery_tv)
    private TextView sendLotteryTv;

    @InjectView(R.id.voucher_detail_send_point_tv)
    private TextView sendPointTv;

    @InjectView(R.id.voucher_detail_describe_tv)
    private TextView describeTv;

    @InjectView(R.id.voucher_detail_suggest_tv)
    private TextView suggestTv;

    @InjectView(R.id.voucher_detail_remark_rating)
    private RatingBar ratingBar;

    @InjectView(R.id.voucher_detail_point_tv)
    private TextView pointTv;

    @InjectView(R.id.voucher_detail_remarkView)
    private AdapterLinearLayout<Remark> remarkView;

    @InjectView(R.id.voucher_detail_all_remark_tv)
    private TextView allRemarkTv;

    @InjectView(R.id.voucher_detail_relevant_view)
    private AdapterLinearLayout<Merchant> relevantView;

    private Voucher voucher;
    private Merchant merchant;

    private RemarkAdapter remarkAdapter;
    private MerchantAdapter merchantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveIntent();
        setToolbar();
        setListener();
        setInfo();
    }

    private void receiveIntent() {
        voucher = getIntent().getParcelableExtra(VOUCHER_KEY);
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
    }

    private void setToolbar() {
        setTopView();
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(VoucherDetailActivity.this, "分享");
            }
        });
    }

    private void setTopView() {
        scrollView.setOnScrollToTopListener(new PullToZoomScrollViewEx.OnScrollToTopListener() {
            @Override
            public void onScrollToTop(int moved, int total) {
                if (moved >= total && topView.getVisibility() != View.VISIBLE)
                    topView.setVisibility(View.VISIBLE);
                else if (moved < total && topView.getVisibility() != View.GONE)
                    topView.setVisibility(View.GONE);
            }
        });
    }

    private void setListener() {
        imageView.setOnClickListener(this);
        purchaseTv.setOnClickListener(this);
        topPurchaseTv.setOnClickListener(this);
        allRemarkTv.setOnClickListener(this);
    }

    private void setInfo() {
        if (voucher != null && merchant != null) {
            //load image
            loadImage();
            //set header text
            mNameTv.setText(merchant.getName());
            vNameTv.setText(voucher.getName());
            //set price
            curPriceTv.setText("" + voucher.getCurrentPrice());
            oriPriceTv.setText("¥" + voucher.getOriginPrice());
            topCurPriceTv.setText("" + voucher.getCurrentPrice());
            topOriPriceTv.setText("¥" + voucher.getOriginPrice());
            //sellNum
            sellNumTv.setText("已售" + voucher.getSellNum());
            //send reward
            setReward();
            //套餐
            String describe = voucher.getDescribe();
            if (!TextUtils.isEmpty(describe)) {
                describe = getContentString(describe).toString();
            }
            describeTv.setText(describe);
            //购买须知
            suggestTv.setText(getSuggest());
            //remark
            setRemark();
            //relevant
            loadRelevant();
        }
    }

    private void loadRelevant() {
        merchantAdapter = new MerchantAdapter(this);
        relevantView.setAdapter(merchantAdapter);
        relevantView.setOnItemClickListener(merchantAdapter);
        //load
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        City city = AppSetting.getCity();
        if (city != null)//加上城市筛选更准确
            query.whereEqualTo(Merchant.CITY_ID, city.getCityId());
        //根据商家品类来查找相应品类的商家进行推荐
        if (merchant.getCategory() != null) {//有父品类
            query.whereEqualTo(Merchant.CATEGORY, merchant.getCategory());
        }
        if (merchant.getSubCategory() != null) {//有子品类
            query.whereEqualTo(Merchant.SUB_CATEGORY, merchant.getSubCategory());
        }
        //取消当前重复的商家
        query.whereNotEqualTo(Merchant.OBJECT_ID, merchant.getObjectId());
        query.orderByDescending(Merchant.POINT);//按评分排序
        query.limit(10);
        query.include(Merchant.CATEGORY);
        query.include(Merchant.SUB_CATEGORY);
        query.include(Merchant.AREA);
        query.include(Merchant.SUB_AREA);
        query.findInBackground(new SafeFindCallback<Merchant>(this) {
            @Override
            public void findResult(List<Merchant> objects, AVException e) {
                if (e != null || objects == null || objects.size() <= 0) {//有错或空数据不显示
                    relevantView.clearData();
                    return;
                }
                //reset到adapter里
                relevantView.resetData(objects);
            }
        });
    }

    private void setRemark() {
        //view
        ratingBar.setRating((float) voucher.getPoint());
        pointTv.setText(ratingBar.getRating() + "分");
        //adapter view
        remarkAdapter = new RemarkAdapter(this);
        remarkView.setAdapter(remarkAdapter);
        loadRemark();
    }

    private void loadRemark() {
        AVQuery<Remark> query = AVQuery.getQuery(Remark.class);
        query.whereEqualTo(Remark.VOUCHER, voucher);
        query.orderByDescending(Remark.UPDATED_AT);//更新评论时间排序
        query.include(Remark.USER);
        query.include(Remark.VOUCHER);
        query.include(Remark.IMAGES);
        query.getFirstInBackground(new SafeGetCallback<Remark>(this) {
            @Override
            public void getResult(Remark object, AVException e) {
                if (e != null || object == null) {
                    remarkView.clearData();
                } else {
                    List<Remark> objs = new ArrayList<>();
                    objs.add(object);
                    remarkView.resetData(objs);
                }
            }
        });
        query.countInBackground(new SafeCountCallback(this) {
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

    private SpannableStringBuilder getSuggest() {
        String suggest = voucher.getSuggest();
        if (TextUtils.isEmpty(suggest))
            return new SpannableStringBuilder("");
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        String[] split = suggest.split("#");//几项
        for (int i = 0; i < split.length; i++) {
            SpannableStringBuilder itemStr = getItemString(split[i]);
            if (!TextUtils.isEmpty(itemStr)) {
                ssb.append(itemStr);
                if (i != split.length - 1)//最后一项不换行
                    ssb.append("\n\n");
            }
        }
        return ssb;
    }

    private SpannableStringBuilder getItemString(String itemStr) {
        int index = itemStr.indexOf(":");
        if (index == -1)
            return getContentString(itemStr);
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        ssb.append(getTitleString(itemStr.substring(0, index + 1))).append("\n\n").append(getContentString(itemStr.substring(index + 1)));
        return ssb;
    }

    private SpannableStringBuilder getContentString(String contentStr) {
        contentStr = contentStr.replaceFirst("@", "•   ").replaceAll("@", "\n\n•   ");
        SpannableStringBuilder ssb = new SpannableStringBuilder(contentStr);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, contentStr.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private SpannableStringBuilder getTitleString(String titleStr) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(titleStr);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, titleStr.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private void setReward() {
        if (voucher.getRewardLottery() != null) {//有送满减券
            ((ViewGroup) sendLotteryTv.getParent()).setVisibility(View.VISIBLE);
            sendLotteryTv.setVisibility(View.VISIBLE);
            sendLotteryTv.setText("送满减券:满" + voucher.getRewardLottery().getPriceToUse() + "减" + voucher.getRewardLottery().getPrice());
        }
        if (voucher.getRewardPoint() != null) {//有送积分
            ((ViewGroup) sendLotteryTv.getParent()).setVisibility(View.VISIBLE);
            sendPointTv.setVisibility(View.VISIBLE);
            sendPointTv.setText("送积分:" + voucher.getRewardPoint().getPoint());
        }
    }

    private void loadImage() {
        //voucher有加载voucher的,否则加载merchant的(如果有的话)
        if (voucher.getImages() != null && voucher.getImages().size() > 0) {
            ImageLoader.displayImage(imageView, voucher.getImages().get(0));
        } else if (merchant.getImages() != null && merchant.getImages().size() > 0) {
            ImageLoader.displayImage(imageView, merchant.getImages().get(0));
        }
    }

    @Override
    public void onClick(View v) {
        if (voucher == null || merchant == null)
            return;
        switch (v.getId()) {
            case R.id.detail_iv://看图
                openBigBitmap();
                break;
            case R.id.voucher_detail_purchase_tv://抢购
            case R.id.voucher_detail_top_purchase_tv:
                enterToSubmitOrder();
                break;
            case R.id.voucher_detail_all_remark_tv://全部评论
                gotoAllRemark();
                break;
        }
    }

    private void gotoAllRemark() {
        Intent intent = new Intent(VoucherDetailActivity.this, AllRemarkActivity.class);
        intent.putExtra(AllRemarkActivity.VOUCHER_KEY, voucher);
        startActivity(intent);
    }

    private void enterToSubmitOrder() {
        Intent intent = new Intent(VoucherDetailActivity.this, SubmitOrderActivity.class);
        intent.putExtra(SubmitOrderActivity.VOUCHER_KEY, voucher);
        intent.putExtra(SubmitOrderActivity.MERCHANT_KEY, merchant);
        startActivity(intent);
    }

    private void openBigBitmap() {
        List<String> tmp = null;
        if (voucher.getImages() != null && voucher.getImages().size() > 0) {
            tmp = voucher.getImages();
        } else if (merchant.getImages() != null && merchant.getImages().size() > 0) {
            tmp = merchant.getImages();
        }
        if (tmp == null)
            return;
        Intent intent = new Intent(VoucherDetailActivity.this, BigBitmapActivity.class);
        ArrayList<String> urls = new ArrayList<>();
        for (String url : tmp)
            urls.add(url);
        intent.putStringArrayListExtra(BigBitmapActivity.INTENT_URL_KEY, urls);
        startActivity(intent);
    }
}
