package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;

/**
 * Created by cwj on 16/4/2.
 * 自定义云函数回调
 */
public abstract class SafeFunctionCallback<T> extends FunctionCallback<T> {

    private Context context;
    private boolean canceled = false;

    public SafeFunctionCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void done(T t, AVException e) {
        if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        functionBack(t, e);
    }

    protected abstract void functionBack(T t, AVException e);

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
