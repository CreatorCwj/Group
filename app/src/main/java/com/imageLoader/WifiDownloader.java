package com.imageLoader;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.util.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cwj on 16/2/18.
 * wifi环境免下载的下载器
 * 此开关要做成可动态设置的(通过存储在SharedPreference的开关)
 */
public class WifiDownloader extends BaseImageDownloader {

    public WifiDownloader(Context context) {
        super(context);
    }

    public WifiDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        //如果不是wifi环境则不从网络下载
        if (!Utils.isWifiConnected(context))
            return null;
        return super.getStreamFromNetwork(imageUri, extra);
    }
}
