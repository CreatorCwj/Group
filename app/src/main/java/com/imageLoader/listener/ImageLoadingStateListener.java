package com.imageLoader.listener;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * Created by cwj on 16/1/14.
 * ImageLoader加载状态回调
 */
abstract class ImageLoadingStateListener {

    /**
     * 开始加载
     *
     * @param imageView
     * @param imgUrl
     */
    public void onLoadingStarted(ImageView imageView, String imgUrl) {
    }

    /**
     * 加载失败
     *
     * @param imageView
     * @param imgUrl
     */
    public void onLoadingFailed(ImageView imageView, String imgUrl, FailReason.FailType failType) {
    }

    /**
     * 加载完成
     *
     * @param imageView
     * @param imgUrl
     */
    public void onLoadingComplete(ImageView imageView, String imgUrl, Bitmap loadedImage) {
    }

    /**
     * 取消加载
     *
     * @param imageView
     * @param imgUrl
     */
    public void onLoadingCancelled(ImageView imageView, String imgUrl) {
    }

    /**
     * 最终执行
     *
     * @param imageView
     * @param imgUrl
     */
    public void onLoadingFinally(ImageView imageView, String imgUrl) {
    }
}
