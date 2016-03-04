package com.dao.listener;

import java.util.List;

/**
 * Created by cwj on 16/2/17.
 * 异步操作DB的结果回调监听器
 */
public abstract class DBOperationListener<T> {

    public abstract void onGetResult(List<T> result);
}
