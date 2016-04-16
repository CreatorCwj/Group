package com.dao.listener;

import android.app.Activity;
import android.content.Context;

import java.util.List;

/**
 * Created by cwj on 16/2/17.
 * 异步操作DB的结果回调监听器(安全模式)
 */
public abstract class DBOperationListener<T> {

    private Context context;

    public DBOperationListener(Context context) {
        this.context = context;
    }

    final public void onInnerGetResult(List<T> result) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing()))
            return;
        onGetResult(result);
    }

    public abstract void onGetResult(List<T> result);

    public Context getContext() {
        return context;
    }
}
