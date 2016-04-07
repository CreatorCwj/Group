package com.volley;

import com.util.JsonUtils;
import com.volley.listener.RequestCallback;

/**
 * Created by cwj on 16/1/12.
 * 对象请求体
 */
@SuppressWarnings("unchecked")
class RequestObject<T> extends BaseRequestObject<T> {

    private RequestModel<T> request;

    public RequestObject(RequestModel<T> request) {
        super(request);
        this.request = request;
    }

    @Override
    protected T getResult(String data) {
        T result;
        try {
            result = JsonUtils.toObject(data, request.getClazz());
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    @Override
    protected void deliverResultCallback(T result) {
        if (request.getRequestCallback() instanceof RequestCallback)
            ((RequestCallback) request.getRequestCallback()).onRequestSuccess(result);
    }

}
