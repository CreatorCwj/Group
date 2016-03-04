package com.imageLoader.listener;

import android.widget.ImageView;

/**
 * Created by cwj on 16/1/15.
 * <p>
 * ImageLoader加载进度回调
 */
public abstract class ImageProgressStateListener extends ImageLoadingStateListener {

    /**
     * 加载进度
     *
     * @param imageView
     * @param imgUrl
     */
    public void onProgress(ImageView imageView, String imgUrl, int current, int total, int progress) {
    }
}
