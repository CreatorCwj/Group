package com.widget.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;

import com.group.R;
import com.util.UIUtils;

/**
 * Created by cwj on 16/2/6.
 * 自定义dialog基类
 */
public abstract class BaseDialog {

    protected Context context;
    protected Dialog dialog;
    protected int maxWidth;
    protected int maxHeight;

    protected int radius;//圆角弧度

    public BaseDialog(Context context) {
        this(context, R.style.dialogTheme);
    }

    public BaseDialog(Context context, int themeResId) {
        this.context = context;
        setSize();//设置最大尺寸
        dialog = new Dialog(context, themeResId);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//保证在屏幕居中
        //设置一些属性
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        setView();//设置view
    }

    private void setSize() {
        radius = UIUtils.dp2px(context, 3);
        int[] screenSize = UIUtils.getScreenWidthHeightPX(context);
        maxWidth = screenSize[0] * 3 / 4;
        maxHeight = screenSize[1] * 3 / 4;
    }

    private void setView() {
        View view = onCreateView();
        dialog.setContentView(view);
        onViewCreated(view);
    }

    /**
     * 设置是否触摸外部消失
     *
     * @param cancelOnTouchOutside
     */
    public void setCanceledOnTouchOutside(boolean cancelOnTouchOutside) {
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
    }

    /**
     * 设置是否按退出键消失
     *
     * @param cancelable
     */
    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }

    /**
     * 设置dialog布局
     */
    abstract protected View onCreateView();

    /**
     * view创建完成
     *
     * @param view
     */
    abstract protected void onViewCreated(View view);

    /**
     * 取消的监听器
     */
    public void setCancelListener(DialogInterface.OnCancelListener listener) {
        dialog.setOnCancelListener(listener);
    }

    /**
     * 展示
     */
    public void show() {
        dialog.show();
    }

    /**
     * 取消
     */
    public void cancel() {
        dialog.cancel();
    }
}
