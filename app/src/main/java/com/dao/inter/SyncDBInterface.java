package com.dao.inter;

import java.util.List;

/**
 * Created by cwj on 16/2/17.
 * GreenDao同步方法
 */
public interface SyncDBInterface<T, K> {

    /**
     * 插入一个数据
     */
    void insertData(T data);

    /**
     * 插入一组数据
     */
    void insertData(List<T> data);

    /**
     * 删除一个数据
     */
    void deleteData(T data);

    /**
     * 删除一组数据
     */
    void deleteData(List<T> data);

    /**
     * 删除全部数据
     */
    void deleteAll();

    /**
     * 更新一个数据
     */
    void updateData(T data);

    /**
     * 更新一组数据
     */
    void updateData(List<T> data);

    /**
     * 按key查找一个数据
     */
    T find(K key);

    /**
     * 查找全部数据
     */
    List<T> findAll();

    /**
     * 获取数据总量
     */
    long getCount();

    /**
     * 是否有数据
     */
    boolean isEmpty();
}
