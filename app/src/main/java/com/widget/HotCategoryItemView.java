package com.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.group.R;
import com.util.DrawableUtils;

/**
 * Created by cwj on 16/3/10.
 */
public class HotCategoryItemView extends FrameLayout {

    private TextView title;
    private TextView subTitle;
    private ImageView imageView;

    public HotCategoryItemView(Context context) {
        this(context, null);
    }

    public HotCategoryItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotCategoryItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HotCategoryItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        addChild();
    }

    private void initView() {
        setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.RectStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, getContext().getResources().getColor(R.color.iconPressed))
                , new DrawableUtils.RectStateDrawable(new int[]{}, Color.parseColor("#F8F8F8"))));
    }

    private void addChild() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hot_category_item_view, this);
        title = (TextView) view.findViewById(R.id.hot_category_title);
        subTitle = (TextView) view.findViewById(R.id.hot_category_subTitle);
        imageView = (ImageView) view.findViewById(R.id.hot_category_iv);
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
