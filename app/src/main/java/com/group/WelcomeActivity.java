package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.dao.dbHelpers.AreaHelper;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.dbHelpers.CityHelper;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.leancloud.SafeFindCallback;
import com.model.Area;
import com.model.Category;
import com.model.City;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {

    private static final long MIN_MILLISECONDS = 3 * 1000;

    @Inject
    private CityHelper cityHelper;

    @Inject
    private AreaHelper areaHelper;

    @Inject
    private CategoryHelper categoryHelper;

    private boolean cityLoaded = false;
    private boolean areaLoaded = false;
    private boolean categoryLoaded = false;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        loadCity();
        loadArea();
        loadCategory();
    }

    private void loadCategory() {
        //判断数据来源
        if (categoryHelper.isEmpty()) {//网络获取
            loadCategoryFromNet();
        } else {
            categoryLoaded = true;
            handler.sendEmptyMessage(0);
        }
    }

    private void loadCategoryFromNet() {
        AVQuery<Category> query = AVQuery.getQuery(Category.class);
        query.setLimit(1000);
        query.findInBackground(new SafeFindCallback<Category>(this) {

            @Override
            public void findResult(List<Category> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.Category> categories = new ArrayList<>();
                    for (Category category : objects) {
                        categories.add(new com.dao.generate.Category(category.getCategoryId(), category.getName(), category.getParentId()));
                    }
                    //放入数据库
                    categoryHelper.insertData(categories);
                }
                categoryLoaded = true;
                handler.sendEmptyMessage(0);
            }

        });
    }

    private void loadArea() {
        //判断数据来源
        if (areaHelper.isEmpty()) {//网络获取
            loadAreaFromNet();
        } else {
            areaLoaded = true;
            handler.sendEmptyMessage(0);
        }
    }

    private void loadAreaFromNet() {
        AVQuery<Area> query = AVQuery.getQuery(Area.class);
        query.setLimit(1000);
        query.include(Area.CITY);
        query.findInBackground(new SafeFindCallback<Area>(this) {

            @Override
            public void findResult(List<Area> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.Area> areas = new ArrayList<>();
                    for (Area area : objects) {
                        areas.add(new com.dao.generate.Area(area.getAreaId(), area.getName(), area.getParentId(), area.getCity().getCityId()));
                    }
                    //放入数据库
                    areaHelper.insertData(areas);
                }
                areaLoaded = true;
                handler.sendEmptyMessage(0);
            }

        });
    }

    private void loadCity() {
        //判断数据来源
        if (cityHelper.isEmpty()) {//网络获取
            loadCityFromNet();
        } else {
            cityLoaded = true;
            handler.sendEmptyMessage(0);
        }
    }

    private void loadCityFromNet() {
        AVQuery<City> query = AVQuery.getQuery(City.class);
        query.setLimit(1000);
        query.findInBackground(new SafeFindCallback<City>(this) {

            @Override
            public void findResult(List<City> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.City> cities = new ArrayList<>();
                    for (City city : objects) {
                        cities.add(new com.dao.generate.City(city.getCityId(), city.getName(), city.getPinyin()));
                    }
                    //放入数据库
                    cityHelper.insertData(cities);
                }
                cityLoaded = true;
                handler.sendEmptyMessage(0);
            }

        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isFinishing())
                return;
            if (msg.what == 0) {
                if (loaded()) {
                    //满足至少显示时间
                    long spareTime = MIN_MILLISECONDS - (System.currentTimeMillis() - startTime);
                    if (spareTime < 0) {
                        spareTime = 0;
                    }
                    handler.sendEmptyMessageDelayed(1, spareTime);
                }
            } else if (msg.what == 1) {
                //Jump To Main
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private boolean loaded() {
        return cityLoaded && areaLoaded && categoryLoaded;
    }

}
