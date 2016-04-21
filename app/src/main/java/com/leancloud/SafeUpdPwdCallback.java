package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.UpdatePasswordCallback;

/**
 * Created by cwj on 16/2/26.
 * 防止activity结束后回调导致异常
 * canceled用来取消请求
 */
public abstract class SafeUpdPwdCallback extends UpdatePasswordCallback {

    private Context context;
    private boolean canceled = false;

    private boolean isFirstEnter = true;

    public SafeUpdPwdCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    final public void done(AVException e) {
        if (isFirstEnter) {//第一次进入时强制调用一次internalDone逻辑,保证在主线程进行
            internalDone(e);
            isFirstEnter = false;
        } else {
            //如果context为空或是activity且结束了则不再回调
            if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
                return;
            update(e);
        }
    }

    public abstract void update(AVException e);

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }

    public Context getContext() {
        return context;
    }
}
