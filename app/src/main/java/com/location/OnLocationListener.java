package com.location;

import com.baidu.location.BDLocation;

/**
 * Created by cwj on 16/2/26.
 * 定位监听器
 */
public interface OnLocationListener {

    /**
     * 定位前处理
     */
    void onPreExecute();

    /**
     * 定位成功
     */
    void onSuccess(BDLocation location);

    /**
     * 定位失败
     */
    void onFailed();

    /**
     * 最终处理
     */
    void onFinally();
}
