package com.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.group.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by cwj on 16/1/12.
 * 自定义的Request对象处理方式
 */
class RequestObject<T> extends Request<T> {

    private RequestModel<T> request;

    public RequestObject(RequestModel<T> request) {
        super(request.getMethod(), UrlBuilder.buildUrl(request), null);
        this.request = request;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (response == null)
            return null;
        String data = getData(response);
        Log.i("Network-data", data);
        T result = null;
        if (request != null && request.getClazz() != null) {
            Class<T> clazz = request.getClazz();
            try {
                result = new Gson().fromJson(data, clazz);
            } catch (Exception e) {
                result = null;
            }
        }
        return success(result, response);
    }

    private Response<T> success(T obj, NetworkResponse response) {
        return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
    }

    private String getData(NetworkResponse response) {
        String charset = HttpHeaderParser.parseCharset(response.headers);
        String str = null;
        try {
            str = new String(response.data, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected void deliverResponse(T result) {
        if (result != null)
            Log.i("Network-result", result.toString());
        if (isCanceled())
            return;
        if (request != null && request.getRequestCallback() != null) {
            request.getRequestCallback().onRequestSuccess(result);
            request.getRequestCallback().onRequestFinally();
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (error != null)
            Log.i("Network-error", error.toString());
        super.deliverError(error);
        if (isCanceled())
            return;
        if (request != null && request.getRequestCallback() != null && error != null) {
            request.getRequestCallback().onRequestError(error.getMessage());
            request.getRequestCallback().onRequestFinally();
        }
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        //20秒超时不尝试重试
        return new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    @Override
    public void cancel() {
        //已经取消过就不用再次取消
        if (isCanceled())
            return;
        if (request != null && request.getRequestCancelCallback() != null) {
            request.getRequestCancelCallback().onCanceled();
        }
        super.cancel();
        Log.i("Network-cancel", "Canceled");
    }

    @Override
    public Object getTag() {
        return (request != null && request.getTag() != null) ? request.getTag() : super.getTag();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        //需要有认证
        if (request == null)
            return super.getHeaders();
        Map<String, String> headers = request.getHeaders();
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("apiKey", request.getContext().getResources().getString(R.string.apiKey));
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return (request != null && request.getRequestBody() != null) ? request.getRequestBody() : super.getBody();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = (request != null && request.getRequestParams() != null) ? request.getRequestParams() : super.getParams();
        Log.i("Network-params", params.toString());
        return params;
    }
}
