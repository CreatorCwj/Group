package com.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by cwj on 15/12/3.
 * 工具类
 */
public class Utils {

    /**
     * Toast提示
     */
    public static void showToast(Context context, String content, int duration) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        Toast.makeText(context, content, duration).show();
    }

    public static void showToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }

    /**
     * Snack提示
     */
    public static void showSnack(View view, String content, int duration) {
        if (view == null)
            return;
        Snackbar.make(view, content, duration).show();
    }

    public static void showSnack(View view, String content) {
        showSnack(view, content, Snackbar.LENGTH_SHORT);
    }

    /**
     * 判断是否连入wifi
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != cm) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if ((null != networkInfo) && (ConnectivityManager.TYPE_WIFI == networkInfo.getType())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
