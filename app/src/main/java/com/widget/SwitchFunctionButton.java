package com.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.group.R;

/**
 * Created by cwj on 16/3/19.
 * 开关功能的按钮
 */
public class SwitchFunctionButton extends FunctionButton {

    private Drawable switchOnDrawable;
    private Drawable switchOffDrawable;

    private ImageView imageView;
    private boolean isOn = false;

    private OnSwitchListener listener;

    public interface OnSwitchListener {
        void onSwitch(View v, boolean isOn);
    }

    public SwitchFunctionButton(Context context) {
        this(context, null);
    }

    public SwitchFunctionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchFunctionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchFunctionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.listener = listener;
    }

    private void initImageView() {
        imageView.setImageDrawable(switchOffDrawable);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOn) {//开了就关上
                    imageView.setImageDrawable(switchOffDrawable);
                } else {//关了就开开
                    imageView.setImageDrawable(switchOnDrawable);
                }
                isOn = !isOn;//更新状态
                if (listener != null)
                    listener.onSwitch(SwitchFunctionButton.this, isOn);
            }
        });
    }

    /**
     * 设置开关状态,也会调用监听器(改变的话)
     */
    public void setSwitch(boolean isOn) {
        if (this.isOn == isOn)
            return;
        imageView.performClick();//主动调用点击事件(别的逻辑都在点击里面干了,不需处理)
    }

    private void initDrawable() {
        switchOnDrawable = getContext().getResources().getDrawable(R.drawable.switch_on);
        switchOffDrawable = getContext().getResources().getDrawable(R.drawable.switch_off);
    }

    @Override
    protected View onCreateCustomView() {
        imageView = new ImageView(getContext());
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @Override
    protected void onCustomViewCreated(View view) {
        super.onCustomViewCreated(view);
        initDrawable();
        initImageView();
    }
}
