package com.group;

import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.group.base.BaseActivity;
import com.leancloud.SafeFindCallback;
import com.widget.CustomToolBar;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;
import com.widget.rlrView.view.RLRView;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by cwj on 16/4/5.
 * 列表页基类
 */
@ContentView(R.layout.activity_base_list)
public abstract class BaseListActivity<T extends AVObject> extends BaseActivity implements RLRView.OnRefreshListener, RLRView.OnLoadListener {

    @InjectView(R.id.base_list_toolbar)
    protected CustomToolBar toolBar;

    @InjectView(R.id.base_list_rlrView)
    protected RLRView rlrView;

    protected RecyclerViewAdapter<T> adapter;

    private SafeFindCallback<T> safeFindCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle
        toolBar.setTitleText(getTitleText());
        //设置adapter
        adapter = getAdapter();
        rlrView.setAdapter(adapter);
        //设置监听器
        if (adapter instanceof LoadMoreRecyclerView.OnItemClickListener)
            rlrView.setOnItemClickListener((LoadMoreRecyclerView.OnItemClickListener) adapter);
        rlrView.setOnRefreshListener(this);
        rlrView.setOnLoadListener(this);
    }

    protected abstract RecyclerViewAdapter<T> getAdapter();

    protected abstract String getTitleText();

    protected abstract AVQuery<T> getQuery();

    @Override
    public void onRefresh() {
        if (safeFindCallback != null)//先取消当前正在进行的请求
            safeFindCallback.cancel();
        loadData(true);
    }

    @Override
    public void onLoad() {
        loadData(false);
    }

    private void loadData(final boolean isRefresh) {
        safeFindCallback = new SafeFindCallback<T>(this) {
            @Override
            public void findResult(List<T> objects, AVException e) {
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
        AVQuery<T> query = getQuery();
        if (query != null) {
            //分页
            query.skip(rlrView.getSkipCount());
            query.setLimit(rlrView.getPageSize());
            query.findInBackground(safeFindCallback);//查询
        } else {
            rlrView.stopRL();//结束刷新加载
        }
    }

}
