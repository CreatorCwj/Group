package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adapter.SearchHistoryAdapter;
import com.adapter.SearchMerchantAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.dao.dbHelpers.SearchRecordHelper;
import com.dao.generate.City;
import com.dao.generate.SearchRecord;
import com.dao.listener.DBOperationListener;
import com.fragment.SearchMerchantFragment;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.leancloud.SafeFindCallback;
import com.model.Area;
import com.model.Category;
import com.model.Merchant;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.CancelableEditView;
import com.widget.CustomToolBar;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity {

    @InjectView(R.id.search_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.search_clear_tv)
    private TextView clearTv;

    @InjectView(R.id.search_history_rlrView)
    private RLRView historyRLRView;

    @InjectView(R.id.search_rlrView)
    private RLRView searchRLRView;

    private SearchHistoryAdapter historyAdapter;
    private SearchMerchantAdapter merchantAdapter;

    @Inject
    private SearchRecordHelper searchRecordHelper;

    private SafeFindCallback<Merchant> safeFindCallback;

    private String preSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRLRView();
        setListener();
    }

    private void setListener() {
        //clear
        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyAdapter.getDataCount() <= 0)//已经没有数据
                    return;
                //清除
                searchRecordHelper.deleteAllAsync(new DBOperationListener<SearchRecord>(SearchActivity.this) {
                    @Override
                    public void onGetResult(List<SearchRecord> result) {
                        loadRecentlyUsed();//刷新
                    }
                });
            }
        });
        //input
        toolBar.setSearchTextChangedListener(new CancelableEditView.OnEditTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (text.trim().equals(preSearchText.trim())) {//与之前搜索内容一样,不查询
                    return;
                }
                preSearchText = text;
                List<String> words = Utils.getSearchWords(text);
                if (words.size() <= 0) {//清空,隐藏
                    searchRLRView.clearData();
                    searchRLRView.setVisibility(View.GONE);
                } else {//实时搜索
                    loadSearch(words);
                }
            }
        });
        //click search
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSearch();
            }
        });
    }

    private void gotoSearch() {
        List<String> words = Utils.getSearchWords(preSearchText);
        if (words.size() <= 0)
            return;
        //跳转到搜索页面
        Intent intent = new Intent(SearchActivity.this, SearchMerchantActivity.class);
        intent.putExtra(SearchMerchantFragment.SEARCH_WORD_KEY, preSearchText);
        startActivity(intent);
        //更新搜索历史本地数据库
        searchRecordHelper.addRecentlyUsed(preSearchText);
    }

    private void loadSearch(List<String> words) {
        if (safeFindCallback != null)
            safeFindCallback.cancel();
        //new callback
        safeFindCallback = new SafeFindCallback<Merchant>(this) {
            @Override
            public void findResult(List<Merchant> objects, AVException e) {
                if (e == null && objects != null) {
                    if (searchRLRView.getVisibility() != View.VISIBLE)
                        searchRLRView.setVisibility(View.VISIBLE);
                    searchRLRView.resetData(objects);
                }
            }
        };
        //load
        getSearchQuery(words).findInBackground(safeFindCallback);
    }

    private AVQuery<Merchant> getSearchQuery(List<String> words) {
        List<AVQuery<Merchant>> andQueries = new ArrayList<>();
        for (String word : words) {//关键字之间用and
            andQueries.add(addOrQuery(word));
        }
        AVQuery<Merchant> mainQuery = AVQuery.and(andQueries);
        //最外层筛选条件(cityId,limit,order,include)
        City city = AppSetting.getCity();
        if (city != null)
            mainQuery.whereEqualTo(Merchant.CITY_ID, city.getCityId());
        mainQuery.limit(1000);
        mainQuery.orderByDescending(Merchant.POINT);
        mainQuery.include(Merchant.CATEGORY);
        mainQuery.include(Merchant.SUB_CATEGORY);
        mainQuery.include(Merchant.AREA);
        mainQuery.include(Merchant.SUB_AREA);
        return mainQuery;
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
        AVQuery<Area> areaQuery = AVQuery.getQuery(Area.class);
        areaQuery.whereContains(Area.NAME, word);
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereMatchesQuery(key, areaQuery);
        return query;
    }

    private AVQuery<Merchant> getMerchantByCategoryLike(String key, String word) {
        AVQuery<Category> categoryQuery = AVQuery.getQuery(Category.class);
        categoryQuery.whereContains(Category.NAME, word);
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereMatchesQuery(key, categoryQuery);
        return query;
    }

    private AVQuery<Merchant> getMerchantByNameLike(String word) {
        AVQuery<Merchant> query = AVQuery.getQuery(Merchant.class);
        query.whereContains(Merchant.NAME, word);
        return query;
    }
    
    private void setRLRView() {
        //历史
        historyAdapter = new SearchHistoryAdapter(this);
        historyRLRView.setAdapter(historyAdapter);
        historyRLRView.setOnItemClickListener(historyAdapter);
        //商家
        merchantAdapter = new SearchMerchantAdapter(this);
        searchRLRView.setAdapter(merchantAdapter);
        searchRLRView.setOnItemClickListener(merchantAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新加载最近搜索记录
        loadRecentlyUsed();
    }

    private void loadRecentlyUsed() {
        searchRecordHelper.getRecentlyUsedAsync(new DBOperationListener<SearchRecord>(this) {
            @Override
            public void onGetResult(List<SearchRecord> result) {
                if (result == null)
                    return;
                historyRLRView.resetData(result);
            }
        });
    }
}
