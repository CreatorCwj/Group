package com.constant;

import com.dao.generate.Category;
import com.group.R;

/**
 * Created by cwj on 16/3/9.
 */
public enum CategoryEnum {

    FOOD(1001, "美食", R.drawable.food_big_icon, R.drawable.food_small_icon),
    MOVIE(1000, "电影", R.drawable.movie_big_icon, R.drawable.movie_small_icon),
    HOTEL(1003, "酒店", R.drawable.hotel_big_icon, R.drawable.hotel_small_icon),
    TRAVEL(1005, "出行游玩", R.drawable.travel_big_icon, R.drawable.travel_small_icon),
    ENTERTAINMENT(1004, "休闲娱乐", R.drawable.entertainment_big_icon, R.drawable.entertainment_small_icon),
    BEAUTY(1006, "丽人", R.drawable.beauty_big_icon, R.drawable.beauty_small_icon),
    CAR(1007, "汽车服务", R.drawable.car_big_icon, R.drawable.car_small_icon),
    PHOTO(1002, "摄影写真", R.drawable.photo_big_icon, R.drawable.photo_small_icon);


    private int id;
    private String name;
    private int bigIconId;
    private int smallIconId;

    CategoryEnum(int id, String name, int bigIconId, int smallIconId) {
        this.id = id;
        this.name = name;
        this.bigIconId = bigIconId;
        this.smallIconId = smallIconId;
    }

    public int getBigIconId() {
        return bigIconId;
    }

    public int getSmallIconId() {
        return smallIconId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CategoryEnum getEnumMap(Category category) {
        for (CategoryEnum categoryEnum : CategoryEnum.values()) {
            if (categoryEnum.getId() == category.getCategoryId())
                return categoryEnum;
        }
        return null;
    }

    public static CategoryEnum getEnumMap(com.model.Category category) {
        for (CategoryEnum categoryEnum : CategoryEnum.values()) {
            if (categoryEnum.getId() == category.getCategoryId())
                return categoryEnum;
        }
        return null;
    }
}
