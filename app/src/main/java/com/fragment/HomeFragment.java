package com.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.MerchantAdapter;
import com.adapter.ModelViewAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.dao.dbHelpers.AreaHelper;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.dbHelpers.CityHelper;
import com.dao.generate.Category;
import com.dao.generate.City;
import com.fragment.base.BaseFragment;
import com.google.inject.Inject;
import com.group.H5Activity;
import com.group.MerchantActivity;
import com.group.R;
import com.group.ScannerActivity;
import com.group.SelectCityActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeFindCallback;
import com.location.Location;
import com.location.OnLocationListener;
import com.model.Area;
import com.model.HotCategory;
import com.model.Merchant;
import com.model.User;
import com.util.AppSetting;
import com.util.DrawableUtils;
import com.util.Utils;
import com.widget.ActivityItemView;
import com.widget.AdapterLinearLayout;
import com.widget.CustomToolBar;
import com.widget.HotCategoryItemView;
import com.widget.dialog.MessageDialog;
import com.widget.rlrView.view.AutoRefreshSwipeView;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private final int SELECT_CITY = 1;

    @InjectView(R.id.home_custom_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.home_model_view)
    private RLRView modelView;

    @InjectView(R.id.activity_item1)
    private ActivityItemView activityItemView1;

    @InjectView(R.id.activity_item2)
    private ActivityItemView activityItemView2;

    @InjectView(R.id.activity_item3)
    private ActivityItemView activityItemView3;

    @InjectView(R.id.activity_item4)
    private ActivityItemView activityItemView4;

    @InjectView(R.id.hot_category_layout)
    private LinearLayout hotCategoryLayout;

    @InjectView(R.id.my_like_layout)
    private AdapterLinearLayout<Merchant> myLikeView;

    @InjectView(R.id.all_group_tv)
    private TextView allGroup;

    @InjectView(R.id.home_refresh_view)
    private AutoRefreshSwipeView refreshView;

    @InjectView(R.id.activityContainer)
    private LinearLayout activityContainer;

    @Inject
    private CityHelper cityHelper;

    @Inject
    private AreaHelper areaHelper;

    @Inject
    private CategoryHelper categoryHelper;

    private City city;

    private List<ActivityItemView> activities;
    private List<HotCategoryItemView> hotCategories;

    private MerchantAdapter merchantAdapter;

    private BDLocation bdLocation;

    private boolean activityLoaded;
    private boolean hotLoaded;
    private boolean myLikeLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //定一次位,看当前城市和选择城市是否一致,不一致提示是否切换
        location();
        //设置Title
        setTitleClick();
        //设置Search
        setSearch();
        //设置扫一扫
        setScanner();
        //加载modelView
        setModelView();
        //初始化活动view
        setActivities();
        //初始化热门品类
        setHotCategory();
        //初始化我的喜爱
        setMyLike();
        //设置查看全部团购
        setAllGroup();
        //设置刷新view
        setRefreshView();
    }

    private void setRefreshView() {
        refreshView.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        //刷新界面数据
        refresh();
    }

    private void setAllGroup() {
        allGroup.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.RectStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, getActivity().getResources().getColor(R.color.iconPressed))
                , new DrawableUtils.RectStateDrawable(new int[]{}, Color.WHITE)));
        allGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开商家搜索界面,传入null,相当于查询全部
                Intent intent = new Intent(getActivity(), MerchantActivity.class);
                intent.putExtra(MerchantFragment.INIT_CATEGORY_KEY, (Category) null);
                startActivity(intent);
            }
        });
    }

    private void setMyLike() {
        merchantAdapter = new MerchantAdapter(getActivity());
        myLikeView.setAdapter(merchantAdapter);
        myLikeView.setOnItemClickListener(merchantAdapter);
    }

    private void setHotCategory() {
        hotCategories = new ArrayList<>();
        for (int i = 0; i < hotCategoryLayout.getChildCount(); i++) {
            View view = hotCategoryLayout.getChildAt(i);
            if (view instanceof HotCategoryItemView)
                hotCategories.add((HotCategoryItemView) view);
        }
    }

    private void setActivities() {
        activities = new ArrayList<>();
        activities.add(activityItemView1);
        activities.add(activityItemView2);
        activities.add(activityItemView3);
        activities.add(activityItemView4);
    }

    private void setModelView() {
        modelView.setAdapter(new ModelViewAdapter(getActivity()));
        modelView.resetHeight();
    }

    private void location() {
        Location.requestLocation(getActivity(), new OnLocationListener() {
            @Override
            public void onPreExecute() {
                //不用重置
            }

            @Override
            public void onSuccess(BDLocation location) {
                bdLocation = location;//定位成功赋值
                if (!TextUtils.isEmpty(location.getCity())) {//缓存经纬度不可用
                    final City newCity = cityHelper.getByName(Utils.getRealCityName(location.getCity()));
                    //不相同时提示切换
                    if (newCity != null && (city == null || newCity.getCityId() != city.getCityId())) {
                        String tip = "当前定位城市为:" + newCity.getName() + "\n是否切换?";
                        MessageDialog dialog = new MessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage(tip);
                        dialog.setPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //切换城市并刷新
                                AppSetting.updCity(newCity);//update
                                refresh();//更新位置及其它信息
                            }
                        });
                        dialog.show();
                    }
                } else onFailed();
            }

            @Override
            public void onFailed() {
                bdLocation = null;//定位失败重置
            }

            @Override
            public void onFinally() {
                merchantAdapter.setBdLocation(bdLocation);
                myLikeView.notifyRefresh();
            }
        });
    }

    private void refresh() {
        resetLoadedFlag();//加载中变量重置
        //获取城市
        city = AppSetting.getCity();
        //title,根据城市获取商圈信息
        if (city != null) {
            toolBar.setTitleText(city.getName());
            loadArea();
        } else {
            toolBar.setTitleText("请选择城市");
        }
        //加载推荐活动
        loadActivities();
        //加载热门品类
        loadHotCategories();
        //加载我的喜爱
        loadMyLike();
    }

    private void resetLoadedFlag() {
        activityLoaded = hotLoaded = myLikeLoaded = false;
    }

    private void loadMyLike() {
        //user的likes在登录和更新(设置完我的喜爱)刷后新,此处用时不用再次fetch了
        List<Integer> likes = null;
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            likes = user.getLikes();
        }
        List<AVQuery<Merchant>> queries = new ArrayList<>();
        //第一个查询
        AVQuery<Merchant> firstQuery = AVQuery.getQuery(Merchant.class);
        queries.add(firstQuery);
        if (city != null)//当前城市
            firstQuery.whereEqualTo(Merchant.CITY_ID, city.getCityId());
        //有品类
        if (likes != null && likes.size() > 0) {
            //品类内部查询
            AVQuery<com.model.Category> innerQuery = AVQuery.getQuery(com.model.Category.class);
            innerQuery.whereContainedIn(com.model.Category.CATEGORY_ID, likes);
            //第一个查询的品类条件
            firstQuery.whereMatchesQuery(Merchant.CATEGORY, innerQuery);
            //第二个查询
            AVQuery<Merchant> secondQuery = AVQuery.getQuery(Merchant.class);
            queries.add(secondQuery);
            if (city != null)//当前城市
                secondQuery.whereEqualTo(Merchant.CITY_ID, city.getCityId());
            //第二个查询的子品类条件
            secondQuery.whereMatchesQuery(Merchant.SUB_CATEGORY, innerQuery);
        }
        //合并or查询,上限20条
        AVQuery<Merchant> mainQuery = AVQuery.or(queries);
        mainQuery.include(Merchant.CATEGORY);
        mainQuery.include(Merchant.SUB_CATEGORY);
        mainQuery.include(Merchant.AREA);
        mainQuery.include(Merchant.SUB_AREA);
        mainQuery.orderByDescending(Merchant.POINT);
        mainQuery.limit(20);
        mainQuery.findInBackground(new SafeFindCallback<Merchant>(getActivity()) {
            @Override
            public void findResult(List<Merchant> objects, AVException e) {
                myLikeLoaded = true;
                handler.sendEmptyMessage(0);//通知查看是否结束刷新
                if (e != null || objects == null || objects.size() <= 0) {//有错或空数据不显示
                    myLikeView.clearData();
                    return;
                }
                //reset到adapter里
                myLikeView.resetData(objects);
            }
        });
    }

    private void loadHotCategories() {
        AVQuery<HotCategory> query = AVQuery.getQuery(HotCategory.class);
        if (city != null)
            query.whereEqualTo(HotCategory.CITY_ID, city.getCityId());
        query.limit(hotCategories.size());
        query.orderByDescending(HotCategory.UPDATED_AT);
        query.findInBackground(new SafeFindCallback<HotCategory>(getActivity()) {
            @Override
            public void findResult(List<HotCategory> objects, AVException e) {
                hotLoaded = true;
                handler.sendEmptyMessage(0);//通知查看是否结束刷新
                if (e != null || objects == null || objects.size() <= 0) {//错误或空数据不显示
                    for (HotCategoryItemView itemView : hotCategories)
                        itemView.setVisibility(View.GONE);
                    return;
                }
                for (int i = 0; i < hotCategories.size(); i++) {
                    if (i >= objects.size()) {//有可能不足4个
                        hotCategories.get(i).setVisibility(View.GONE);
                        continue;
                    }
                    //设置view
                    hotCategories.get(i).setVisibility(View.VISIBLE);
                    final HotCategory hotCategory = objects.get(i);
                    HotCategoryItemView itemView = hotCategories.get(i);
                    itemView.setTitle(hotCategory.getTitle());
                    itemView.setSubTitle(hotCategory.getDescribe());
                    ImageLoader.displayImage(itemView.getImageView(), hotCategory.getImageUrl());
                    //点击调到商家查看页面了,传入相应品类,null也传相当于全部
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Category category = categoryHelper.findById(hotCategory.getCategoryId());
                            //打开商家搜索界面,传入相应的品类,null就为全部也要传过去
                            Intent intent = new Intent(getActivity(), MerchantActivity.class);
                            intent.putExtra(MerchantFragment.INIT_CATEGORY_KEY, category);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void loadActivities() {
        AVQuery<com.model.Activity> query = AVQuery.getQuery(com.model.Activity.class);
        if (city != null)
            query.whereEqualTo(com.model.Activity.CITY_ID, city.getCityId());
        query.limit(activities.size());
        query.orderByDescending(com.model.Activity.UPDATED_AT);
        query.findInBackground(new SafeFindCallback<com.model.Activity>(getActivity()) {
            @Override
            public void findResult(List<com.model.Activity> objects, AVException e) {
                activityLoaded = true;
                handler.sendEmptyMessage(0);//通知查看是否结束刷新
                if (e != null || objects == null || objects.size() <= 0) {//错误或空数据不显示
                    activityContainer.setVisibility(View.GONE);
                    return;
                }
                //加载数据
                activityContainer.setVisibility(View.VISIBLE);//显示
                for (int i = 0; i < activities.size(); i++) {
                    if (i >= objects.size()) {//有可能不足4个
                        activities.get(i).setVisibility(View.GONE);
                        continue;
                    }
                    //设置view
                    activities.get(i).setVisibility(View.VISIBLE);
                    final com.model.Activity activity = objects.get(i);
                    ActivityItemView itemView = activities.get(i);
                    itemView.setTitle(activity.getTitle());
                    itemView.setSubTitle(activity.getDescribe());
                    ImageLoader.displayImage(itemView.getImageView(), activity.getImageUrl());
                    //点击跳到H5界面访问url
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), H5Activity.class);
                            intent.putExtra(H5Activity.TITLE_KEY, activity.getTitle());
                            intent.putExtra(H5Activity.URL_KEY, activity.getWebUrl());
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void loadArea() {
        if (!areaHelper.isEmptyOfCity(city.getCityId()))//该城市商圈没有被缓存
            return;
        AVQuery<Area> query = AVQuery.getQuery(Area.class);
        query.whereEqualTo(Area.CITY_ID, city.getCityId());
        query.setLimit(1000);
        query.findInBackground(new SafeFindCallback<Area>(getActivity()) {

            @Override
            public void findResult(List<Area> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.Area> areas = new ArrayList<>();
                    for (Area area : objects) {
                        areas.add(new com.dao.generate.Area(area.getAreaId(), area.getName(), area.getParentId(), area.getCityId()));
                    }
                    //放入数据库
                    areaHelper.insertData(areas);
                }
            }

        });
    }

    private void setScanner() {
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSearch() {
        toolBar.setSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(getActivity(), "搜索");
            }
        });
    }

    private void setTitleClick() {
        toolBar.setTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                startActivityForResult(intent, SELECT_CITY);//城市选择
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_CITY) {
                City city = (City) data.getSerializableExtra(SelectCityActivity.SELECT_CITY_KEY);
                AppSetting.updCity(city);//update
                refreshView.invokeRefresh();//更新位置及其它信息
            }
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && isLoaded()) {//全部加载完停止刷新
                refreshView.stopRefresh();
            }
        }
    };

    private boolean isLoaded() {
        return hotLoaded && activityLoaded && myLikeLoaded;
    }

}
