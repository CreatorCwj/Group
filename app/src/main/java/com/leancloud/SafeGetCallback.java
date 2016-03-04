package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;

/**
 * Created by cwj on 16/2/26.
 * 防止activity结束后回调导致异常
 * canceled用来取消请求
 */
public abstract class SafeGetCallback<T extends AVObject> extends GetCallback<T> {

    private Context context;
    private boolean canceled = false;

    public SafeGetCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    final public void done(T t, AVException e) {
        //如果context为空或是activity且结束了则不再回调
        if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        getResult(t, e);
    }

    public abstract void getResult(T object, AVException e);

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }
}
