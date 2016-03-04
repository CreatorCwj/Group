package com.widget.viewPagers.imageViewPager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.group.R;

/**
 * Created by cwj on 16/2/9.
 * 自定义圆点view
 * 1.可指定选中颜色和未选中颜色
 * 2.可以指定padding
 * 3.可以指定边框
 */
public class PointView extends View {

    private int selectColor;
    private int unSelectColor;

    private int strokeWidth;
    private int strokeColor;

    private String text = "";
    private int textColor;
    private float textSize;

    private Paint paint;
    private Paint strokePaint;
    private Paint textPaint;

    public PointView(Context context) {
        this(context, null);
    }

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ImageViewPager);
            selectColor = typedArray.getColor(R.styleable.ImageViewPager_selectColor, getResources().getColor(R.color.colorPrimary));
            unSelectColor = typedArray.getColor(R.styleable.ImageViewPager_unSelectColor, Color.GRAY);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.ImageViewPager_pointStrokeWidth, 0);
            strokeColor = typedArray.getColor(R.styleable.ImageViewPager_pointStrokeColor, Color.TRANSPARENT);
            text = typedArray.getString(R.styleable.ImageViewPager_pointText);
            textColor = typedArray.getColor(R.styleable.ImageViewPager_pointTextColor, Color.WHITE);
            textSize = typedArray.getDimensionPixelSize(R.styleable.ImageViewPager_pointTextSize, 15);
            typedArray.recycle();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        //设置画笔
        if (paint == null)
            paint = new Paint();
        paint.setAntiAlias(true);
        if (isSelected()) {
            paint.setColor(selectColor);
        } else {
            paint.setColor(unSelectColor);
        }

        //绘制圆形
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;
        int radius = Math.min(Math.min(centerX - getPaddingLeft(), centerX - getPaddingRight()), Math.min(centerY - getPaddingTop(), centerY - getPaddingBottom()));
        canvas.drawCircle(centerX, centerY, radius, paint);

        //绘制边框
        if (strokePaint == null)
            strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2, strokePaint);

        //绘制文字
        if (TextUtils.isEmpty(text))
            return;
        if (textPaint == null)
            textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText(text); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        //根据font属性来决定baseline作为字体的y轴中心
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, centerX - textWidth / 2, baseline, textPaint); // 画出文字
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
        invalidate();
    }

    public void setUnSelectColor(int unSelectColor) {
        this.unSelectColor = unSelectColor;
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }
}
