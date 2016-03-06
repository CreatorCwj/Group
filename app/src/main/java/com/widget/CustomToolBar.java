package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.group.R;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/6.
 * 自定义ToolBar
 */
public class CustomToolBar extends FrameLayout {

    private ImageView backIv;
    private TextView textView;
    private TextView searchTextView;
    private ImageView leftIconIv;
    private ImageView rightIconIv;

    private boolean backVisibility;
    private boolean textVisibility;
    private boolean textIconVisibility;
    private boolean searchVisibility;
    private String titleText;
    private Drawable leftIcon;
    private Drawable rightIcon;

    public CustomToolBar(Context context) {
        this(context, null);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
        setView();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CustomToolBar);
            backVisibility = typedArray.getBoolean(R.styleable.CustomToolBar_backVisibility, false);
            textVisibility = typedArray.getBoolean(R.styleable.CustomToolBar_textVisibility, true);
            textIconVisibility = typedArray.getBoolean(R.styleable.CustomToolBar_textIconVisibility, false);
            searchVisibility = typedArray.getBoolean(R.styleable.CustomToolBar_searchVisibility, false);
            titleText = typedArray.getString(R.styleable.CustomToolBar_titleText);
            leftIcon = typedArray.getDrawable(R.styleable.CustomToolBar_leftIcon);
            rightIcon = typedArray.getDrawable(R.styleable.CustomToolBar_rightIcon);
            typedArray.recycle();
        }
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_toolbar, this);
        backIv = (ImageView) view.findViewById(R.id.custom_toolbar_back_iv);
        textView = (TextView) view.findViewById(R.id.custom_toolbar_tv);
        searchTextView = (TextView) view.findViewById(R.id.custom_toolbar_search_tv);
        leftIconIv = (ImageView) view.findViewById(R.id.custom_toolbar_left_iv);
        rightIconIv = (ImageView) view.findViewById(R.id.custom_toolbar_right_iv);
    }

    private void setView() {
        //可见性
        setVisible(backIv, backVisibility);
        setVisible(textView, textVisibility);
        setVisible(searchTextView, searchVisibility);
        setVisible(leftIconIv, false);
        setVisible(rightIconIv, false);
        //TextView限长
        setTextView();
        //setIcon
        setIcon();
    }

    private void setIcon() {
        if (leftIcon != null) {
            leftIconIv.setImageDrawable(leftIcon);
            leftIconIv.setVisibility(VISIBLE);
        }
        if (rightIcon != null) {
            rightIconIv.setImageDrawable(rightIcon);
            rightIconIv.setVisibility(VISIBLE);
        }
    }

    private void setTextView() {
        textView.setText(getText(titleText));
        //右边按钮
        if (textIconVisibility) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.arrow_down), null);
            textView.setCompoundDrawablePadding(UIUtils.dp2px(getContext(), 3));
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.icon_press));
        } else {
            textView.setCompoundDrawables(null, null, null, null);
            textView.setBackground(null);
        }
    }

    private void setVisible(View view, boolean visible) {
        if (visible)
            view.setVisibility(VISIBLE);
        else view.setVisibility(GONE);
    }

    private String getText(String titleText) {
        //长度限制(否则搜索框显示不好看)
        if (searchVisibility && !TextUtils.isEmpty(titleText) && titleText.length() > 4) {
            return titleText.substring(0, 3) + "...";
        } else {
            return titleText;
        }
    }

    /**
     * 设置文本
     */
    public void setTitleText(String titleText) {
        this.titleText = titleText;
        textView.setText(getText(titleText));
    }

    /**
     * 得到文本
     */
    public String getTitleText() {
        return titleText;
    }

    /**
     * 返回监听
     */
    public void setBackClickListener(View.OnClickListener listener) {
        backIv.setOnClickListener(listener);
    }

    /**
     * 搜索点击监听
     */
    public void setSearchClickListener(View.OnClickListener listener) {
        searchTextView.setOnClickListener(listener);
    }

    /**
     * 左边功能按钮点击监听
     */
    public void setLeftIconClickListener(View.OnClickListener listener) {
        leftIconIv.setOnClickListener(listener);
    }

    /**
     * 右边功能按钮点击监听
     */
    public void setRightIconClickListener(View.OnClickListener listener) {
        rightIconIv.setOnClickListener(listener);
    }

    /**
     * 标题按钮点击监听
     */
    public void setTextClickListener(View.OnClickListener listener) {
        textView.setOnClickListener(listener);
    }

}
