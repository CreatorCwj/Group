package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.group.R;


/**
 * 圆角圆形ImageView
 */
public class RoundImageView extends ImageView {

    /**
     * 圆角形
     */
    public static final int ROUND = 0;

    /**
     * 圆形
     */
    public static final int CIRCLE = 1;

    private int imageType = ROUND;
    private float radius;
    private int strokeWidth;
    private int strokeColor;

    private Paint maskPaint;
    private Paint zonePaint;
    private Paint strokePaint;
    private RectF rectF;
    private RectF strokeRect;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initProps();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.RoundImageView);
            imageType = typedArray.getInt(R.styleable.RoundImageView_imageType, ROUND);
            radius = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_radius, 0);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_strokeWidth, 0);
            strokeColor = typedArray.getColor(R.styleable.RoundImageView_strokeColor, Color.TRANSPARENT);
            typedArray.recycle();
        }
    }

    private void initProps() {
        //mask
        maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //zone
        zonePaint = new Paint();
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
        //stroke
        strokePaint = new Paint();
        //rect
        strokeRect = new RectF();
        rectF = new RectF();
    }

    //更新边框
    private void updateStroke() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        strokeRect.set(strokeWidth / 2, strokeWidth / 2, rectF.right - strokeWidth / 2, rectF.bottom - strokeWidth / 2);
    }

    /**
     * 设置图片形状
     *
     * @param imageType
     */
    public void setImageType(int imageType) {
        this.imageType = imageType;
        invalidate();
    }

    /**
     * 设置圆角弧度
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 设置边框宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rectF.set(0, 0, getWidth(), getHeight());//实际尺寸
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.saveLayer(rectF, zonePaint, Canvas.ALL_SAVE_FLAG);
        //根据类型画
        if (imageType == ROUND) {
            canvas.drawRoundRect(rectF, radius, radius, zonePaint);
        } else if (imageType == CIRCLE) {
            canvas.drawCircle((rectF.right - rectF.left) / 2,
                    (rectF.bottom - rectF.top) / 2, (rectF.right - rectF.left) / 2, zonePaint);
        }
        canvas.saveLayer(rectF, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        //根据类型画stroke
        updateStroke();
        if (imageType == ROUND) {
            canvas.drawRoundRect(strokeRect, radius, radius, strokePaint);
        } else if (imageType == CIRCLE) {
            canvas.drawCircle((rectF.right - rectF.left) / 2,
                    (rectF.bottom - rectF.top) / 2, (rectF.right - rectF.left - strokeWidth) / 2, strokePaint);
        }
        canvas.restore();
    }

}
