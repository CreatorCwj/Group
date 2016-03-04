package com.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by cwj on 16/2/10.
 * 制作各种Drawable背景的工具类
 * 1.普通背景,包括边框
 * 2.状态背景,包括边框
 * 3.支持指定每个边框的长度
 * 4.支持圆角
 * 5.支持文字状态颜色的生成
 * 6.提供了几种常用状态,其余的可以自行通过android.R.attr.state_xxx来设置
 * 7.stroke单位是px
 */
public class DrawableUtils {

    /**
     * 提供的状态
     */
    //按下与否
    public static final int STATE_PRESSED = android.R.attr.state_pressed;
    public static final int STATE_UNPRESSED = -STATE_PRESSED;
    //checked与否
    public static final int STATE_CHECKED = android.R.attr.state_checked;
    public static final int STATE_UNCHECKED = -STATE_CHECKED;
    //select与否
    public static final int STATE_SELECTED = android.R.attr.state_selected;
    public static final int STATE_UNSELECTED = -STATE_SELECTED;
    //是否可用
    public static final int STATE_ENABLED = android.R.attr.state_enabled;
    public static final int STATE_UNENABLED = -STATE_ENABLED;

    /**
     * 颜色状态对象,传入颜色状态对象数组
     */
    public static ColorStateList getStateColor(StateColor... colors) {
        int[][] stateArr = new int[colors.length][];
        int[] colorArr = new int[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            stateArr[i] = colors[i].states;
            colorArr[i] = colors[i].color;
        }
        return new ColorStateList(stateArr, colorArr);
    }

    /**
     * 矩形drawable
     */
    public static Drawable getDrawable(int fillColor) {
        return getDrawable(0, 0, 0, 0, fillColor);
    }

    /**
     * 矩形drawable,带边框的
     */
    public static Drawable getDrawable(int fillColor, int strokeWidth, int strokeColor) {
        return getDrawable(0, 0, 0, 0, fillColor, strokeWidth, strokeColor);
    }

    /**
     * 圆角drawable
     * 提供颜色和四个角弧度
     */
    public static Drawable getDrawable(int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor) {
        float[] outer = new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft};
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(outer);
        drawable.setColor(fillColor);
        return drawable;
    }

    /**
     * 圆角drawable
     * 提供颜色和四个角弧度(四个角度一样)
     */
    public static Drawable getDrawable(int radius, int fillColor) {
        return getDrawable(radius, radius, radius, radius, fillColor);
    }

    /**
     * 圆角drawable,带边框的
     * 提供颜色和四个角弧度以及边框长度和颜色
     */
    public static Drawable getDrawable(int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = (GradientDrawable) getDrawable(topLeft, topRight, bottomRight, bottomLeft, fillColor);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    /**
     * 圆角drawable,带边框的
     * 提供颜色和四个角弧度以及边框长度和颜色(四个角度一样)
     */
    public static Drawable getDrawable(int radius, int fillColor, int strokeWidth, int strokeColor) {
        return getDrawable(radius, radius, radius, radius, fillColor, strokeWidth, strokeColor);
    }

    /**
     * 矩形drawable,指定每边边框
     */
    public static Drawable getLayerDrawable(int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
        return getLayerDrawable(0, 0, 0, 0, fillColor, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth, strokeColor);
    }

    /**
     * 圆角drawable,带边框的,指定每边边框
     */
    public static Drawable getLayerDrawable(int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
        Drawable fillDrawable = getDrawable(topLeft, topRight, bottomRight, bottomLeft, fillColor);//中间drawable
        float[] outer = new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft};
        GradientDrawable strokeDrawable = new GradientDrawable();//边框drawable
        strokeDrawable.setColor(strokeColor);
        strokeDrawable.setCornerRadii(outer);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{strokeDrawable, fillDrawable});
        //1为上面这层
        layerDrawable.setLayerInset(1, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth);
        return layerDrawable;
    }

    /**
     * 圆角drawable,带边框的,指定每边边框(角度一样)
     */
    public static Drawable getLayerDrawable(int radius, int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
        return getLayerDrawable(radius, radius, radius, radius, fillColor, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth, strokeColor);
    }

    /**
     * 生成状态drawable
     * 传入矩形的或圆角的状态类即可
     *
     * @param drawables
     */
    public static Drawable getStateDrawable(StateDrawable... drawables) {
        StateListDrawable drawable = new StateListDrawable();
        for (StateDrawable stateDrawable : drawables) {
            drawable.addState(stateDrawable.states, stateDrawable.drawable);
        }
        return drawable;
    }

    /**
     * 颜色状态类
     */
    public static class StateColor {

        protected int[] states;
        protected int color;

        public StateColor(int[] states, int color) {
            this.states = states;
            this.color = color;
        }
    }

    /**
     * 矩形状态类
     */
    public static class RectStateDrawable extends StateDrawable {

        public RectStateDrawable(int[] states, int fillColor) {
            super(states);
            this.drawable = getDrawable(fillColor);
        }

        public RectStateDrawable(int[] states, int fillColor, int strokeWidth, int strokeColor) {
            super(states);
            this.drawable = getDrawable(fillColor, strokeWidth, strokeColor);
        }

    }

    /**
     * 圆角状态类
     */
    public static class CornerStateDrawable extends StateDrawable {

        public CornerStateDrawable(int[] states, int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor) {
            super(states);
            this.drawable = getDrawable(topLeft, topRight, bottomRight, bottomLeft, fillColor);
        }

        public CornerStateDrawable(int[] states, int radius, int fillColor) {
            super(states);
            this.drawable = getDrawable(radius, fillColor);
        }

        public CornerStateDrawable(int[] states, int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor, int strokeWidth, int strokeColor) {
            super(states);
            this.drawable = getDrawable(topLeft, topRight, bottomRight, bottomLeft, fillColor, strokeWidth, strokeColor);
        }

        public CornerStateDrawable(int[] states, int radius, int fillColor, int strokeWidth, int strokeColor) {
            super(states);
            this.drawable = getDrawable(radius, fillColor, strokeWidth, strokeColor);
        }

    }

    /**
     * 圆角状态类,可指定各个边框的
     */
    public static class LayerStateDrawable extends StateDrawable {

        public LayerStateDrawable(int[] states, int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
            super(states);
            this.drawable = getLayerDrawable(fillColor, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth, strokeColor);
        }

        public LayerStateDrawable(int[] states, int radius, int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
            super(states);
            this.drawable = getLayerDrawable(radius, fillColor, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth, strokeColor);
        }

        public LayerStateDrawable(int[] states, int topLeft, int topRight, int bottomRight, int bottomLeft, int fillColor, int leftStrokeWidth, int topStrokeWidth, int rightStrokeWidth, int bottomStrokeWidth, int strokeColor) {
            super(states);
            this.drawable = getLayerDrawable(topLeft, topRight, bottomRight, bottomLeft, fillColor, leftStrokeWidth, topStrokeWidth, rightStrokeWidth, bottomStrokeWidth, strokeColor);
        }

    }

    //基类
    public abstract static class StateDrawable {

        protected int[] states;
        protected Drawable drawable;

        public StateDrawable(int[] states) {
            this.states = states;
        }

    }
}
