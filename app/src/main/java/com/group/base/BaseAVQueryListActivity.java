package com.group.base;

import com.avos.avoscloud.AVObject;

import java.util.Map;

/**
 * Created by cwj on 16/4/8.
 * AVQuery查询列表基类
 */
public abstract class BaseAVQueryListActivity<T extends AVObject> extends BaseListActivity<T> {

    @Override
    final protected Map<String, Object> getRpcFunctionParams() {
        return null;
    }

    @Override
    final protected String getRpcFunctionName() {
        return null;
    }

    @Override
    final protected boolean isRpcFunction() {
        return false;
    }
}
