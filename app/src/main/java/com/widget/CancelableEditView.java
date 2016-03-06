package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.group.R;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/9.
 * 自定义EditText的view(管理取消事件,并提供接口)
 * 可设置Hint图标,Cancel图标,字体大小,hint和text的color,hint文字
 */
public class CancelableEditView extends LinearLayout {

    private Drawable hintIcon;
    private Drawable cancelIcon;

    private int editTextSize;
    private int hintColor;
    private int editTextColor;

    private String hintText;

    private EditText editText;
    private ImageView cancelIv;

    private OnEditTextChangedListener listener;

    public interface OnEditTextChangedListener {
        void onTextChanged(String text);
    }

    public CancelableEditView(Context context) {
        this(context, null);
    }

    public CancelableEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CancelableEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CancelableEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
        addChild();
        manage();
    }

    public void setOnEditTextChangedListener(OnEditTextChangedListener listener) {
        this.listener = listener;
    }

    private void manage() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (TextUtils.isEmpty(str)) {
                    cancelIv.setVisibility(INVISIBLE);
                } else if (cancelIv.getVisibility() != VISIBLE) {
                    cancelIv.setVisibility(VISIBLE);
                }
                if (listener != null)
                    listener.onTextChanged(str);
            }
        });
        cancelIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CancelableEditView);
            hintIcon = typedArray.getDrawable(R.styleable.CancelableEditView_hintIcon);
            cancelIcon = typedArray.getDrawable(R.styleable.CancelableEditView_cancelIcon);
            editTextSize = typedArray.getDimensionPixelSize(R.styleable.CancelableEditView_editTextSize, UIUtils.sp2px(getContext(), 17));
            hintColor = typedArray.getColor(R.styleable.CancelableEditView_hintColor, getContext().getResources().getColor(R.color.gray));
            editTextColor = typedArray.getColor(R.styleable.CancelableEditView_editColor, getContext().getResources().getColor(R.color.black));
            hintText = typedArray.getString(R.styleable.CancelableEditView_hintText);
            typedArray.recycle();
        }
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = UIUtils.dp2px(getContext(), 5);
        setPadding(padding, padding, padding, padding);
    }

    private void addChild() {
        addHintIcon();
        addEditText();
        addCancelIcon();
    }

    private void addHintIcon() {
        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = params.topMargin = UIUtils.dp2px(getContext(), 2.5f);
        params.rightMargin = UIUtils.dp2px(getContext(), 5);
        imageView.setAdjustViewBounds(true);
        imageView.setImageDrawable(hintIcon);
        addView(imageView, params);
    }

    private void addEditText() {
        editText = new EditText(getContext());
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        editText.setPadding(0, 0, 0, 0);//一定要设置,好像EditText默认有padding
        editText.setBackground(null);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setSingleLine(true);
        editText.setHint(hintText);
        editText.setHintTextColor(hintColor);
        editText.setTextColor(editTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextSize);
        addView(editText, params);
    }

    private void addCancelIcon() {
        cancelIv = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = params.topMargin = UIUtils.dp2px(getContext(), 2.5f);
        cancelIv.setAdjustViewBounds(true);
        cancelIv.setImageDrawable(cancelIcon);
        cancelIv.setVisibility(INVISIBLE);
        addView(cancelIv, params);
    }

}
