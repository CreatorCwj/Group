package com.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.group.R;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.widget.dialog.base.BaseDialog;

/**
 * Created by cwj on 16/2/6.
 * 自定义的加载对话框,可以设置显示文字
 */
public class LoadingDialog extends BaseDialog {

    private static final String DEFAULT_TEXT = "加载中...";

    private TextView textView;

    public LoadingDialog(Context context) {
        super(context);
        init();
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
    }

    @Override
    protected void onViewCreated(View view) {
        radius = UIUtils.dp2px(context, 5);
        view.setBackground(DrawableUtils.getDrawable(radius, Color.parseColor("#60000000")));
        textView = (TextView) view.findViewById(R.id.loadingTextView);
    }

    /**
     * 自定义show方法
     */
    @Override
    public void show() {
        show(DEFAULT_TEXT);
    }

    public void show(String text) {
        textView.setText(text);
        dialog.show();
    }

}
