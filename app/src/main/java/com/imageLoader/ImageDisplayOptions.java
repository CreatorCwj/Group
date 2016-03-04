package com.imageLoader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by cwj on 16/1/14.
 * 根据设置参数创建不同的Options,允许外部创建options来加载图片
 */
public class ImageDisplayOptions {

    private static final int INVALID_IMAGE_ID = 0;

    public static DisplayImageOptions getOptions(int loadingImageId, int failImageId, Drawable loadingImage, Drawable failImage) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//内存缓存
                .cacheOnDisk(true)//磁盘缓存
                .bitmapConfig(Bitmap.Config.RGB_565)//图像解码配置
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//解码规模类型
//                .displayer(new FadeInBitmapDisplayer(300))//显示方式(300duration的渐进显示)
                .resetViewBeforeLoading(false);//加载前重置

        //加载时的图片
        if (loadingImageId != INVALID_IMAGE_ID)
            builder.showImageOnLoading(loadingImageId);
        if (loadingImage != null)
            builder.showImageOnLoading(loadingImage);

        //加载失败的图片
        if (failImageId != INVALID_IMAGE_ID) {
            builder.showImageOnFail(loadingImageId);
            builder.showImageForEmptyUri(loadingImageId);
        }
        if (failImage != null) {
            builder.showImageOnFail(failImage);
            builder.showImageForEmptyUri(failImage);
        }
        return builder.build();
    }

    public static DisplayImageOptions getOptions(int loadingImageId, Drawable failImage) {
        return getOptions(loadingImageId, INVALID_IMAGE_ID, null, failImage);
    }

    public static DisplayImageOptions getOptions(Drawable loadingImage, int failImageId) {
        return getOptions(INVALID_IMAGE_ID, failImageId, loadingImage, null);
    }

    public static DisplayImageOptions getOptions(int loadingImageId, int failImageId) {
        return getOptions(loadingImageId, failImageId, null, null);
    }

    public static DisplayImageOptions getOptions(Drawable loadingImage, Drawable failImage) {
        return getOptions(INVALID_IMAGE_ID, INVALID_IMAGE_ID, loadingImage, failImage);
    }

    public static DisplayImageOptions getOptions(int defaultImageId) {
        return getOptions(defaultImageId, defaultImageId, null, null);
    }

    public static DisplayImageOptions getOptions(Drawable defaultImage) {
        return getOptions(INVALID_IMAGE_ID, INVALID_IMAGE_ID, defaultImage, defaultImage);
    }

    public static DisplayImageOptions getOptions() {
        return getOptions(INVALID_IMAGE_ID, INVALID_IMAGE_ID, null, null);
    }
}
