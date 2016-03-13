package com.widget.rlrView.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.group.R;
import com.util.UIUtils;

import java.lang.reflect.Field;

/**
 * Created by cwj on 16/3/12.
 * 可自动刷新的SwipeView
 */
public class AutoRefreshSwipeView extends SwipeRefreshLayout {

    private boolean autoRefresh;

    public AutoRefreshSwipeView(Context context) {
        this(context, null);
    }

    public AutoRefreshSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        initProps();
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RLRView);
            autoRefresh = typedArray.getBoolean(R.styleable.RLRView_autoRefresh, true);//默认自动刷新
            typedArray.recycle();
        }
    }

    private void initProps() {
        //加载圈颜色,可在外部自己设置
        this.setColorSchemeResources(
                android.R.color.holo_red_light, android.R.color.holo_blue_light,
                android.R.color.holo_orange_light);
        //一定要设置offset,否则调用setRefreshing(true)时没有效果
        this.setProgressViewOffset(false, -UIUtils.dp2px(getContext(), 24), UIUtils.dp2px(getContext(), 24));
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //调用处理方法处理
                onViewCreated();
                //防止后续调用,调用一次后即可取消
                AutoRefreshSwipeView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 可重写进行特殊逻辑处理
     * 默认为直接调用刷新
     */
    protected void onViewCreated() {
        if (isEnabled() && isAutoRefresh()) {
            invokeRefresh();
        }
    }

    //通过反射拿到listener
    public void invokeRefresh() {
        try {
            Field field = SwipeRefreshLayout.class.getDeclaredField("mListener");
            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj != null && obj instanceof SwipeRefreshLayout.OnRefreshListener) {
                setRefreshing(true);
                ((SwipeRefreshLayout.OnRefreshListener) obj).onRefresh();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopRefresh() {
        setRefreshing(false);
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }
}
