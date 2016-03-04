package com.widget.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.group.R;
import com.widget.dialog.base.BaseAlertDialog;

/**
 * Created by cwj on 16/2/8.
 * 输入文本的对话框
 */
public class InputDialog extends BaseAlertDialog {

    private static final String DEFAULT_HINT = "请输入文本";

    private EditText editText;

    public InputDialog(Context context) {
        super(context);
    }

    public InputDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected View onCreateContentView() {
        editText = new EditText(context);
        return editText;
    }

    @Override
    protected void onContentViewCreated(View view) {
        editText.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editText.setHint(DEFAULT_HINT);
        editText.setTextColor(context.getResources().getColor(R.color.black));
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);
    }

    /**
     * 拿到内容
     *
     * @return
     */
    public String getText() {
        return editText.getText().toString();
    }

    /**
     * 设置hint
     *
     * @param hint
     */
    public void setHint(String hint) {
        editText.setHint(hint);
    }

    /**
     * 设置输入类型
     *
     * @param type
     */
    public void setInputType(int type) {
        editText.setInputType(type);
    }

    /**
     * 设置最多输入个数
     *
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    /**
     * show前要重置editText
     */
    @Override
    public void show() {
        editText.setText("");
        super.show();
    }
}
