package com.widget.viewPagers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cwj on 16/1/31.
 * 可点击的ViewPager,提供接口
 */
public class ClickViewPager extends ViewPager {

    public interface OnClickListener {
        void onClick();
    }

    public interface OnLongClickListener {
        void onLongClick();
    }

    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    private static final int LONG_CLICK_TIME = 500;//500ms算onClick事件

    private MyHandler handler = new MyHandler();
    private boolean longClickPressed = false;
    private float firstX = -1;
    private float firstY = -1;

    public ClickViewPager(Context context) {
        super(context);
    }

    public ClickViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //时间到时还处于按下状态则调用listener
            if (longClickPressed && onLongClickListener != null) {
                onLongClickListener.onLongClick();
                longClickPressed = false;
                //触发listener后还要重置click事件,防止调用onClick
                firstX = firstY = -1;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = ev.getRawX();
                firstY = ev.getRawY();
                //按下去,开启计时事件
                longClickPressed = true;
                handler.postDelayed(runnable, LONG_CLICK_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                //取消按下事件
                longClickPressed = false;
                handler.removeCallbacks(runnable);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float curX = ev.getRawX();
                float curY = ev.getRawY();
                handle(firstX, firstY, curX, curY);
                firstX = firstY = -1;
                //取消按下事件
                longClickPressed = false;
                handler.removeCallbacks(runnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void handle(float firstX, float firstY, float curX, float curY) {
        float dis = getDistance(firstX, firstY, curX, curY);
        if (dis == 0 && onClickListener != null) {
            onClickListener.onClick();
        }
    }

    float getDistance(float firstX, float firstY, float curX, float curY) {
        float x = curX - firstX;
        float y = curY - firstY;
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
