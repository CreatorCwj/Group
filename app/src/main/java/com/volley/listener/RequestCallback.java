package com.volley.listener;

/**
 * Created by cwj on 16/1/12.
 * 请求对象
 */
public interface RequestCallback<T> extends BaseRequestCallback {

    //请求成功
    void onRequestSuccess(T result);
}
