package com.fragment.base;

import com.adapter.SlideFragmentAdapter;

/**
 * Created by cwj on 16/4/5.
 * {@link SlideFragmentAdapter}
 */
public abstract class BaseSlideFragment extends BaseFragment {

    private SlideFragmentAdapter adapter;
    private int position;

    public void attachToAdapter(SlideFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    /**
     * 使用onSlideFragmentResume替代更准确
     */
    @Override
    final public void onResume() {
        super.onResume();
        if (adapter != null && adapter.getCurrentPage() == position) {
            onSlideFragmentResume();
        }
    }

    /**
     * 完全替代onResume
     */
    public void onSlideFragmentResume() {

    }

    /**
     * 使用onSlideFragmentPause替代更准确
     */
    @Override
    final public void onPause() {
        super.onPause();
        if (adapter != null && adapter.getCurrentPage() == position) {
            onSlideFragmentPause();
        }
    }

    /**
     * 完全替代onPause
     */
    public void onSlideFragmentPause() {

    }
}
