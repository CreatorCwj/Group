package com.widget.rlrView.other;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/1/22.
 * 自定义分割线:支持高度和颜色
 */
public class Divider extends RecyclerView.ItemDecoration {

    private int height;//高度,px
    private Drawable drawable;

    public Divider(int height, int color) {
        this.height = height;
        drawable = new ColorDrawable(color);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            //footer和deader不添加divider
            int position = parent.getChildAdapterPosition(child);
            RecyclerViewAdapter adapter = (RecyclerViewAdapter) parent.getAdapter();
            if (adapter != null && (adapter.getItemViewType(position) == RecyclerViewAdapter.FOOTER
                    || adapter.getItemViewType(position) == RecyclerViewAdapter.HEADER)) {
                continue;
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + height;
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, height);
    }
}
