package com.location;

import android.app.Activity;
import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by cwj on 16/2/26.
 * 定位模块,需要初始化
 */
public class Location {

    private static LocationClient locationClient;

    public static void init(Context context) {
        locationClient = new LocationClient(context.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位
        option.setCoorType("bd09ll");//可在地图上完美显示的坐标类型
        option.setScanSpan(0);//每次只获取一次
        option.setIsNeedAddress(true);//获取位置信息
        locationClient.setLocOption(option);
    }

    /**
     * 获取位置信息
     * 保证不会在activity销毁时返回
     * 一次请求只获取一次
     */
    public static void requestLocation(final Context context, final OnLocationListener locationListener) {
        //不初始化时会报错
        if (locationListener != null)
            locationListener.onPreExecute();
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (locationListener != null && context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
                    switch (bdLocation.getLocType()) {
                        case BDLocation.TypeCacheLocation://缓存定位(经纬度有,其他信息没有)
                        case BDLocation.TypeNetWorkLocation://网络基站定位
                        case BDLocation.TypeGpsLocation://GPS定位
                        case BDLocation.TypeOffLineLocation://离线定位
                            locationListener.onSuccess(bdLocation);
                            break;
                        default:
                            locationListener.onFailed();
                            break;
                    }
                    locationListener.onFinally();

                }
                locationClient.unRegisterLocationListener(this);//一次性的
                locationClient.stop();//获取后结束
            }
        });
        //每次新打开去获取
        if (!locationClient.isStarted())
            locationClient.start();
        else
            locationClient.requestLocation();
    }
}
