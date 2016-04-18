package com.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adapter.base.BaseFragmentPagerAdapter;
import com.fragment.DriveRouteFragment;
import com.fragment.TransitRouteFragment;
import com.fragment.WalkRouteFragment;
import com.fragment.base.BaseRouteFragment;
import com.group.R;
import com.util.UIUtils;

import java.util.List;

/**
 * Created by cwj on 16/4/19.
 * 线路fragment的pagerAdapter
 */
public class RouteFragmentPagerAdapter extends BaseFragmentPagerAdapter<BaseRouteFragment> {

    private TabLayout tabLayout;

    public RouteFragmentPagerAdapter(FragmentActivity fa, TabLayout tabLayout, ViewPager viewPager, List<BaseRouteFragment> fragments, int firstPage) {
        super(fa, viewPager, fragments, firstPage);
        this.tabLayout = tabLayout;
        initTabLayout();
    }

    private void initTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setTabView();
    }

    private void setTabView() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null)
                tab.setCustomView(getTabView(i));
        }
        //首页选中
        TabLayout.Tab currentTab = tabLayout.getTabAt(getCurrentIndex());
        if (currentTab != null && currentTab.getCustomView() != null) {
            currentTab.getCustomView().setSelected(true);
        }
    }

    private View getTabView(int pos) {
        BaseRouteFragment baseRouteFragment = fragments.get(pos);
        if (baseRouteFragment instanceof TransitRouteFragment) {
            return getImageView(R.drawable.transit);
        } else if (baseRouteFragment instanceof DriveRouteFragment) {
            return getImageView(R.drawable.drive);
        } else if (baseRouteFragment instanceof WalkRouteFragment) {
            return getImageView(R.drawable.walk);
        }
        return getImageView(R.drawable.transit);
    }

    private ImageView getImageView(int resId) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(UIUtils.dp2px(context, 22), UIUtils.dp2px(context, 22)));
        imageView.setImageResource(resId);
        return imageView;
    }
}
