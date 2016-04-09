package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.group.MerchantDetailActivity;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeDeleteCallback;
import com.model.Area;
import com.model.Category;
import com.model.Collection;
import com.model.Merchant;
import com.util.Utils;
import com.widget.RoundImageView;
import com.widget.dialog.LoadingDialog;
import com.widget.dialog.MessageDialog;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

/**
 * Created by cwj on 16/4/10.
 * 收藏adapter
 */
public class CollectionAdapter extends RecyclerViewAdapter<Collection> implements LoadMoreRecyclerView.OnItemClickListener, LoadMoreRecyclerView.OnItemLongClickListener {

    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;

    public CollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Collection collection = getDataItem(position);
            Merchant merchant = collection.getMerchant();
            CollectionViewHolder viewHolder = (CollectionViewHolder) holder;
            viewHolder.merchantNameTv.setText(merchant.getName());
            viewHolder.remarkNumTv.setText(merchant.getRemarkNum() + "评论");
            viewHolder.averageTv.setText("人均¥" + merchant.getAverage());
            viewHolder.ratingBar.setRating((float) merchant.getPoint());
            viewHolder.categoryTv.setText(getCategoryName(merchant));
            viewHolder.areaTv.setText(getAreaName(merchant));
            if (merchant.getImages() != null && merchant.getImages().size() > 0)
                ImageLoader.displayImage(viewHolder.merchantIv, merchant.getImages().get(0));//加载第一张图片
        }
    }

    private String getAreaName(Merchant merchant) {
        Area subArea = merchant.getSubArea();
        Area area = merchant.getArea();
        if (subArea != null)//优先显示子商圈
            return subArea.getName();
        if (area != null)
            return area.getName();
        return "";
    }

    private String getCategoryName(Merchant merchant) {
        Category category = merchant.getCategory();
        Category subCat = merchant.getSubCategory();
        if (subCat != null)//优先显示子品类
            return subCat.getName();
        if (category != null)
            return category.getName();
        return "";
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new CollectionViewHolder(layoutInflater.inflate(R.layout.collection_item, parent, false));
    }

    @Override
    public void onItemClick(int position) {
        //进入商家
        Intent intent = new Intent(context, MerchantDetailActivity.class);
        intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, getDataItem(position).getMerchant());
        context.startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(context);
            messageDialog.setTitle("删除收藏");
            messageDialog.setMessage("是否删除收藏");
            messageDialog.setNegativeText("取消");
            messageDialog.setPositiveText("删除");
        }
        //删除收藏
        final Collection collection = getDataItem(position);
        messageDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCollection(collection);
            }
        });
        messageDialog.show();
    }

    private void deleteCollection(Collection collection) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show("删除收藏中...");
        collection.deleteInBackground(new SafeDeleteCallback(context) {
            @Override
            protected void delete(AVException e) {
                if (e != null) {
                    Utils.showToast(context, "删除失败");
                } else {
                    Utils.showToast(context, "删除成功");
                }
                loadingDialog.cancel();
            }
        });
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {

        RoundImageView merchantIv;
        RatingBar ratingBar;
        TextView merchantNameTv;
        TextView remarkNumTv;
        TextView averageTv;
        TextView categoryTv;
        TextView areaTv;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            merchantIv = (RoundImageView) itemView.findViewById(R.id.collection_item_iv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.collection_item_rating);
            merchantNameTv = (TextView) itemView.findViewById(R.id.collection_item_merchant_name_tv);
            remarkNumTv = (TextView) itemView.findViewById(R.id.collection_item_remark_num);
            averageTv = (TextView) itemView.findViewById(R.id.collection_item_average);
            categoryTv = (TextView) itemView.findViewById(R.id.collection_item_category);
            areaTv = (TextView) itemView.findViewById(R.id.collection_item_area);
        }
    }
}
