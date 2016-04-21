package com.group;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adapter.MyLikeAdapter;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.dao.dbHelpers.CategoryHelper;
import com.dao.generate.Category;
import com.google.inject.Inject;
import com.group.base.BaseActivity;
import com.leancloud.SafeFunctionCallback;
import com.model.User;
import com.util.Utils;
import com.widget.rlrView.view.LoadMoreRecyclerView;
import com.widget.rlrView.view.RLRView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_my_like)
public class MyLikeActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.OnItemClickListener {

    @InjectView(R.id.my_like_rlrView)
    private RLRView rlrView;

    @InjectView(R.id.my_like_submit_btn)
    private Button submitBtn;

    @Inject
    private CategoryHelper categoryHelper;

    private MyLikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRLRView();
        setListener();
    }

    private void setRLRView() {
        adapter = new MyLikeAdapter(this);
        rlrView.setAdapter(adapter);
        //set data
        rlrView.resetData(categoryHelper.findAllFirstCategories());
        //set selected
        for (int i = 0; i < adapter.getDataCount(); i++) {
            Category category = adapter.getDataItem(i);
            if (isSelected(category))
                rlrView.setSelected(i, true, false, false);
        }
    }

    private boolean isSelected(Category category) {
        User user = AVUser.getCurrentUser(User.class);
        if (user == null || user.getLikes() == null || user.getLikes().size() <= 0)//没有选择
            return false;
        for (int i : user.getLikes()) {
            if (category.getCategoryId() == i) {//选择了
                return true;
            }
        }
        return false;
    }

    private void setListener() {
        submitBtn.setOnClickListener(this);
        rlrView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        rlrView.setSelected(position, !adapter.isSelected(position), false, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_like_submit_btn://完成
                submit();
                break;
        }
    }

    private void submit() {
        if (AVUser.getCurrentUser() == null) {
            Utils.showToast(this, "请先登陆");
            return;
        }
        if (adapter.getDataCount() <= 0) {
            Utils.showToast(this, "数据有误,请重新进入应用");
            return;
        }
        showLoadingDialog("设置中...");
        List<Integer> likes = new ArrayList<>();
        for (int i = 0; i < adapter.getDataCount(); i++) {
            if (adapter.isSelected(i)) {//选中
                likes.add(adapter.getDataItem(i).getCategoryId());
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("likeIds", likes);
        AVCloud.rpcFunctionInBackground(CloudFunction.UPD_MY_LIKE, params, new SafeFunctionCallback<User>(this) {
            @Override
            protected void functionBack(User user, AVException e) {
                if (e != null) {
                    Utils.showToast(MyLikeActivity.this, "设置失败");
                } else {
                    Utils.showToast(MyLikeActivity.this, "设置成功");
                    finish();
                }
                cancelLoadingDialog();
            }
        });
    }

}
