package com.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapter.RemarkAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.fragment.base.BaseViewPagerFragment;
import com.group.R;
import com.leancloud.SafeFindCallback;
import com.model.Remark;
import com.widget.rlrView.view.RLRView;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by cwj on 16/4/11.
 * 普通的评论
 */
public class RemarkListFragment extends BaseViewPagerFragment implements RLRView.OnRefreshListener, RLRView.OnLoadListener {

    @InjectView(R.id.remark_list_rlrView)
    private RLRView rlrView;

    private RemarkAdapter remarkAdapter;

    private SafeFindCallback<Remark> safeFindCallback;

    private AVQuery<Remark> query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remark_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRLRView();
    }

    @Override
    public String getTitle() {
        return "全部";
    }

    private void setRLRView() {
        remarkAdapter = new RemarkAdapter(getActivity());
        rlrView.setAdapter(remarkAdapter);
        rlrView.setOnRefreshListener(this);
        rlrView.setOnLoadListener(this);
    }

    @Override
    public void onLoad() {
        loadData(false);
    }

    @Override
    public void onRefresh() {
        if (safeFindCallback != null)//先取消当前正在进行的请求
            safeFindCallback.cancel();
        loadData(true);
    }

    private void loadData(final boolean isRefresh) {
        safeFindCallback = new SafeFindCallback<Remark>(getActivity()) {
            @Override
            public void findResult(List<Remark> objects, AVException e) {
                if (e == null) {
                    if (isRefresh)
                        rlrView.resetData(objects);
                    else rlrView.addData(objects);
                } else {//有错
                    rlrView.rlError();
                }
                rlrView.stopRL();//结束刷新加载
            }
        };
        //设置查询
        AVQuery<Remark> query = getQuery();
        if (query != null) {
            //分页
            query.skip(rlrView.getSkipCount());
            query.setLimit(rlrView.getPageSize());
            query.findInBackground(safeFindCallback);//查询
        } else {
            rlrView.stopRL();//结束刷新加载
        }
    }

    public void setQuery(AVQuery<Remark> query) {
        this.query = query;
    }

    protected AVQuery<Remark> getQuery() {
        return query;
    }
}
