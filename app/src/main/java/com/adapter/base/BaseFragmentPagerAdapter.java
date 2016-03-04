package com.adapter.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.fragment.base.BaseViewPagerFragment;

import java.util.List;

/**
 * Created by cwj on 16/1/28.
 * 自定义的嵌套在ViewPager里的Fragment的adapter:
 * 1.支持初始页
 * 2.不去反复生成新view
 * 3.展示提供新周期方法进行处理(比如每次展示都要重新加载一些数据)
 * 4.切换走时提供新周期方法进行处理
 * 5.3-4需要使用{@link BaseViewPagerFragment}作为fragment基类
 * 6.作为非抽象类,如果没用特殊地方则直接new该基类即可
 */
public class BaseFragmentPagerAdapter<T extends BaseViewPagerFragment> extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private static final int DEFAULT_FIRST_PAGE = 0;//首页默认为第一页

    protected Context context;
    protected FragmentManager fm;
    protected ViewPager viewPager;
    protected List<T> fragments;

    private int currentIndex;//当前页索引

    public BaseFragmentPagerAdapter(FragmentActivity fa, ViewPager viewPager, List<T> fragments) {
        this(fa, viewPager, fragments, DEFAULT_FIRST_PAGE);
    }

    public BaseFragmentPagerAdapter(FragmentActivity fa, ViewPager viewPager, List<T> fragments, int firstPage) {
        this.context = fa;
        this.fm = fa.getSupportFragmentManager();
        this.viewPager = viewPager;
        this.fragments = fragments;
        this.currentIndex = firstPage;
        attachFragment();
        initViewPager();
        setFirstPage();
    }

    //双向绑定,fragment处理生命周期使用
    private void attachFragment() {
        for (int i = 0; i < fragments.size(); ++i) {
            fragments.get(i).attachToAdapter(this, i);
        }
    }

    private void setFirstPage() {
        viewPager.setCurrentItem(currentIndex);
    }

    private void initViewPager() {
        viewPager.setAdapter(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if (!fragment.isAdded()) {//第一次添加
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commit();//提交生效,立即执行的话会执行fragment的生命周期
            fm.executePendingTransactions();//立刻执行事务,commit只是提交,是否理科执行依赖于系统性能
            //第一次加载首页要手动调用一下首页的onViewPagerFragmentResume
            /*if (position == firstPage) {
                ((BaseViewPagerFragment) fragment).onViewPagerFragmentResume();
            }*/
        }
        //添加过的删除时只是把view移除了,再添加时只需把view加进来即可
        if (fragment.getView() != null && fragment.getView().getParent() == null) {
            container.addView(fragment.getView());
        }
        return fragment.getView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = fragments.get(position);
        if (fragment != null && fragment.getView() != null) {
            container.removeView(fragment.getView());
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        BaseViewPagerFragment oldFragment = fragments.get(currentIndex);
        if (oldFragment != null && oldFragment.isAdded())//前一个离开时调用
            oldFragment.onViewPagerFragmentPause();
        currentIndex = position;//更新
        BaseViewPagerFragment newFragment = fragments.get(position);
        if (newFragment != null && newFragment.isAdded())//再次打开时调用
            newFragment.onViewPagerFragmentResume();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 供Fragment调用
     *
     * @return
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
}
