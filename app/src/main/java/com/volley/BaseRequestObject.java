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
import com.group.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by cwj on 16/4/7.
 * 请求体基类
 */
@SuppressWarnings("unchecked")
abstract class BaseRequestObject<T> extends Request<T> {

    private RequestModel innerRequest;

    public BaseRequestObject(RequestModel request) {
        super(request.getMethod(), UrlBuilder.buildUrl(request), null);
        this.innerRequest = request;
    }

    @Override
    final protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (response == null)
            return null;
        String data = getData(response);
        Log.i("Network-data", data);
        T result = null;
        if (innerRequest != null && innerRequest.getClazz() != null) {
            result = getResult(data);
        }
        return success(result, response);
    }

    protected abstract T getResult(String data);

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
        if (innerRequest != null && innerRequest.getRequestCallback() != null) {
            deliverResultCallback(result);
            innerRequest.getRequestCallback().onRequestFinally();
        }
    }

    protected abstract void deliverResultCallback(T result);

    @Override
    public void deliverError(VolleyError error) {
        if (error != null)
            Log.i("Network-error", error.toString());
        super.deliverError(error);
        if (isCanceled())
            return;
        if (innerRequest != null && innerRequest.getRequestCallback() != null && error != null) {
            innerRequest.getRequestCallback().onRequestError(error.getMessage());
            innerRequest.getRequestCallback().onRequestFinally();
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
        if (innerRequest != null && innerRequest.getRequestCancelCallback() != null) {
            innerRequest.getRequestCancelCallback().onCanceled();
        }
        super.cancel();
        Log.i("Network-cancel", "Canceled");
    }

    @Override
    public Object getTag() {
        return (innerRequest != null && innerRequest.getTag() != null) ? innerRequest.getTag() : super.getTag();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        //需要有认证
        if (innerRequest == null)
            return super.getHeaders();
        Map<String, String> headers = innerRequest.getHeaders();
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("apiKey", innerRequest.getContext().getResources().getString(R.string.apiKey));
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return (innerRequest != null && innerRequest.getRequestBody() != null) ? innerRequest.getRequestBody() : super.getBody();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = (innerRequest != null && innerRequest.getRequestParams() != null) ? innerRequest.getRequestParams() : super.getParams();
        Log.i("Network-params", params.toString());
        return params;
    }
}
