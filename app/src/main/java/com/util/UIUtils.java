package com.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by cwj on 16/1/14.
 * UI相关工具
 */
public class UIUtils {

    /**
     * 将dp单位转为px单位
     */
    public static int dp2px(Context context, float dpValue) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());

    }

    /**
     * 将sp单位转为px单位
     */
    public static int sp2px(Context context, float spValue) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());

    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidthPX(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPX(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽高像素值
     *
     * @param context 上下文
     * @return 返回宽高数组
     */
    public static int[] getScreenWidthHeightPX(Context context) {
        return new int[]{getScreenWidthPX(context), getScreenHeightPX(context)};
    }
}
