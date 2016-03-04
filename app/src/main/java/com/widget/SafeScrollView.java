package com.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.imageLoader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 可监听滑动的ScrollView
 * 滚动时Image不加载
 * 可添加多个滚动监听器
 */
public class SafeScrollView extends ScrollView {

    /**
     * 空闲
     */
    public static final int IDLE = 0;

    /**
     * 触摸滚动
     */
    public static final int TOUCH_SCROLL = 1;

    /**
     * 自由滑动
     */
    public static final int FLING = 2;

    private static final int HANDLE_DELAY = 50;

    private int currentY = Integer.MIN_VALUE;

    private int scrollState = IDLE;

    private List<OnScrollStateChangedListener> listeners = new ArrayList<>();

    private Handler mHandler = new Handler();

    public interface OnScrollStateChangedListener {
        void onScrollStateChanged(int scrollState);
    }

    public SafeScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SafeScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SafeScrollView(Context context) {
        this(context, null);
    }

    private void init() {
        addOnScrollStateChangedListener(new OnScrollStateChangedListener() {
            @Override
            public void onScrollStateChanged(int scrollState) {
                Log.i("SafeScrollView", "" + scrollState);
                switch (scrollState) {
                    case FLING:
                        if (ImageLoader.isInitial())
                            ImageLoader.pause();
                        break;
                    default:
                        if (ImageLoader.isInitial())
                            ImageLoader.resume();
                        break;
                }
            }
        });
    }

    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (getScrollY() == currentY) {
                if (scrollState != IDLE) {//改变时才调用
                    for (OnScrollStateChangedListener listener : listeners) {
                        listener.onScrollStateChanged(IDLE);
                    }
                }
                scrollState = IDLE;
                mHandler.removeCallbacks(this);
            } else {
                if (scrollState != FLING) {//改变时才调用
                    for (OnScrollStateChangedListener listener : listeners) {
                        listener.onScrollStateChanged(FLING);
                    }
                }
                scrollState = FLING;
                currentY = getScrollY();
                mHandler.postDelayed(this, HANDLE_DELAY);
            }
        }
    };

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (scrollState != TOUCH_SCROLL) {//改变时才调用
                    for (OnScrollStateChangedListener listener : listeners) {
                        listener.onScrollStateChanged(TOUCH_SCROLL);
                    }
                }
                this.scrollState = TOUCH_SCROLL;
                currentY = getScrollY();
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 可添加多个滚动监听器
     */
    public void addOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
        this.listeners.add(listener);
    }

}
