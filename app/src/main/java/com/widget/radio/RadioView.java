package com.widget.radio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group.R;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/5.
 * 自定义的单选按钮
 * 状态:
 * 按下:pressed
 * 选中:selected
 */
public class RadioView extends RelativeLayout {

    private Drawable imageBackground;
    private ColorStateList textBackground;

    private String radioText;
    private int radioTextSize;

    private int imageWidth;
    private int imageHeight;
    private int imageBottom;

    private ImageView imageView;
    private TextView textView;

    private boolean radioChecked;

    public RadioView(Context context) {
        this(context, null);
    }

    public RadioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RadioView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
        initView();
        setCheckListener();
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.RadioView);
            imageBackground = typedArray.getDrawable(R.styleable.RadioView_imageBackground);
            textBackground = typedArray.getColorStateList(R.styleable.RadioView_textBackground);
            imageWidth = typedArray.getDimensionPixelSize(R.styleable.RadioView_imageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageHeight = typedArray.getDimensionPixelSize(R.styleable.RadioView_imageHeight, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioText = typedArray.getString(R.styleable.RadioView_radioText);
            radioTextSize = typedArray.getDimensionPixelSize(R.styleable.RadioView_radioTextSize, UIUtils.sp2px(getContext(), 13));
            radioChecked = typedArray.getBoolean(R.styleable.RadioView_radioChecked, false);
            imageBottom = typedArray.getDimensionPixelSize(R.styleable.RadioView_imageBottom, UIUtils.dp2px(getContext(), 3));
            typedArray.recycle();
        }
    }

    private void initView() {
        //container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(linearLayout, containerParams);
        //ImageView
        imageView = new ImageView(getContext());
        imageView.setBackground(imageBackground);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
        ivParams.bottomMargin = imageBottom;
        linearLayout.addView(imageView, ivParams);
        //TextView
        textView = new TextView(getContext());
        if (textBackground != null)
            textView.setTextColor(textBackground);
        textView.setText(radioText);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, radioTextSize);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(textView, tvParams);
    }

    private void setCheckListener() {
        check(radioChecked);//init
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRadioChecked())//已经选择则不做任何处理
                    return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        press(true);
                        break;
                    case MotionEvent.ACTION_UP://正常松手说明选中,不去设置背景,因为click(此时一定会执行到)会设置
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        press(false);
                        break;
                }
                return false;
            }
        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRadioChecked())//已经选择则不做任何处理
                    return;
                setRadioChecked(true);
                press(false);//此时先设置checked为true,再取消之前的press状态,则不会闪,因为已经改变成checked的background了
            }
        });
    }

    private void check(boolean flag) {
        imageView.setSelected(flag);
        textView.setSelected(flag);
    }

    private void press(boolean flag) {
        imageView.setPressed(flag);
        textView.setPressed(flag);
    }

    public boolean isRadioChecked() {
        return radioChecked;
    }

    public void setRadioChecked(boolean radioChecked) {
        this.radioChecked = radioChecked;
        check(radioChecked);
        //改变的话要通知父view
        if (radioChecked)
            notifyParent();
    }

    private void notifyParent() {
        //通知父view
        if (getParent() != null && getParent() instanceof RadioLayout) {
            ((RadioLayout) getParent()).checkChanged(RadioView.this);
        }
    }

    public void setImageBackground(Drawable imageBackground) {
        imageView.setBackground(imageBackground);
    }

    public void setTextBackground(ColorStateList textBackground) {
        textView.setTextColor(textBackground);
    }

    public void setRadioText(String text) {
        textView.setText(text);
    }

    /**
     * 传入的单位是像素即可
     */
    public void setRadioTextSize(int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

}
