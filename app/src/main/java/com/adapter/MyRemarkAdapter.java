package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.constant.CategoryEnum;
import com.group.AddUpdRemarkActivity;
import com.group.MerchantDetailActivity;
import com.group.R;
import com.leancloud.SafeDeleteCallback;
import com.model.Category;
import com.model.Remark;
import com.util.DateUtils;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.dialog.LoadingDialog;
import com.widget.dialog.MessageDialog;
import com.widget.remarkImagesLayout.RemarkImagesLayout;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

import java.util.List;

/**
 * Created by cwj on 16/4/11.
 * 我的评论adapter
 */
public class MyRemarkAdapter extends RecyclerViewAdapter<Remark> {

    public static final int UPD_REMARK_CODE = 0;

    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;

    public MyRemarkAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            final Remark remark = getDataItem(position);
            MyRemarkViewHolder viewHolder = (MyRemarkViewHolder) holder;
            //merchant layout
            viewHolder.merchantNameTv.setText(remark.getVoucher().getMerchant().getName());
            setCatIv(viewHolder.categoryIv, remark.getVoucher().getMerchant().getCategory());
            viewHolder.merchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入商家详情页
                    Intent intent = new Intent(context, MerchantDetailActivity.class);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, remark.getVoucher().getMerchant());
                    context.startActivity(intent);
                }
            });
            //rating
            viewHolder.ratingBar.setRating(remark.getPoint());
            //content
            viewHolder.contentTv.setText(remark.getContent());
            //images
            List<String> urls = remark.getImages();
            if (urls == null || urls.size() <= 0) {
                viewHolder.imagesLayout.clearData();
            } else {
                viewHolder.imagesLayout.resetData(urls);
            }
            //date
            viewHolder.dateTv.setText(DateUtils.getDateString(remark.getCreatedAt()));
            //edit
            viewHolder.editTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddUpdRemarkActivity.class);
                    intent.putExtra(AddUpdRemarkActivity.ORDER_KEY, remark.getOrder());
                    intent.putExtra(AddUpdRemarkActivity.VOUCHER_KEY, remark.getVoucher());
                    intent.putExtra(AddUpdRemarkActivity.MERCHANT_KEY, remark.getVoucher().getMerchant());
                    intent.putExtra(AddUpdRemarkActivity.REMARK_KEY, remark);
                    intent.putExtra(AddUpdRemarkActivity.IS_UPD_KEY, true);
                    if (context instanceof Activity)
                        ((Activity) context).startActivityForResult(intent, UPD_REMARK_CODE);
                }
            });
            //delete
            viewHolder.delTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRemark(remark);
                }
            });
        }
    }

    private void deleteRemark(final Remark remark) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(context);
            messageDialog.setTitle("删除评论");
            messageDialog.setMessage("是否删除评论");
            messageDialog.setNegativeText("取消");
            messageDialog.setPositiveText("删除");
        }
        messageDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(remark);
            }
        });
        messageDialog.show();
    }

    private void delete(final Remark remark) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show("删除评论中...");
        remark.deleteInBackground(new SafeDeleteCallback(context) {
            @Override
            protected void delete(AVException e) {
                if (e != null) {
                    Utils.showToast(context, "删除失败");
                } else {
                    removeData(remark);
                    Utils.showToast(context, "删除成功");
                }
                loadingDialog.cancel();
            }
        });
    }

    private void setCatIv(ImageView categoryIv, Category category) {
        CategoryEnum categoryEnum = CategoryEnum.getEnumMap(category);
        if (categoryEnum == null) {
            categoryIv.setImageDrawable(null);
            return;
        }
        categoryIv.setImageResource(categoryEnum.getSmallIconId());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new MyRemarkViewHolder(layoutInflater.inflate(R.layout.my_remark_item, parent, false));
    }

    public class MyRemarkViewHolder extends RecyclerView.ViewHolder {

        LinearLayout merchantLayout;
        ImageView categoryIv;
        TextView merchantNameTv;

        RatingBar ratingBar;
        TextView contentTv;
        RemarkImagesLayout imagesLayout;

        TextView dateTv;
        TextView editTv;
        TextView delTv;

        public MyRemarkViewHolder(View itemView) {
            super(itemView);
            merchantLayout = (LinearLayout) itemView.findViewById(R.id.my_remark_item_merchant_layout);
            categoryIv = (ImageView) itemView.findViewById(R.id.my_remark_item_category_iv);
            merchantNameTv = (TextView) itemView.findViewById(R.id.my_remark_item_merchant_name_tv);

            ratingBar = (RatingBar) itemView.findViewById(R.id.my_remark_item_rating);
            contentTv = (TextView) itemView.findViewById(R.id.my_remark_item_content);
            imagesLayout = (RemarkImagesLayout) itemView.findViewById(R.id.my_remark_item_image_layout);

            dateTv = (TextView) itemView.findViewById(R.id.my_remark_item_date_tv);
            editTv = (TextView) itemView.findViewById(R.id.my_remark_item_edit_tv);
            delTv = (TextView) itemView.findViewById(R.id.my_remark_item_del_tv);
            setBack();
        }

        private void setBack() {
            int gray = context.getResources().getColor(R.color.gray);
            int strokeWidth = UIUtils.dp2px(context, 1);
            int corner = UIUtils.dp2px(context, 3);
            editTv.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, corner, gray)
                    , new DrawableUtils.LayerStateDrawable(new int[]{}, corner, Color.WHITE, strokeWidth, strokeWidth, strokeWidth, strokeWidth, gray)));
            delTv.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, corner, gray)
                    , new DrawableUtils.LayerStateDrawable(new int[]{}, corner, Color.WHITE, strokeWidth, strokeWidth, strokeWidth, strokeWidth, gray)));
        }
    }
}
