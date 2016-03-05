package com.widget.radio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by cwj on 16/3/5.
 * 与RadioView联合使用,子view只能是RadioView,否则会有错
 */
public class RadioLayout extends LinearLayout {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioLayout radioLayout, int index);
    }

    private RadioLayout.OnCheckedChangeListener listener;

    public RadioLayout(Context context) {
        this(context, null);
    }

    public RadioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RadioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void checkChanged(RadioView radioView) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == radioView) {
                if (listener != null)
                    listener.onCheckedChanged(RadioLayout.this, i);
            } else {
                ((RadioView) view).setRadioChecked(false);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }
}
