package com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.MerchantAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.location.BDLocation;
import com.constant.SortEnum;
import com.constant.TagEnum;
import com.dao.base.BaseSortFilterModelInter;
import com.dao.generate.Area;
import com.dao.generate.Category;
import com.dao.generate.City;
import com.dao.generate.NearbyArea;
import com.fragment.base.BaseFragment;
import com.fragment.base.BaseSlideFragment;
import com.fragment.base.BaseSortFilterFragment;
import com.group.R;
import com.group.SearchActivity;
import com.leancloud.SafeFindCallback;
import com.location.Location;
import com.location.OnLocationListener;
import com.model.Merchant;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.CustomToolBar;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.inject.InjectView;

public class SearchMerchantFragment extends BaseSlideFragment implements RLRView.OnRefreshListener, RLRView.OnLoadListener {

    public static final String SEARCH_WORD_KEY = "searchWord";

    @InjectView(R.id.merchant_custom_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.merchant_fragment_category_filter_tv)
    private TextView categoryFilterTv;

    @InjectView(R.id.merchant_fragment_area_filter_tv)
    private TextView areaFilterTv;

    @InjectView(R.id.merchant_fragment_sort_tv)
    private TextView sortTv;

    @InjectView(R.id.merchant_fragment_filter_tv)
    private TextView filterTv;

    @InjectView(R.id.merchant_fragment_location_layout)
    private LinearLayout locationLayout;

    @InjectView(R.id.merchant_fragment_location_tv)
    private TextView locationTv;

    @InjectView(R.id.merchant_fragment_rlrView)
    private RLRView rlrView;

    private MerchantAdapter adapter;

    private CategoryFragment categoryFragment;
    private AreaFragment areaFragment;
    private SortFragment sortFragment;
    private FilterFragment filterFragment;
    private List<? extends BaseFragment> childFragments;

    private BDLocation curLocation;
    private Area area;
    private Category category;
    private SortEnum sortEnum;
    private List<TagEnum> tagEnums;
    private String searchWord;

    private SafeFindCallback<Merchant> safeFindCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_merchant, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //先定位一次
        locate(false);
        //设置toolbar
        setToolBar();
        //设置选择过滤器
        setSortFilter();
        //设置定位
        setLocation();
        //设置加载view
        setRLRView();
        //接收init参数并更新
        initArguments();
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            searchWord = bundle.getString(SEARCH_WORD_KEY, "");//搜索关键字
            toolBar.setSearchHintText(searchWord);
        }
    }

    private void setRLRView() {
        adapter = new MerchantAdapter(getActivity());
        rlrView.setAdapter(adapter);
        rlrView.setOnItemClickListener(adapter);
        rlrView.setOnRefreshListener(this);
        rlrView.setOnLoadListener(this);
    }

    private void setLocation() {
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //定位
                locate(true);
            }
        });
    }

    private void locate(final boolean isManual) {
        Location.requestLocation(getActivity(), new OnLocationListener() {
            @Override
            public void onPreExecute() {
                locationTv.setText("定位中...");
            }

            @Override
            public void onSuccess(BDLocation location) {
                curLocation = location;//定位成功且有位置信息
                if (!TextUtils.isEmpty(location.getAddrStr())) {
                    locationTv.setText(location.getAddrStr());
                    //手动定位成功要刷新
                    if (isManual)
                        rlrView.refresh();
                } else {
                    onFailed();
                }
            }

            @Override
            public void onFailed() {
                curLocation = null;
                locationTv.setText("定位失败,点击重新定位");
            }

            @Override
            public void onFinally() {
                adapter.setBdLocation(curLocation);//更新adapter里的距离
            }
        });
    }

    private void setSortFilter() {
        //初始化fragments
        initFragments();
        //点击管理
        categoryFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragment(categoryFragment, category);
            }
        });
        areaFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragment(areaFragment, area);
            }
        });
        sortTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragment(sortFragment, sortEnum);
            }
        });
        filterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragment(filterFragment, tagEnums);
            }
        });
    }

    private void initFragments() {
        //initView
        categoryFragment = new CategoryFragment();
        categoryFragment.setControlView(categoryFilterTv);
        areaFragment = new AreaFragment();
        areaFragment.setControlView(areaFilterTv);
        sortFragment = new SortFragment();
        sortFragment.setControlView(sortTv);
        filterFragment = new FilterFragment();
        filterFragment.setControlView(filterTv);
        childFragments = Arrays.asList(categoryFragment, areaFragment, sortFragment, filterFragment);
        //设置更新回调(点击都刷新,不判断是否改变了,方便)
        categoryFragment.setOnSelectListener(new BaseSortFilterFragment.OnSelectListener<Category>() {
            @Override
            public void onSelect(Category obj) {
                category = obj;
                rlrView.refresh();
                if (categoryFragment.getControlView() != null) {
                    categoryFragment.getControlView().setText(category.getName());
                }
            }
        });
        areaFragment.setOnSelectListener(new BaseSortFilterFragment.OnSelectListener<Area>() {
            @Override
            public void onSelect(Area obj) {
                area = obj;
                rlrView.refresh();
                if (areaFragment.getControlView() != null)
                    areaFragment.getControlView().setText(area.getName());
            }
        });
        sortFragment.setOnSelectListener(new BaseSortFilterFragment.OnSelectListener<SortEnum>() {
            @Override
            public void onSelect(SortEnum obj) {
                sortEnum = obj;
                rlrView.refresh();
                if (sortFragment.getControlView() != null)
                    sortFragment.getControlView().setText(sortEnum.getName());
            }
        });
        filterFragment.setOnMultiSelectListener(new BaseSortFilterFragment.OnMultiSelectListener<TagEnum>() {
            @Override
            public void onMultiSelect(List<TagEnum> obj) {
                tagEnums = obj;
                rlrView.refresh();
                //不改变textView的内容
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseSortFilterModelInter> void handleFragment(BaseSortFilterFragment<T> filterFragment, Object t) {
        //关闭其他
        for (BaseFragment fragment : childFragments) {
            if (fragment != filterFragment)
                fragment.hideFragment();
        }
        //打开当前
        if (filterFragment.isAdded()) {
            if (filterFragment.isVisible()) {
                filterFragment.hideFragment();
            } else {
                filterFragment.showFragment();
                if (filterFragment.isCheckable()) {//多选是数组
                    filterFragment.initSelect((List<T>) t);//每次点开后保留上次选择
                } else {//单选是一个
                    filterFragment.initSelect((T) t);//每次点开后保留上次选择
                }
            }
        } else {//首次打开也可能要初始化
            getChildFragmentManager().beginTransaction()
                    .add(R.id.filter_sort_content, filterFragment).commitAllowingStateLoss();
        }
    }

    private void setToolBar() {
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //有数据时才允许跳转到地图界面
                if (adapter.getDataCount() <= 0) {
                    Utils.showToast(getActivity(), "无数据");
                    return;
                }
                Utils.showToast(getActivity(), "地图展示");
            }
        });
        toolBar.setSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (safeFindCallback != null)//先取消当前正在进行的请求
            safeFindCallback.cancel();
        loadData(true);
    }

    @Override
    public void onLoad() {
        loadData(false);
    }

    private void loadData(final boolean isRefresh) {
        safeFindCallback = new SafeFindCallback<Merchant>(getActivity()) {
            @Override
            public void findResult(List<Merchant> objects, AVException e) {
                if (e == null) {
                    if (isRefresh)
                        rlrView.resetData(objects);
                    else rlrView.addData(objects);
                } else {//有错
                    rlrView.rlError();
                }
                rlrView.stopRL();//结束刷新加载
            }
        };
        //搜索查询和其他查询用and
        getMainQuery().findInBackground(safeFindCallback);//查询
    }

    private AVQuery<Merchant> getSearchQuery() {
        List<String> words = Utils.getSearchWords(searchWord);
        if (words.size() <= 0)
            return null;
        return getSearchQuery(words);
    }

    private AVQuery<Merchant> getSearchQuery(List<String> words) {
        List<AVQuery<Merchant>> andQueries = new ArrayList<>();
        for (String word : words) {//关键字之间用and
            andQueries.add(addOrQuery(word));
        }
        return AVQuery.and(andQueries);
    }

    private AVQuery<Merchant> addOrQuery(String word) {
        List<AVQuery<Merchant>> orQueries = new ArrayList<>();
        //商家名,商圈,品类的or查询
        orQueries.add(getMerchantByNameLike(word));
        orQueries.add(getMerchantByAreaLike(Merchant.AREA, word));
        orQueries.add(getMerchantByAreaLike(Merchant.SUB_AREA, word));
        orQueries.add(getMerchantByCategoryLike(Merchant.CATEGORY, word));
        orQueries.add(getMerchantByCategoryLike(Merchant.SUB_CATEGORY, word));
        return AVQuery.or(orQueries);
    }

    private AVQuery<Merchant> getMerchantByAreaLike(String key, String word) {
        AVQuery<com.model.Area> areaQuery = AVQuery.getQuery(com.model.Area.class);
        areaQuery.whereContains(com.model.Area.NAME, word);
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereMatchesQuery(key, areaQuery);
        return query;
    }

    private AVQuery<Merchant> getMerchantByCategoryLike(String key, String word) {
        AVQuery<com.model.Category> categoryQuery = AVQuery.getQuery(com.model.Category.class);
        categoryQuery.whereContains(com.model.Category.NAME, word);
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereMatchesQuery(key, categoryQuery);
        return query;
    }

    private AVQuery<Merchant> getMerchantByNameLike(String word) {
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereContains(Merchant.NAME, word);
        return query;
    }

    private AVQuery<Merchant> getMainQuery() {
        List<AVQuery<Merchant>> andQueries = new ArrayList<>();//搜索查询和其他查询用and
        //搜索查询
        AVQuery<Merchant> searchQuery = getSearchQuery();
        if (searchQuery != null)
            andQueries.add(searchQuery);
        //其他条件查询
        AVQuery<Merchant> mainQuery = AVQuery.getQuery(Merchant.class);
        andQueries.add(mainQuery);
        //cityId(商圈没有时还能定到当前城市)
        City city = AppSetting.getCity();
        if (city != null)
            mainQuery.whereEqualTo(Merchant.CITY_ID, city.getCityId());
        //Area(筛选某一级即可,没必要一二级都筛选)
        if (area != null && !area.isAllFirstFilter()) {//不为空且不为一级全部才有过滤
            if (area.isFirstFilter()) {//一级品类,筛选area字段
                mainQuery.whereMatchesQuery(Merchant.AREA, getInnerQuery(com.model.Area.class, com.model.Area.AREA_ID, area.getAreaId()));
            } else if (area.isSubFilter()) {//二级商圈,筛选subArea字段,或者距离
                if (area instanceof NearbyArea) {//distance(km)
                    if (curLocation != null) {//当前有定位
                        int dis = ((NearbyArea) area).getDistance();
                        mainQuery.whereWithinKilometers(Merchant.LOCATION, new AVGeoPoint(curLocation.getLatitude(), curLocation.getLongitude()), dis);
                    }
                } else {//normal
                    mainQuery.whereMatchesQuery(Merchant.SUB_AREA, getInnerQuery(com.model.Area.class, com.model.Area.AREA_ID, area.getAreaId()));
                }
            }
        }
        //category
        if (category != null && !category.isAllFirstFilter()) {//不为空且不为一级全部才有过滤
            if (category.isFirstFilter()) {//一级品类,category字段筛选
                mainQuery.whereMatchesQuery(Merchant.CATEGORY, getInnerQuery(com.model.Category.class, com.model.Category.CATEGORY_ID, category.getCategoryId()));
            } else if (category.isSubFilter()) {//二级普通品类,subCategory字段筛选(不用再筛选category字段了)
                mainQuery.whereMatchesQuery(Merchant.SUB_CATEGORY, getInnerQuery(com.model.Category.class, com.model.Category.CATEGORY_ID, category.getCategoryId()));
            }
        }
        //filter
        if (tagEnums != null && tagEnums.size() > 0) {//有筛选条件
            List<Integer> tags = new ArrayList<>();
            for (TagEnum tagEnum : tagEnums)
                tags.add(tagEnum.getId());
            mainQuery.whereContainedIn(Merchant.TAGS, tags);
        }
        //组合
        AVQuery<Merchant> query = AVQuery.and(andQueries);
        //sort(排序,在主查询指定)
        if (sortEnum != null && !sortEnum.isAllFirstFilter()) {//不为空且不为一级全部才有过滤
            switch (sortEnum) {
                case POINT:
                    query.orderByDescending(Merchant.POINT);
                    break;
                case DISTANCE:
                    if (curLocation != null)//当前定位有才有用,否则该排序无效
                        query.whereNear(Merchant.LOCATION, new AVGeoPoint(curLocation.getLatitude(), curLocation.getLongitude()));
                    break;
                case AVERAGE:
                    query.orderByDescending(Merchant.AVERAGE);
                    break;
            }
        }
        //include
        query.include(Merchant.CATEGORY);
        query.include(Merchant.SUB_CATEGORY);
        query.include(Merchant.AREA);
        query.include(Merchant.SUB_AREA);
        //当前分页
        query.skip(rlrView.getSkipCount());
        query.setLimit(rlrView.getPageSize());
        return query;
    }

    private <T extends AVObject> AVQuery<T> getInnerQuery(Class<T> clazz, String key, Object value) {
        AVQuery<T> query = AVQuery.getQuery(clazz);
        query.whereEqualTo(key, value);
        return query;
    }

}
