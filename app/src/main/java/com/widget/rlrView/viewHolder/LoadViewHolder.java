package com.widget.rlrView.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.group.R;

/**
 * Created by cwj on 16/1/17.
 * FOOTER的viewHolder,可以在此做一些对于FOOTER的操作
 */
public class LoadViewHolder extends RecyclerView.ViewHolder {

    private ProgressBar loadMoreProgressBar;
    private TextView loadMoreText;

    public LoadViewHolder(View itemView) {
        super(itemView);
        loadMoreProgressBar = (ProgressBar) itemView.findViewById(R.id.circleProgressBar);
        loadMoreText = (TextView) itemView.findViewById(R.id.loadMoreText);
    }

    /**
     * 设置加载状态决定显示
     *
     * @param isLoading
     */
    public void setLoadState(boolean isLoading) {
        if (isLoading) {//刷新中
            loadMoreProgressBar.setVisibility(View.VISIBLE);
            loadMoreText.setVisibility(View.GONE);
        } else {
            loadMoreProgressBar.setVisibility(View.GONE);
            loadMoreText.setVisibility(View.VISIBLE);
        }
    }
}
