package com.fragment.base;

import com.adapter.base.BaseFragmentPagerAdapter;

/**
 * Created by cwj on 16/1/28.
 * {@link BaseFragmentPagerAdapter}
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    private BaseFragmentPagerAdapter adapter;
    private int position;

    public void attachToAdapter(BaseFragmentPagerAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    public String getTitle() {
        return "";
    }

    /**
     * 使用onViewPagerFragmentResume替代更准确
     */
    @Override
    final public void onResume() {
        super.onResume();
        if (adapter != null && adapter.getCurrentIndex() == position) {
            onViewPagerFragmentResume();
        }
    }

    /**
     * viewPager里的fragment展现时调用（包括第一次创建,切换viewPager和从Home、电源键等回来可以完全替代onResume）
     */
    public void onViewPagerFragmentResume() {

    }

    /**
     * 使用onViewPagerFragmentPause替代更准确
     */
    @Override
    final public void onPause() {
        super.onPause();
        if (adapter != null && adapter.getCurrentIndex() == position) {
            onViewPagerFragmentPause();
        }
    }

    /**
     * viewPager里的fragment不显示时调用(包括且换viewPager,退出界面和按Home、电源键等,完全替代onPause)
     */
    public void onViewPagerFragmentPause() {

    }
}
