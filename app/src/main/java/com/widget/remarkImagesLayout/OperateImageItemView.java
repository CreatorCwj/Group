package com.widget.remarkImagesLayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.group.R;
import com.imageLoader.ImageLoader;
import com.util.UIUtils;

/**
 * Created by cwj on 16/4/12.
 * 操作图片item
 */
public class OperateImageItemView extends RelativeLayout {

    private ImageView imageView;
    private ImageView cancelIcon;

    private Drawable clearDrawable;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onAddClick(OperateImageItemView imageItemView);

        void onCancelClick(OperateImageItemView imageItemView);
    }

    public OperateImageItemView(Context context) {
        this(context, null);
    }

    public OperateImageItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OperateImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OperateImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListener();
    }

    private void setListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClear() && onClickListener != null) {//可增加时的点击
                    onClickListener.onAddClick(OperateImageItemView.this);
                }
            }
        });
        cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {//取消时的点击
                    onClickListener.onCancelClick(OperateImageItemView.this);
                }
            }
        });
    }

    private void initView() {
        addImageView();
        addCancelIcon();
    }

    private void addCancelIcon() {
        cancelIcon = new ImageView(getContext());
        cancelIcon.setAdjustViewBounds(true);
        cancelIcon.setImageResource(R.drawable.cancel_icon);
        LayoutParams params = new LayoutParams(UIUtils.dp2px(getContext(), 20), UIUtils.dp2px(getContext(), 20));
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(ALIGN_PARENT_TOP);
        addView(cancelIcon, params);
    }

    private void addImageView() {
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams params = new LayoutParams(UIUtils.dp2px(getContext(), 60), UIUtils.dp2px(getContext(), 60));
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        addView(imageView, params);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void reset() {
        setVisibility(VISIBLE);
        cancelIcon.setVisibility(GONE);
        imageView.setImageDrawable(clearDrawable);
    }

    public void setClearDrawable(Drawable drawable) {
        this.clearDrawable = drawable;
    }

    public void setCancelDrawable(Drawable drawable) {
        cancelIcon.setImageDrawable(drawable);
    }

    public void setImage(String url) {
        setVisibility(VISIBLE);
        cancelIcon.setVisibility(VISIBLE);
        ImageLoader.displayImage(imageView, url);
    }

    public boolean isClear() {
        return imageView.getDrawable() == clearDrawable;
    }
}
