package com.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.inject.Inject;
import com.group.R;
import com.util.AppSetting;
import com.util.JsonUtils;

import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Created by cwj on 16/4/27.
 * 推送广播基类
 */
public abstract class BasePushReceiver extends RoboBroadcastReceiver {

    private final String DATA_KEY = "com.avos.avoscloud.Data";

    private final int NOTIFICATION_ID = 1000;

    @Inject
    private NotificationManager manager;

    private Notification notification;

    private Notification.Builder builder;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        boolean push = AppSetting.getPush();
        if (!push)//不接收推送
            return;
        if (intent == null || !intent.hasExtra(DATA_KEY))//不是推送来的
            return;
        //设置通知栏
        initNotification(context, intent);
        //提示通知栏
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void initNotification(Context context, Intent intent) {
        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.app_icon);// 一定要设置,在顶上显示的图标
        builder.setDefaults(Notification.DEFAULT_ALL);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                getIntent(context, intent), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        setView(remoteViews, intent);
        builder.setContent(remoteViews);

        notification = builder.build();
        // 点击就取消|只提示一次(更新时候只震动一次,cancel后重置)
        notification.flags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_ONLY_ALERT_ONCE;
    }

    private void setView(RemoteViews remoteViews, Intent intent) {
        remoteViews.setTextViewText(R.id.push_notify_name_tv, getInfo(intent, "name"));
        remoteViews.setTextViewText(R.id.push_notify_describe_tv, getInfo(intent, "describe"));
    }

    protected String getInfo(Intent intent, String key) {
        String json = intent.getExtras().getString(DATA_KEY);
        return JsonUtils.getStrValueOfJsonStr(json, key);
    }

    protected abstract Intent getIntent(Context context, Intent intent);
}
