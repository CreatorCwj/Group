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
import com.avos.avoscloud.AVUser;
import com.dao.generate.City;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeCountCallback;
import com.leancloud.SafeDeleteCallback;
import com.leancloud.SafeFindCallback;
import com.leancloud.SafeGetCallback;
import com.leancloud.SafeSaveCallback;
import com.model.Collection;
import com.model.Merchant;
import com.model.Remark;
import com.model.User;
import com.model.Voucher;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.AdapterLinearLayout;
import com.widget.CustomToolBar;
import com.widget.dialog.SelectDialog;
import com.widget.dialog.ShareDialog;
import com.widget.pullToZoomView.PullToZoomScrollViewEx;
import com.widget.viewPagers.imageViewPager.PointView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_merchant_detail)
public class MerchantDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String MERCHANT_ID_KEY = "merchantId";//推送用
    public static final String MERCHANT_KEY = "merchant";

    public static final int LOGIN_CODE = 1;

    @InjectView(R.id.merchant_detail_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.merchant_detail_scrollView)
    private PullToZoomScrollViewEx scrollView;

    @InjectView(R.id.detail_iv)
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
    private ShareDialog shareDialog;

    private Boolean hasCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String merchantId = getIntent().getStringExtra(MERCHANT_ID_KEY);
        if (!TextUtils.isEmpty(merchantId)) {//需要加载的,推送用
            showLoadingDialog("加载商家信息...");
            AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
            query.include(Merchant.CATEGORY);
            query.include(Merchant.SUB_CATEGORY);
            query.include(Merchant.AREA);
            query.include(Merchant.SUB_AREA);
            query.getInBackground(merchantId, new SafeGetCallback<Merchant>(this) {
                @Override
                public void getResult(Merchant object, AVException e) {
                    if (e != null) {
                        Utils.showToast(MerchantDetailActivity.this, "商家信息加载失败");
                        finish();
                    } else {
                        merchant = object;
                        setToolbar();
                        setListener();
                        setInfo();
                    }
                    cancelLoadingDialog();
                }
            });
        } else {//直接设置即可
            receiveIntent();
            setToolbar();
            setListener();
            setInfo();
        }
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
                //分享
                shareToWx();
            }
        });
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要根据是否已经收藏进行处理
                handleCollection();
            }
        });
        loadIfCollected();//加载是否收藏过(根据此来设置收藏功能)
    }

    private void shareToWx() {
        if (merchant == null)
            return;
        if (shareDialog == null) {
            shareDialog = new ShareDialog(this, "http://tj.meituan.com/", "我发现 " + merchant.getName() + " 不错", "地址:" + merchant.getAddress());
        }
        shareDialog.show();
    }

    private void handleCollection() {
        if (merchant == null)
            return;
        if (AVUser.getCurrentUser() == null) {//跳到登陆界面
            startActivityForResult(new Intent(MerchantDetailActivity.this, LoginActivity.class), LOGIN_CODE);
            return;
        }
        //根据是否收藏过做处理
        if (hasCollected == null)
            return;
        if (hasCollected) {//收藏过,删除收藏
            showLoadingDialog("取消收藏...");
            getCollectionQuery().deleteAllInBackground(new SafeDeleteCallback(this) {
                @Override
                protected void delete(AVException e) {
                    if (e != null) {
                        Utils.showToast(MerchantDetailActivity.this, "取消收藏失败");
                    } else {
                        Utils.showToast(MerchantDetailActivity.this, "已取消收藏");
                        updCollectionState(false);
                    }
                    cancelLoadingDialog();
                }
            });
        } else {//增加收藏
            showLoadingDialog("添加收藏...");
            Collection collection = new Collection();
            collection.setMerchant(merchant);
            collection.setUser(AVUser.getCurrentUser(User.class));
            collection.saveInBackground(new SafeSaveCallback(this) {
                @Override
                public void save(AVException e) {
                    if (e != null) {
                        Utils.showToast(MerchantDetailActivity.this, "收藏失败");
                    } else {
                        Utils.showToast(MerchantDetailActivity.this, "已收藏");
                        updCollectionState(true);
                    }
                    cancelLoadingDialog();
                }
            });
        }
    }

    private void loadIfCollected() {
        if (merchant == null || AVUser.getCurrentUser() == null)
            return;
        //查询有没有收藏记录
        getCollectionQuery().countInBackground(new SafeCountCallback(this) {
            @Override
            public void getCount(int i, AVException e) {
                if (e == null) {
                    updCollectionState(i > 0);//标志位标识是否已经收藏
                }
            }
        });
    }

    private void updCollectionState(boolean hasCollected) {
        this.hasCollected = hasCollected;
        //img
        if (hasCollected) {
            toolBar.setRightIcon(R.drawable.collected_icon);
        } else {
            toolBar.setRightIcon(R.drawable.collect_icon);
        }
    }

    private AVQuery<Collection> getCollectionQuery() {
        AVQuery<Collection> query = AVQuery.getQuery(Collection.class);
        query.whereEqualTo(Collection.MERCHANT, merchant);
        query.whereEqualTo(Collection.USER, AVUser.getCurrentUser());
        return query;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGIN_CODE) {//登陆成功了
                loadIfCollected();//再次请求收藏记录,设置收藏功能
            }
        }
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
            if (merchant.getImages() != null) {
                pointView.setText(merchant.getImages().size() + "张");//imagesNum
                if (merchant.getImages().size() > 0)//load first img
                    ImageLoader.displayImage(imageView, merchant.getImages().get(0));
            }
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
        City city = AppSetting.getCity();
        if (city != null)//加上城市筛选更准确
            query.whereEqualTo(Merchant.CITY_ID, city.getCityId());
        query.whereNear(Merchant.LOCATION, merchant.getLocation());//附近的(排好序的)
        //取消当前重复的商家
        query.whereNotEqualTo(Merchant.OBJECT_ID, merchant.getObjectId());
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
                    remarkView.clearData();
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
        voucherAdapter.setMerchant(merchant);
        voucherView.setAdapter(voucherAdapter);
        voucherView.setOnItemClickListener(voucherAdapter);
        AVQuery<Voucher> query = AVQuery.getQuery(Voucher.class);
        query.whereEqualTo(Voucher.MERCHANT, merchant);
        query.orderByDescending(Voucher.TAG);//降序排列,满减券在团购券前面
        query.include(Voucher.MERCHANT);
        query.include(Voucher.REWARD_LOTTERY);
        query.include(Voucher.REWARD_POINT);
        query.findInBackground(new SafeFindCallback<Voucher>(this) {
            @Override
            public void findResult(List<Voucher> objects, AVException e) {
                if (e != null || objects == null || objects.size() <= 0) {//错误或空数据清空
                    voucherView.clearData();
                } else {
                    voucherView.resetData(objects);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (merchant == null)
            return;
        switch (v.getId()) {
            case R.id.detail_iv:
            case R.id.merchant_detail_pv://打开大图模式
                openBigBitmap();
                break;
            case R.id.merchant_detail_address_layout://进入地图
                gotoMap();
                break;
            case R.id.merchant_detail_call://打电话(选择)
                call();
                break;
            case R.id.merchant_detail_all_remark_tv:
                gotoAllRemark();
                break;
        }
    }

    private void gotoMap() {
        Intent intent = new Intent(MerchantDetailActivity.this, MapMerchantActivity.class);
        intent.putExtra(MapMerchantActivity.MERCHANT_KEY, merchant);
        startActivity(intent);
    }

    private void gotoAllRemark() {
        Intent intent = new Intent(MerchantDetailActivity.this, AllRemarkActivity.class);
        intent.putExtra(AllRemarkActivity.MERCHANT_KEY, merchant);
        startActivity(intent);
    }

    private void call() {
        if (merchant.getPhones() == null || merchant.getPhones().size() <= 0)
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
        if (merchant.getImages() == null || merchant.getImages().size() <= 0)//无图不打开
            return;
        Intent intent = new Intent(MerchantDetailActivity.this, BigBitmapActivity.class);
        ArrayList<String> urls = new ArrayList<>();
        for (String url : merchant.getImages())
            urls.add(url);
        intent.putStringArrayListExtra(BigBitmapActivity.INTENT_URL_KEY, urls);
        startActivity(intent);
    }
}
