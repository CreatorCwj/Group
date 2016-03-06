package com.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.adapter.base.BaseAdapter;
import com.baidu.location.BDLocation;
import com.dao.dbHelpers.CityHelper;
import com.dao.generate.City;
import com.google.inject.Inject;
import com.group.R;
import com.location.Location;
import com.location.OnLocationListener;
import com.util.DrawableUtils;
import com.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import roboguice.RoboGuice;

/**
 * Created by cwj on 16/3/7.
 * 城市选择器adapter
 */
public class SelectCityAdapter extends BaseAdapter<City> implements SectionIndexer, AdapterView.OnItemClickListener {

    private static final String LOCATION_PINYIN = "定";
    private static final String LOCATION_NAME = "当前定位城市";

    private static final String RECENTLY_PINYIN = "最";
    private static final String RECENTLY_NAME = "最近使用";

    private static final String sections = LOCATION_PINYIN + RECENTLY_PINYIN + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    enum TYPE {
        LOCATION, NORMAL
    }

    @Inject
    private CityHelper cityHelper;

    private OnCitySelectedListener listener;
    private boolean isSearch = false;

    private BDLocation currentLocation;

    public interface OnCitySelectedListener {
        void onCitySelected(City city);
    }

    public SelectCityAdapter(Context context, OnCitySelectedListener listener, boolean isSearch) {
        super(context);
        this.listener = listener;
        this.isSearch = isSearch;
    }

    @Override
    public void addData(List<City> data) {
        if (!isSearch) {//搜索不加
            //添加A-Z的item
            for (char i = 'A'; i <= 'Z'; i++) {
                City letterItem = new City();
                letterItem.setPinyin(String.valueOf(i));
                letterItem.setName(String.valueOf(i));
                data.add(letterItem);
            }
        }
        //排序
        Collections.sort(data, new Comparator<City>() {
            @Override
            public int compare(City city1, City city2) {
                String sc1 = city1.getPinyin().toLowerCase();
                String sc2 = city2.getPinyin().toLowerCase();
                return sc1.compareTo(sc2);
            }
        });
        if (!isSearch) {//搜索不加
            //加入最近使用
            addRecentUse(data);
            //加入定位城市
            addLocation(data);
        }
        super.addData(data);
    }

    private void addLocation(List<City> data) {
        City locationCity = new City();
        locationCity.setName(LOCATION_NAME);
        locationCity.setPinyin(LOCATION_PINYIN);
        data.add(0, locationCity);
    }

    private void addRecentUse(List<City> data) {
        //从数据库取出指定数量最近使用城市
        List<City> recentlyUsed = cityHelper.getRecentlyUsed();
        if (recentlyUsed.size() <= 0)
            return;
        recentlyUsed = copyRecentlyUsed(recentlyUsed);
        //增加最近使用item
        City recentlyCity = new City();
        recentlyCity.setName(RECENTLY_NAME);
        recentlyCity.setPinyin(RECENTLY_PINYIN);
        recentlyUsed.add(0, recentlyCity);
        //放入到集合中
        data.addAll(0, recentlyUsed);
    }

    private List<City> copyRecentlyUsed(List<City> recentlyUsed) {
        List<City> result = new ArrayList<>();
        for (City city : recentlyUsed) {
            result.add(new City(city.getId(), city.getCityId(), city.getName(), RECENTLY_PINYIN, city.getLastUseTime()));
        }
        return result;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).getPinyin()) {
            case LOCATION_PINYIN:
                return TYPE.LOCATION.ordinal();
            default:
                return TYPE.NORMAL.ordinal();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE.NORMAL.ordinal()) {
            NormalViewHolder holder;
            if (convertView == null) {
                holder = new NormalViewHolder();
                convertView = layoutInflater.inflate(R.layout.select_city_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.select_city_icon);
                holder.textView = (TextView) convertView.findViewById(R.id.select_city_text);
                holder.divider = convertView.findViewById(R.id.select_city_divider);
                convertView.setTag(holder);
            } else {
                holder = (NormalViewHolder) convertView.getTag();
            }
            City city = getItem(position);
            //根据不同类型设置view
            holder.textView.setText(city.getName());
            if (city.getCityId() == 0) {//最近使用item和A-Z的item
                holder.divider.setVisibility(View.VISIBLE);
                holder.textView.setTextColor(context.getResources().getColor(R.color.gray));
                holder.imageView.setVisibility(View.GONE);
                convertView.setBackground(null);
            } else {//正常项
                holder.divider.setVisibility(View.GONE);
                holder.textView.setTextColor(context.getResources().getColor(R.color.black));
                if (city.getLastUseTime() != 0)//是最近使用的
                    holder.imageView.setVisibility(View.VISIBLE);
                else holder.imageView.setVisibility(View.INVISIBLE);
                convertView.setBackground(context.getResources().getDrawable(R.drawable.icon_press));
            }
        } else if (getItemViewType(position) == TYPE.LOCATION.ordinal()) {
            final LocationViewHolder holder;
            if (convertView == null) {
                holder = new LocationViewHolder();
                convertView = layoutInflater.inflate(R.layout.select_city_location_item, null);
                holder.button = (Button) convertView.findViewById(R.id.select_city_location_btn);
                convertView.setTag(holder);
                //只设置一次btn即可,不会变化
                holder.button.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, 5, context.getResources().getColor(R.color.iconPressed))
                        , new DrawableUtils.CornerStateDrawable(new int[]{}, 5, context.getResources().getColor(R.color.colorPrimary))));
                final OnLocationListener locationListener = new OnLocationListener() {
                    @Override
                    public void onPreExecute() {
                        currentLocation = null;//reset
                        holder.button.setText("定位中...");
                    }

                    @Override
                    public void onSuccess(BDLocation location) {
                        String cityName = location.getCity();
                        if (TextUtils.isEmpty(cityName)) {//没获取到城市名算失败
                            onFailed();
                        } else {
                            currentLocation = location;
                            holder.button.setText(cityName);
                        }
                    }

                    @Override
                    public void onFailed() {
                        holder.button.setText("定位失败");
                    }

                    @Override
                    public void onFinally() {

                    }
                };
                //先主动定位
                Location.requestLocation(context, locationListener);
                //点击时失败则重新定位,成功则返回
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentLocation == null) {//重新定位
                            Location.requestLocation(context, locationListener);
                        } else {
                            //获取对应城市返回
                            if (listener != null) {
                                City city = cityHelper.getByName(Utils.getRealCityName(currentLocation.getCity()));
                                listener.onCitySelected(city);
                                if (city != null) {
                                    //更改使用时间
                                    city.setLastUseTime(System.currentTimeMillis());
                                    //放入到数据库中(检查最近使用个数)
                                    cityHelper.updateRecentlyUsed(city);
                                }
                            }
                        }
                    }
                });
            } else {
                holder = (LocationViewHolder) convertView.getTag();
            }
        }
        return convertView;
    }

    @Override
    public String[] getSections() {
        String[] ss = new String[sections.length()];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = sections.charAt(i) + "";
        }
        return ss;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        // 如果当前部分没有item，则之前的部分将被选择
        for (int i = sectionIndex; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    return 0;
                } else {
                    String py = String.valueOf(getItem(j).getPinyin().charAt(0) + "").toLowerCase();
                    String sec = getSections()[i].toLowerCase();
                    if (py.equals(sec))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        City city = getItem(position);
        if (city.getCityId() == 0)//不是具体城市项
            return;
        if (RECENTLY_PINYIN.equals(city.getPinyin()))//最近使用需要转换成真实的city,否则会更新错误拼音
            city = getRealCity(city);
        if (listener != null) {//通知接口
            listener.onCitySelected(city);
        }
        //更改使用时间
        city.setLastUseTime(System.currentTimeMillis());
        //放入到数据库中(检查最近使用个数)
        cityHelper.updateRecentlyUsed(city);
    }

    private City getRealCity(City city) {
        for (City tmp : getData()) {
            if (tmp.getCityId() == city.getCityId() && !RECENTLY_PINYIN.equals(tmp.getPinyin()))
                return tmp;
        }
        return city;
    }

    class NormalViewHolder {
        ImageView imageView;
        TextView textView;
        View divider;
    }

    class LocationViewHolder {
        Button button;
    }
}
