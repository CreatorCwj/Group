package com.widget.dialog.base;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group.R;
import com.util.DrawableUtils;

/**
 * Created by cwj on 16/2/7.
 * 自定义AlertDialog的基类
 * 支持隐藏或显示按钮
 * 支持对按钮添加监听器(不影响关闭dialog)
 * 可设置按钮文字,标题文字,中间内容view可自定义
 */
public abstract class BaseAlertDialog extends BaseDialog {

    private LinearLayout buttonLayout;
    private TextView titleTextView;
    private TextView positiveTextView;
    private TextView negativeTextView;
    RelativeLayout contentLayout;

    private View.OnClickListener customPositiveListener;
    private View.OnClickListener customNegativeListener;

    private View.OnClickListener baseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancel();//默认监听器:点击后关闭dialog
        }
    };

    public BaseAlertDialog(Context context) {
        super(context);
    }

    public BaseAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    final protected View onCreateView() {
        return LayoutInflater.from(context).inflate(R.layout.base_alert_dialog, null);
    }

    @Override
    final protected void onViewCreated(View view) {
        view.setLayoutParams(new FrameLayout.LayoutParams(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        buttonLayout = (LinearLayout) view.findViewById(R.id.alert_dialog_textView_layout);
        titleTextView = (TextView) view.findViewById(R.id.alert_dialog_title_textView);
        positiveTextView = (TextView) view.findViewById(R.id.alert_dialog_positive_textView);
        negativeTextView = (TextView) view.findViewById(R.id.alert_dialog_negative_textView);
        contentLayout = (RelativeLayout) view.findViewById(R.id.alert_dialog_content_layout);
        //background
        titleTextView.setBackground(DrawableUtils.getDrawable(radius, radius, 0, 0, context.getResources().getColor(R.color.colorPrimary)));
        positiveTextView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_UNPRESSED}, 0, 0, 0, radius, Color.WHITE)
                , new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, 0, 0, 0, radius, context.getResources().getColor(R.color.dividerColor))));
        negativeTextView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_UNPRESSED}, 0, 0, radius, 0, Color.WHITE)
                , new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, 0, 0, radius, 0, context.getResources().getColor(R.color.dividerColor))));
        contentLayout.setBackground(DrawableUtils.getDrawable(0, 0, 0, 0, Color.WHITE));
        setListener();//设置监听器
        View contentView = onCreateContentView();
        contentLayout.addView(contentView);
        onContentViewCreated(contentView);
    }

    private void setListener() {
        positiveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseClickListener.onClick(v);
                if (customPositiveListener != null)
                    customPositiveListener.onClick(v);
            }
        });
        negativeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseClickListener.onClick(v);
                if (customNegativeListener != null)
                    customNegativeListener.onClick(v);
            }
        });
    }

    /**
     * 获取内容的view
     *
     * @return
     */
    abstract protected View onCreateContentView();

    /**
     * 拿到内容的view做处理
     *
     * @param view
     */
    abstract protected void onContentViewCreated(View view);

    /**
     * 设置确定按钮文字
     */
    public void setPositiveText(String text) {
        positiveTextView.setText(text);
    }

    /**
     * 设置取消按钮文字
     */
    public void setNegativeText(String text) {
        negativeTextView.setText(text);
    }

    /**
     * 设置确定按钮点击
     */
    public void setPositiveListener(View.OnClickListener listener) {
        customPositiveListener = listener;
    }

    /**
     * 设置取消按钮点击
     */
    public void setNegativeListener(View.OnClickListener listener) {
        customNegativeListener = listener;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    /**
     * 是否隐藏按钮,也决定中间的背景
     */
    public void hideButton(boolean hide) {
        if (hide && buttonLayout.getVisibility() != View.GONE) {
            buttonLayout.setVisibility(View.GONE);
            contentLayout.setBackground(DrawableUtils.getDrawable(0, 0, radius, radius, Color.WHITE));
        } else if (!hide && buttonLayout.getVisibility() != View.VISIBLE) {
            buttonLayout.setVisibility(View.VISIBLE);
            contentLayout.setBackground(DrawableUtils.getDrawable(0, 0, 0, 0, Color.WHITE));
        }
    }
}
