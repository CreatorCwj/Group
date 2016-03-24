package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

/**
 * Created by cwj on 16/3/23.
 * 登录的安全回调
 */
public abstract class SafeLogInCallback<T extends AVUser> extends LogInCallback<T> {

    private Context context;
    private boolean canceled = false;

    public SafeLogInCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void done(T t, AVException e) {
        if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        logIn(t, e);
    }

    public abstract void logIn(T user, AVException e);

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
