package com.dao.inter;

import com.dao.listener.DBOperationListener;

import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by cwj on 16/2/17.
 * GreenDao异步方法
 */
public interface AsyncDBInterface<T> {

    /**
     * 插入一组数据
     */
    void insertDataAsync(List<T> data, DBOperationListener<T> listener);

    /**
     * 删除一组数据
     */
    void deleteDataAsync(List<T> data, DBOperationListener<T> listener);

    /**
     * 删除全部数据
     */
    void deleteAllAsync(DBOperationListener<T> listener);

    /**
     * 更新一组数据
     */
    void updateDataAsync(List<T> data, DBOperationListener<T> listener);

    /**
     * 查找全部数据
     */
    void findAllAsync(DBOperationListener<T> listener);

    /**
     * 按条件查询
     */
    void findByQueryAsync(Query<T> query, DBOperationListener<T> listener);

}
