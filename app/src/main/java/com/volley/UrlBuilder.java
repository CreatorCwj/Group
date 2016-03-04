package com.volley;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.group.R;

import java.util.Map;

/**
 * Created by cwj on 16/1/13.
 * 构造request的url
 */
class UrlBuilder {

    /**
     * 获取拼接的URL,GET请求的话还要拼接参数
     *
     * @param requestModel
     * @return
     */
    public static String buildUrl(RequestModel requestModel) {
        if (requestModel == null)
            return "";
        String url = getUrl(requestModel);
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (requestModel.getMethod() == Request.Method.GET) {
            addGetParams(requestModel, builder);
        }
        url = builder.build().toString();
        Log.i("Network-url", url);
        return url;
    }

    @SuppressWarnings("unchecked")
    private static void addGetParams(RequestModel requestModel, Uri.Builder builder) {
        if (requestModel.getRequestParams() != null) {
            Map<String, String> params = requestModel.getRequestParams();
            for (String key : params.keySet()) {
                String value = params.get(key);
                builder.appendQueryParameter(key, value);
            }
        }
    }

    private static String getUrl(RequestModel requestModel) {
        String url;
        if (!TextUtils.isEmpty(requestModel.getUrl())) {
            url = requestModel.getUrl();
        } else {
            String baseUrl = TextUtils.isEmpty(requestModel.getBaseUrl()) ?
                    requestModel.getContext().getResources().getString(R.string.base_url) :
                    requestModel.getBaseUrl();
            String pathUrl = TextUtils.isEmpty(requestModel.getPathUrl()) ? "" : requestModel.getPathUrl();
            url = baseUrl + pathUrl;
        }
        return url;
    }
}
