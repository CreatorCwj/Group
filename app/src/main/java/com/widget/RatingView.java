package com.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group.R;
import com.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/4/12.
 * 打分的自定义view,5分,带textView
 */
public class RatingView extends RelativeLayout {

    public static final int MAX_POINT = 5;

    private TextView pointTv;

    private List<ImageView> ratings;

    private Drawable empty;
    private Drawable full;

    private int curPoint;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        initDrawable();
        addTextView();
        addRating();
        initRating();
        setListener();
    }

    public int getPoint() {
        return curPoint;
    }

    public void setPoint(int point) {
        if (point < 0 || point > MAX_POINT)
            return;
        updView(point - 1);
    }

    private void setListener() {
        for (int i = 0; i < ratings.size(); i++) {
            final int finalI = i;
            ratings.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    updView(finalI);
                }
            });
        }
    }

    private void updView(int finalI) {
        //逻辑
        int newPoint = finalI + 1;
        if (newPoint == curPoint)//分数没变
            return;
        //改变
        for (int i = 0; i < ratings.size(); i++) {
            if (i <= finalI) {//之前的都是选择的
                ratings.get(i).setImageDrawable(full);
            } else if (i > finalI) {//之后的都是未选择的
                ratings.get(i).setImageDrawable(empty);
            }
        }
        curPoint = newPoint;
        pointTv.setText(curPoint + "分");
    }

    private void initRating() {
        for (ImageView imageView : ratings) {
            imageView.setImageDrawable(empty);
        }
        pointTv.setText(curPoint + "分");
    }

    private void initDrawable() {
        empty = getContext().getResources().getDrawable(R.drawable.rating_star_empty);
        full = getContext().getResources().getDrawable(R.drawable.rating_star_full);
    }

    private void addRating() {
        LinearLayout ratingContainer = new LinearLayout(getContext());
        ratingContainer.setOrientation(LinearLayout.HORIZONTAL);
        ratingContainer.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        params.addRule(LEFT_OF, pointTv.getId());
        addView(ratingContainer, params);
        addImageViews(ratingContainer);
    }

    private void addImageViews(LinearLayout ratingContainer) {
        ratings = new ArrayList<>();
        for (int i = 0; i < MAX_POINT; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, UIUtils.dp2px(getContext(), 30));
            params.weight = 1;
            ratingContainer.addView(imageView, params);
            ratings.add(imageView);
        }
    }

    private void addTextView() {
        pointTv = new TextView(getContext());
        pointTv.setId(generateViewId());
        pointTv.setSingleLine(true);
        pointTv.setEllipsize(TextUtils.TruncateAt.END);
        pointTv.setGravity(Gravity.CENTER);
        pointTv.setTextColor(getContext().getResources().getColor(R.color.orange));
        pointTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(CENTER_VERTICAL);
        params.leftMargin = UIUtils.dp2px(getContext(), 25);
        addView(pointTv, params);
    }
}
