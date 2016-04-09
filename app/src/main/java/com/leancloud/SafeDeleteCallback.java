package com.leancloud;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.DeleteCallback;

/**
 * Created by cwj on 16/4/10.
 * 安全删除回调
 */
public abstract class SafeDeleteCallback extends DeleteCallback {

    private Context context;
    private boolean canceled = false;

    public SafeDeleteCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void done(AVException e) {
        if (isCanceled() || context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        delete(e);
    }

    protected abstract void delete(AVException e);

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
