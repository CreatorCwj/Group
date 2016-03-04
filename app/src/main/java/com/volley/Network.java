package com.volley;

import android.content.Context;

import com.volley.listener.RequestCallback;
import com.volley.listener.RequestCancelCallback;

import java.util.Map;

/**
 * Created by cwj on 16/1/12.
 * 网络请求类(针对API Store定制的网络请求框架(auth))
 * 只支持类映射,暂不支持List Map复杂类型
 */
public class Network<T> {

    private RequestModel<T> request;

    public Network(Context context, Class<T> clazz) {
        request = new RequestModel<>(context, clazz);
    }

    public Network<T> setPathUrl(String pathUrl) {
        request.setPathUrl(pathUrl);
        return this;
    }

    public Network<T> setBaseUrl(String baseUrl) {
        request.setBaseUrl(baseUrl);
        return this;
    }

    public Network<T> setUrl(String url) {
        request.setUrl(url);
        return this;
    }

    public Network<T> setTag(Object tag) {
        request.setTag(tag);
        return this;
    }

    public Network<T> setMethod(int method) {
        request.setMethod(method);
        return this;
    }

    public Network<T> setHeaders(Map<String, String> headers) {
        request.setHeaders(headers);
        return this;
    }

    public Network<T> setRequestParams(Map<String, String> requestParams) {
        request.setRequestParams(requestParams);
        return this;
    }

    public Network<T> setRequestBody(byte[] requestBody) {
        request.setRequestBody(requestBody);
        return this;
    }

    public Network<T> setRequestCallback(RequestCallback<T> requestCallback) {
        request.setRequestCallback(requestCallback);
        return this;
    }

    public Network<T> setRequestCancelCallback(RequestCancelCallback requestCancelCallback) {
        request.setRequestCancelCallback(requestCancelCallback);
        return this;
    }

    public Network<T> execute() {
        NetworkManager.addToQueue(request);
        return this;
    }

    /**
     * 初始化网络框架,建议在application里调用
     *
     * @param context
     */
    public static void initNetwork(Context context) {
        NetworkManager.initNetwork(context);
    }

    /**
     * 根据Tag取消请求
     *
     * @param tag
     */
    public static void cancelRequest(Object tag) {
        NetworkManager.cancelRequest(tag);
    }
}
