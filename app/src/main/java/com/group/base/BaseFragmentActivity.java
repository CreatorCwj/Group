package com.group.base;

import android.support.v4.app.Fragment;

import com.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;

/**
 * Created by cwj on 16/2/19.
 * FragmentActivity基类
 */
public class BaseFragmentActivity extends RoboFragmentActivity {

    private List<BaseFragment> fragments = new ArrayList<>();

    /**
     * 设置监听回退的fragment
     */
    public void setOnBackPress(BaseFragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public void onBackPressed() {
        //先循环每个子fragment(可见的)的回退事件,都返回false则交由系统处理
        boolean isConsumed = false;
        if (fragments != null && fragments.size() > 0) {
            for (BaseFragment fragment : fragments) {
                if (isVisible(fragment) && fragment.onBackPress()) {//有一个消费则不再给系统处理
                    isConsumed = true;
                }
            }
        }
        if (!isConsumed)
            super.onBackPressed();
    }

    //fragment可见指的是可以看见,也就是说逐层都要可见
    private boolean isVisible(Fragment fragment) {
        if (fragment == null)
            return false;
        Fragment tmp = fragment;
        do {
            if (!tmp.isVisible())//有一个没有显示就不可见
                return false;
            tmp = tmp.getParentFragment();
        } while (tmp != null);
        return true;
    }
}
