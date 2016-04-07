package com.volley.listener;

import java.util.List;

/**
 * Created by cwj on 16/4/7.
 * 请求list
 */
public interface RequestListCallback<T> extends BaseRequestCallback {

    //请求成功
    void onRequestSuccess(List<T> result);
}
