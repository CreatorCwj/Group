package com.volley;

import com.util.JsonUtils;
import com.volley.listener.RequestListCallback;

import java.util.List;

/**
 * Created by cwj on 16/4/7.
 * 数组请求体
 */
@SuppressWarnings("unchecked")
class RequestListObject<T> extends BaseRequestObject<List<T>> {

    private RequestModel<T> request;

    public RequestListObject(RequestModel<T> request) {
        super(request);
        this.request = request;
    }

    @Override
    protected List<T> getResult(String data) {
        List<T> result;
        try {
            result = JsonUtils.toList(data, request.getClazz());
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    @Override
    protected void deliverResultCallback(List<T> result) {
        if (request.getRequestCallback() instanceof RequestListCallback)
            ((RequestListCallback) request.getRequestCallback()).onRequestSuccess(result);
    }

}
