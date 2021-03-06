package com.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dao.generate.City;

/**
 * Created by cwj on 16/3/7.
 * 应用设置(SharedPreference)
 */
@SuppressLint("CommitPrefEdits")
public class AppSetting {

    private static final String SETTING_SP_NAME = "setting";

    private static final String START_COUNT_KEY = "startCount";
    private static final String CITY_KEY = "city";
    private static final String WIFI_ENV_KEY = "wifiEnv";
    private static final String PUSH_KEY = "push";

    private static SharedPreferences sharedPreferences;

    /**
     * 必须初始化
     *
     * @param context
     */
    public static void init(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SETTING_SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 进入应用次数
     */
    public static int getStartCount() {
        return sharedPreferences.getInt(START_COUNT_KEY, 0);
    }

    /**
     * 进入一次应用
     */
    public static void addStartCount() {
        int newCount = getStartCount() + 1;
        sharedPreferences.edit().putInt(START_COUNT_KEY, newCount).commit();
    }

    /**
     * 更新城市
     */
    public static void updCity(City city) {
        sharedPreferences.edit().putString(CITY_KEY, JsonUtils.toJsonStr(city)).commit();
    }

    /**
     * 获取当前城市
     */
    public static City getCity() {
        String cityStr = sharedPreferences.getString(CITY_KEY, null);
        if (TextUtils.isEmpty(cityStr))
            return null;
        return JsonUtils.toObject(cityStr, City.class);
    }

    /**
     * 获取wifi环境状态
     */
    public static boolean getWifiEnv() {
        return sharedPreferences.getBoolean(WIFI_ENV_KEY, false);//默认未打开
    }

    /**
     * 设置wifi环境状态
     */
    public static void setWifiEnv(boolean wifiEnv) {
        sharedPreferences.edit().putBoolean(WIFI_ENV_KEY, wifiEnv).commit();
    }

    /**
     * 获取推送状态
     */
    public static boolean getPush() {
        return sharedPreferences.getBoolean(PUSH_KEY, true);//默认打开
    }

    /**
     * 设置推送状态
     */
    public static void setPush(boolean push) {
        sharedPreferences.edit().putBoolean(PUSH_KEY, push).commit();
    }
}
