package com.volley;

import android.content.Context;

import com.android.volley.Request;
import com.volley.listener.BaseRequestCallback;
import com.volley.listener.RequestCallback;
import com.volley.listener.RequestCancelCallback;
import com.volley.listener.RequestListCallback;

import java.util.Map;

/**
 * Created by cwj on 16/1/12.
 * 自定义的Request模块,装有常用的参数
 */
class RequestModel<T> {

    //context
    private Context context;

    //全 url
    private String url;

    //base url
    private String baseUrl;

    //相对 url
    private String pathUrl;

    //方法
    private int method = Request.Method.GET;

    //TAG 用于取消的tag
    private Object tag;

    //映射类
    private Class<T> clazz;

    //头部
    private Map<String, String> headers;

    //请求参数
    private Map<String, String> requestParams;

    //post方法请求体(非键值对那种,比如直接上传一张图片)
    private byte[] requestBody;

    //回调
    private BaseRequestCallback baseRequestCallback;

    //取消请求回调
    private RequestCancelCallback requestCancelCallback;

    public RequestModel(Context context, Class<T> clazz) {
        this.context = context;
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Context getContext() {
        return context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    public BaseRequestCallback getRequestCallback() {
        return baseRequestCallback;
    }

    public void setRequestCallback(RequestCallback<T> requestCallback) {
        this.baseRequestCallback = requestCallback;
    }

    public void setRequestListCallback(RequestListCallback<T> requestListCallback) {
        this.baseRequestCallback = requestListCallback;
    }

    public RequestCancelCallback getRequestCancelCallback() {
        return requestCancelCallback;
    }

    public void setRequestCancelCallback(RequestCancelCallback requestCancelCallback) {
        this.requestCancelCallback = requestCancelCallback;
    }
}
