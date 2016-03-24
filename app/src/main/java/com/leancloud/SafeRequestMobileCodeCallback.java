package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.RequestMobileCodeCallback;

/**
 * Created by cwj on 16/3/23.
 * 发送验证码的安全回调
 */
public abstract class SafeRequestMobileCodeCallback extends RequestMobileCodeCallback {

    private Context context;
    private boolean canceled = false;

    public SafeRequestMobileCodeCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void done(AVException e) {
        //如果context为空或是activity且结束了则不再回调
        if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        sendResult(e);
    }

    public abstract void sendResult(AVException e);

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
