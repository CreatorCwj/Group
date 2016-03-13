package com.dao.generate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/16.
 * 商圈里附近的选项的类
 */
public class NearbyArea extends Area {

    public static final int NEARBY_ID = 100;

    private static final int[] distances = new int[]{1, 3, 5, 10};//距离种类
    private static final List<Area> distanceItems = new ArrayList<>();//距离对象(不用每次都new一个集合)

    private int distance;//距离

    public NearbyArea(int areaId, String name, int parentId) {
        super(areaId, name, parentId);
    }

    public NearbyArea(int areaId, String name, int parentId, int distance) {
        super(areaId, name, parentId);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    /**
     * 取得一级附近的item
     */
    public static Area getNearbyItem() {
        return new NearbyArea(NEARBY_ID, "附近", INVALID_PARENT_ID);
    }

    /**
     * 取得二级的距离items
     */
    public static List<Area> getDistanceItems() {
        if (distanceItems.size() <= 0) {
            for (int dis : distances) {
                distanceItems.add(new NearbyArea((NEARBY_ID + dis), dis + "千米", NEARBY_ID, dis));//areaId为了方便直接这样即可,不会重复的
            }
        }
        return distanceItems;
    }
}
