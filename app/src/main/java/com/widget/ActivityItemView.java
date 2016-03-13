package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.group.R;

/**
 * Created by cwj on 16/3/10.
 */
public class ActivityItemView extends FrameLayout {

    private TextView title;
    private TextView subTitle;
    private ImageView imageView;

    private int titleColor;

    public ActivityItemView(Context context) {
        this(context, null);
    }

    public ActivityItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActivityItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
        addChild();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ActivityItemView);
            titleColor = typedArray.getColor(R.styleable.ActivityItemView_titleColor, Color.BLACK);
            typedArray.recycle();
        }
    }

    private void initView() {
        setBackground(getContext().getResources().getDrawable(R.drawable.icon_press));
    }

    private void addChild() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_view, this);
        title = (TextView) view.findViewById(R.id.activity_title);
        subTitle = (TextView) view.findViewById(R.id.activity_subTitle);
        imageView = (ImageView) view.findViewById(R.id.activity_img);
        title.setTextColor(titleColor);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setSubTitle(String subTitle) {
        this.subTitle.setText(subTitle);
    }

    public void setTitleColor(int color) {
        this.title.setTextColor(color);
    }

    public ImageView getImageView() {
        return imageView;
    }

}
