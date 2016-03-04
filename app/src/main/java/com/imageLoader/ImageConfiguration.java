package com.imageLoader;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by cwj on 16/1/14.
 * app的图片加载各种配置
 */
class ImageConfiguration {

    public static ImageLoaderConfiguration getConfiguration(Context context, DisplayImageOptions options) {
        ImageLoaderConfiguration.Builder configuration = new ImageLoaderConfiguration.Builder(context);
        if (options != null)//通用展示设置
            configuration.defaultDisplayImageOptions(options);
        //缓存文件夹(disk缓存大小不限制,可以设置 discCacheSize)
        File file = FileCache.getCacheDir();
        if (file != null)
            configuration.diskCache(new UnlimitedDiskCache(file));
        //Wifi环境下载器
        configuration.imageDownloader(new WifiDownloader(context));
        //内存缓存默认为当前应用可用内存的1/8大小的LruMemoryCache,也可设置 memoryCache
        //也可设置内存和disk缓存的最大宽度高度质量格式等 memoryCacheExtraOptions discCacheExtraOptions
        return configuration.build();
    }
}
