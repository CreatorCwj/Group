package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.adapter.SelectCityAdapter;
import com.dao.dbHelpers.CityHelper;
import com.dao.generate.City;
import com.dao.listener.DBOperationListener;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.CancelableEditView;
import com.widget.CustomToolBar;
import com.widget.indexableListView.IndexableListView;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_select_city)
public class SelectCityActivity extends BaseActivity {

    public static final String SELECT_CITY_KEY = "selectCity";
    public static final String FIRST_COME_KEY = "firstCome";

    @InjectView(R.id.select_city_lv)
    private IndexableListView listView;

    @InjectView(R.id.select_city_searchView)
    private CancelableEditView editView;

    @Inject
    private CityHelper cityHelper;

    private boolean firstCome;//首次进应用(从welcome界面)

    private SelectCityAdapter.OnCitySelectedListener listener = new SelectCityAdapter.OnCitySelectedListener() {
        @Override
        public void onCitySelected(City city) {
            if (city == null) {
                Utils.showToast(SelectCityActivity.this, "对应城市不存在,请重新选择");
                return;
            }
            //修改setting,进入主界面
            if (firstCome) {
                AppSetting.updCity(city);
                Intent intent = new Intent(SelectCityActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {//返回city即可
                Intent intent = new Intent();
                intent.putExtra(SELECT_CITY_KEY, city);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstCome = getIntent().getBooleanExtra(FIRST_COME_KEY, false);
        setListView();
        setSearch();
        setAllData();
    }

    private void setSearch() {
        editView.setOnEditTextChangedListener(new CancelableEditView.OnEditTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (TextUtils.isEmpty(text))
                    setAllData();
                else search(text);
            }
        });
    }

    private void search(String text) {
        List<City> cities = cityHelper.searchByName(text);
        setAdapter(cities, true);
    }

    private void setAllData() {
        //查找全部时用异步,否则有点卡
        cityHelper.findAllAsync(new DBOperationListener<City>(this) {
            @Override
            public void onGetResult(List<City> result) {
                setAdapter(result, false);
            }
        });
    }

    private void setAdapter(List<City> cities, boolean isSearch) {
        if (cities == null || cities.size() <= 0) {
            listView.setAdapter(null);
            return;
        }
        SelectCityAdapter adapter = new SelectCityAdapter(this, listener, isSearch);
        adapter.addData(cities);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

    private void setListView() {
        listView.setFastScrollEnabled(true);
        listView.showIndexScrollerAlwas();
    }

}
