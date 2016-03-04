package com.volley.listener;

/**
 * Created by cwj on 16/1/12.
 */
public interface RequestCallback<T> {
    //请求成功
    void onRequestSuccess(T result);

    //请求失败
    void onRequestError(String errorMessage);

    //最终执行的
    void onRequestFinally();
}
