package com.group;

import com.adapter.CollectionAdapter;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseAVQueryListActivity;
import com.model.Collection;
import com.model.Merchant;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/10.
 * 收藏列表页
 */
public class CollectionActivity extends BaseAVQueryListActivity<Collection> {

    @Override
    protected RecyclerViewAdapter<Collection> getAdapter() {
        return new CollectionAdapter(this);
    }

    @Override
    protected String getTitleText() {
        return "收藏";
    }

    @Override
    protected AVQuery<Collection> getQuery() {
        if (AVUser.getCurrentUser() == null)
            return null;
        AVQuery<Collection> query = AVQuery.getQuery(Collection.class);
        query.whereEqualTo(Collection.USER, AVUser.getCurrentUser());
        query.orderByDescending(Collection.CREATED_AT);
        query.include(Collection.MERCHANT);
        query.include(Collection.MERCHANT + "." + Merchant.AREA);
        query.include(Collection.MERCHANT + "." + Merchant.SUB_AREA);
        query.include(Collection.MERCHANT + "." + Merchant.CATEGORY);
        query.include(Collection.MERCHANT + "." + Merchant.SUB_CATEGORY);
        return query;
    }
}
