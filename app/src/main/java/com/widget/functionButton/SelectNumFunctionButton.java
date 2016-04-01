package com.widget.functionButton;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.group.R;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.util.Utils;

/**
 * Created by cwj on 16/4/1.
 * 选择数量的自定义功能按钮
 */
public class SelectNumFunctionButton extends FunctionButton {

    private ImageView jianhaoIv;
    private ImageView jiahaoIv;
    private EditText editText;

    private int maxNum = 100;

    private OnNumChangedListener onNumChangedListener;

    public interface OnNumChangedListener {
        void onNumChanged(int newNum);
    }

    public void setOnNumChangedListener(OnNumChangedListener onNumChangedListener) {
        this.onNumChangedListener = onNumChangedListener;
    }

    public SelectNumFunctionButton(Context context) {
        super(context);
    }

    public SelectNumFunctionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectNumFunctionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelectNumFunctionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateCustomView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.select_num_layout, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return view;
    }

    @Override
    protected void onCustomViewCreated(View view) {
        super.onCustomViewCreated(view);
        initView(view);
        setView();
    }

    private void setView() {
        setViewBackground();
        //edittext,先设置监听器,可以初始化
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int num = getNum();
                if (num <= 0) {//最小,不能再小
                    setUseful(jianhaoIv, false);
                    setUseful(jiahaoIv, true);
                } else if (num < maxNum) {//normal
                    setUseful(jianhaoIv, true);
                    setUseful(jiahaoIv, true);
                } else if (num >= maxNum) {//最大,不能再大
                    setUseful(jianhaoIv, true);
                    setUseful(jiahaoIv, false);
                    if (num > maxNum) {//超过时置为最大值并提示
                        editText.setText("" + maxNum);
                        Utils.showToast(getContext(), "一次限购" + maxNum + "张");
                    }
                }
                //通知更新(范围内的才算)
                if (onNumChangedListener != null && num >= 0 && num <= maxNum) {
                    onNumChangedListener.onNumChanged(num);
                }
            }
        });
        editText.setText("" + 0);
        //jianhao
        jianhaoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = getNum() - 1;
                editText.setText("" + num);
            }
        });
        //jiahao
        jiahaoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = getNum() + 1;
                editText.setText("" + num);
            }
        });
    }

    private void setViewBackground() {
        int radius = UIUtils.dp2px(getContext(), 5);
        int strokeWidth = UIUtils.dp2px(getContext(), 1);
        int strokeColor = Color.parseColor("#CCCCCC");
        jianhaoIv.setBackground(DrawableUtils.getLayerDrawable(radius, 0, 0, radius, Color.WHITE, strokeWidth, strokeWidth, strokeWidth, strokeWidth, strokeColor));
        jiahaoIv.setBackground(DrawableUtils.getLayerDrawable(0, radius, radius, 0, Color.WHITE, strokeWidth, strokeWidth, strokeWidth, strokeWidth, strokeColor));
        editText.setBackground(DrawableUtils.getLayerDrawable(Color.WHITE, 0, strokeWidth, 0, strokeWidth, strokeColor));
    }

    private void setUseful(ImageView iv, boolean useful) {
        if (iv.isEnabled() != useful)
            iv.setEnabled(useful);
        if (iv.isClickable() != useful)
            iv.setClickable(useful);
    }

    private void initView(View view) {
        jianhaoIv = (ImageView) view.findViewById(R.id.jianhao_iv);
        jiahaoIv = (ImageView) view.findViewById(R.id.jiahao_iv);
        editText = (EditText) view.findViewById(R.id.select_num_et);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    /**
     * 错误也返回0,当最小处理
     */
    public int getNum() {
        String text = editText.getText().toString();
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }
}
