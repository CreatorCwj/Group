package com.fragment;

import com.avos.avoscloud.AVQuery;
import com.model.Remark;

/**
 * Created by cwj on 16/4/11.
 * 有图的评论
 */
public class ImagesRemarkListFragment extends RemarkListFragment {

    @Override
    protected AVQuery<Remark> getQuery() {
        AVQuery<Remark> query = super.getQuery();
        if (query == null)
            return null;
        return query.whereExists(Remark.IMAGES);
    }

    @Override
    public String getTitle() {
        return "有图";
    }
}
