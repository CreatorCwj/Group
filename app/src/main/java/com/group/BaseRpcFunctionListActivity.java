package com.group;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

/**
 * Created by cwj on 16/4/8.
 * Rpc查询列表基类
 */
public abstract class BaseRpcFunctionListActivity<T extends AVObject> extends BaseListActivity<T> {

    @Override
    final protected boolean isRpcFunction() {
        return true;
    }

    @Override
    final protected AVQuery<T> getQuery() {
        return null;
    }
}
