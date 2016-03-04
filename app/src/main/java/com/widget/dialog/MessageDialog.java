package com.widget.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group.R;
import com.widget.dialog.base.BaseAlertDialog;

/**
 * Created by cwj on 16/2/7.
 * 显示提示文字的对话框
 */
public class MessageDialog extends BaseAlertDialog {

    private TextView msgTextView;

    public MessageDialog(Context context) {
        super(context);
    }

    public MessageDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected View onCreateContentView() {
        msgTextView = new TextView(context);
        return msgTextView;
    }

    @Override
    protected void onContentViewCreated(View view) {
        msgTextView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        msgTextView.setTextColor(context.getResources().getColor(R.color.black));
        msgTextView.setSingleLine(false);
    }

    /**
     * 设置内容
     *
     * @param msg
     */
    public void setMessage(String msg) {
        msgTextView.setText(msg);
    }
}
