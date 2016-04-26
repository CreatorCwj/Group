package com.application;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.SDKInitializer;
import com.dao.base.DaoManager;
import com.group.HomeActivity;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.location.Location;
import com.model.Activity;
import com.model.Area;
import com.model.Category;
import com.model.CinemaHall;
import com.model.City;
import com.model.Collection;
import com.model.HotCategory;
import com.model.Merchant;
import com.model.Movie;
import com.model.MoviePlay;
import com.model.MovieWant;
import com.model.Order;
import com.model.Remark;
import com.model.RewardLottery;
import com.model.RewardLotteryRecord;
import com.model.RewardPoint;
import com.model.RewardPointRecord;
import com.model.User;
import com.model.Voucher;
import com.util.AppSetting;
import com.volley.Network;

/**
 * Created by cwj on 16/3/4.
 */
public class GroupApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //注册子类后不能再使用new,只能使用子类(因为表已经关联到子类了)
        initSubClass();//注册各个子类(要在init之前)
        AVOSCloud.initialize(this, getResources().getString(R.string.app_id), getResources().getString(R.string.app_key));
        initPush();//初始化推送(做到时再写)

        //AppSetting
        AppSetting.init(getApplicationContext());
        //地图
        SDKInitializer.initialize(getApplicationContext());
        //定位
        Location.init(getApplicationContext());
        //Volley网络
        Network.initNetwork(getApplicationContext());
        //ImageLoader图片加载
        ImageLoader.initConfig(getApplicationContext(), new ColorDrawable(getResources().getColor(R.color.dividerColor)));
        //GreenDao本地数据库
        DaoManager.initGreenDao(getApplicationContext());
    }

    private void initSubClass() {
        AVUser.alwaysUseSubUserClass(User.class);
        AVObject.registerSubclass(Activity.class);
        AVObject.registerSubclass(City.class);
        AVObject.registerSubclass(Area.class);
        AVObject.registerSubclass(Category.class);
        AVObject.registerSubclass(Merchant.class);
        AVObject.registerSubclass(Movie.class);
        AVObject.registerSubclass(RewardPoint.class);
        AVObject.registerSubclass(RewardLottery.class);
        AVObject.registerSubclass(CinemaHall.class);
        AVObject.registerSubclass(MoviePlay.class);
        AVObject.registerSubclass(Voucher.class);
        AVObject.registerSubclass(Collection.class);
        AVObject.registerSubclass(MovieWant.class);
        AVObject.registerSubclass(RewardPointRecord.class);
        AVObject.registerSubclass(RewardLotteryRecord.class);
        AVObject.registerSubclass(Remark.class);
        AVObject.registerSubclass(Order.class);
        AVObject.registerSubclass(HotCategory.class);
    }

    private void initPush() {
        //默认启动界面(也可以用subscribe/unsubscribe来订阅/退订某个频道(名字只能由26字母和数字构成)对应打开的界面,订阅要在保存installation前,退订后也要重新save一下installation)
        PushService.setDefaultPushCallback(getApplicationContext(), HomeActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //获取到唯一的注册ID，卸载后id也删除(可以理解为存储在app包的一个数据,不卸载就一直用一个)
//                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.i("InstallationId", "推送初始化成功");
                } else {
                    Log.i("InstallationId", "推送初始化失败");
                }
            }
        });
    }

}
