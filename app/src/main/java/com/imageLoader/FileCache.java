package com.imageLoader;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by cwj on 16/1/14.
 * disk缓存文件夹,允许外部获取缓存文件夹
 * 下载文件夹
 */
public class FileCache {

    //缓存文件夹名字
    private static final String CACHE_DIR_NAME = "TestLeanCloudCache" + File.separator + "cache";

    //下载文件夹名字
    private static final String DOWNLOAD_DIR_NAME = "TestLeanCloudCache" + File.separator + "download";

    public static File getCacheDir() {
        File file = getDir(CACHE_DIR_NAME);
        Log.i("Cache-Direction", file == null ? "" : file.toString());
        return file;
    }

    public static File getDownloadDir() {
        File file = getDir(DOWNLOAD_DIR_NAME);
        Log.i("Download-Direction", file == null ? "" : file.toString());
        return file;
    }

    private static File getDir(String dirName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStorageDirectory() + File.separator + dirName;
            File file = new File(path);
            if (!file.exists()) {
                boolean res = file.mkdirs();
                if (!res)
                    return null;
            }
            return file;
        }
        return null;
    }
}
