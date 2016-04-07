package com.volley.listener;

/**
 * Created by cwj on 16/4/7.
 * 请求回调基类
 */
public interface BaseRequestCallback {

    //请求失败
    void onRequestError(String errorMessage);

    //最终执行的
    void onRequestFinally();
}
