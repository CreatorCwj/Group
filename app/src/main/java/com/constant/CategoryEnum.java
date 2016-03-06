package com.constant;

/**
 * Created by cwj on 16/3/9.
 */
public enum CategoryEnum {

    FOOD(1001, "美食"),
    MOVIE(1000, "电影"),
    HOTEL(1003, "酒店"),
    TRAVEL(1005, "出行游玩"),
    ENTERTAINMENT(1004, "休闲娱乐"),
    BEAUTY(1006, "丽人"),
    CAR(1007, "汽车服务"),
    PHOTO(1002, "摄影写真");


    private int id;
    private String name;

    CategoryEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
