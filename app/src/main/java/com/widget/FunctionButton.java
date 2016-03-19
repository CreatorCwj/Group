package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group.R;
import com.util.DrawableUtils;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/19.
 * 封装功能按钮,用于我的,更多等
 */
public class FunctionButton extends LinearLayout {

    private Drawable icon;
    private boolean iconVisibility;
    private String name;
    private String describe;
    private boolean nextVisibility;

    private ImageView iconIv;
    private ImageView nextIv;
    private TextView nameTv;
    private TextView describeTv;

    public FunctionButton(Context context) {
        this(context, null);
    }

    public FunctionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FunctionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FunctionButton);
            icon = typedArray.getDrawable(R.styleable.FunctionButton_functionIcon);
            iconVisibility = typedArray.getBoolean(R.styleable.FunctionButton_functionIconVisibility, true);
            name = typedArray.getString(R.styleable.FunctionButton_functionName);
            describe = typedArray.getString(R.styleable.FunctionButton_functionDescribe);
            nextVisibility = typedArray.getBoolean(R.styleable.FunctionButton_nextIconVisibility, true);
            typedArray.recycle();
        }
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = UIUtils.dp2px(getContext(), 12);
        setPadding(padding, 0, padding, 0);
        setBackground(getBackDrawable());
        addIconIv();
        addNameTv();
        addDescribeTv();
        addNextIcon();
    }

    private Drawable getBackDrawable() {
        int strokeWidth = getContext().getResources().getDimensionPixelSize(R.dimen.divider_height);
        int strokeColor = getContext().getResources().getColor(R.color.dividerColor);
        Drawable drawable = DrawableUtils.getStateDrawable(new DrawableUtils.LayerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, getContext().getResources().getColor(R.color.iconPressed), 0, strokeWidth, 0, strokeWidth, strokeColor)
                , new DrawableUtils.LayerStateDrawable(new int[]{}, Color.WHITE, 0, strokeWidth, 0, strokeWidth, strokeColor));
        return drawable;
    }

    private void addNextIcon() {
        nextIv = new ImageView(getContext());
        LayoutParams params = new LayoutParams(UIUtils.dp2px(getContext(), 10), UIUtils.dp2px(getContext(), 10));
        nextIv.setImageResource(R.drawable.arrow_right);
        if (nextVisibility) {
            nextIv.setVisibility(VISIBLE);
        } else {
            nextIv.setVisibility(GONE);
        }
        addView(nextIv, params);
    }

    private void addDescribeTv() {
        describeTv = new TextView(getContext());
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.rightMargin = UIUtils.dp2px(getContext(), 12);
        describeTv.setSingleLine(true);
        describeTv.setEllipsize(TextUtils.TruncateAt.END);
        describeTv.setGravity(Gravity.END);
        describeTv.setTextColor(getContext().getResources().getColor(R.color.gray));
        describeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        addView(describeTv, params);
        describeTv.setText(describe);
    }

    private void addNameTv() {
        nameTv = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTv.setSingleLine(true);
        nameTv.setEllipsize(TextUtils.TruncateAt.END);
        nameTv.setGravity(Gravity.CENTER_VERTICAL);
        nameTv.setTextColor(getContext().getResources().getColor(R.color.black));
        nameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        addView(nameTv, params);
        nameTv.setText(name);
    }

    private void addIconIv() {
        iconIv = new ImageView(getContext());
        LayoutParams params = new LayoutParams(UIUtils.dp2px(getContext(), 27), UIUtils.dp2px(getContext(), 27));
        params.rightMargin = UIUtils.dp2px(getContext(), 12);
        iconIv.setImageDrawable(icon);
        if (iconVisibility) {
            iconIv.setVisibility(VISIBLE);
        } else {
            iconIv.setVisibility(GONE);
        }
        addView(iconIv, params);
    }

    public void setDescribe(String describe) {
        this.describe = describe;
        describeTv.setText(describe);
    }

    public String getDescribe() {
        return describe;
    }
}
