package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.constant.GradeEnum;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.model.Remark;
import com.model.User;
import com.util.DateUtils;
import com.widget.RemarkImagesLayout;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.List;

/**
 * Created by cwj on 16/3/27.
 * 评论adapter
 */
public class RemarkAdapter extends RecyclerViewAdapter<Remark> {

    public RemarkAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Remark remark = getDataItem(position);
            User user = remark.getUser();
            RemarkViewHolder viewHolder = (RemarkViewHolder) holder;
            //user img
            if (TextUtils.isEmpty(user.getImageUrl()))
                viewHolder.userImg.setImageResource(R.drawable.no_user_icon);
            else ImageLoader.displayImage(viewHolder.userImg, user.getImageUrl());
            //name
            if (TextUtils.isEmpty(user.getDisplayName()))
                viewHolder.username.setText(user.getUsername());
            else viewHolder.username.setText(user.getDisplayName());
            //lv
            int value = user.getGrowthValue();
            viewHolder.lv.setText("Lv" + GradeEnum.getGradeByValue(value).getGrade());
            //rating
            viewHolder.ratingBar.setRating(remark.getPoint());
            //date
            viewHolder.date.setText(DateUtils.getDateString(remark.getCreatedAt()));
            //content
            viewHolder.content.setText(remark.getContent());
            //images
            List<String> urls = remark.getImages();
            if (urls == null || urls.size() <= 0) {
                viewHolder.imagesLayout.clearData();
            } else {
                viewHolder.imagesLayout.resetData(urls);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new RemarkViewHolder(layoutInflater.inflate(R.layout.remark_item, parent, false));
    }

    public class RemarkViewHolder extends RecyclerView.ViewHolder {

        ImageView userImg;
        TextView username;
        TextView lv;
        TextView date;
        TextView content;
        RatingBar ratingBar;
        RemarkImagesLayout imagesLayout;

        public RemarkViewHolder(View itemView) {
            super(itemView);
            userImg = (ImageView) itemView.findViewById(R.id.remark_item_user_img);
            username = (TextView) itemView.findViewById(R.id.remark_item_username);
            lv = (TextView) itemView.findViewById(R.id.remark_item_lv);
            date = (TextView) itemView.findViewById(R.id.remark_item_date);
            content = (TextView) itemView.findViewById(R.id.remark_item_content);
            ratingBar = (RatingBar) itemView.findViewById(R.id.remark_item_rating);
            imagesLayout = (RemarkImagesLayout) itemView.findViewById(R.id.remark_item_image_layout);
        }
    }
}
