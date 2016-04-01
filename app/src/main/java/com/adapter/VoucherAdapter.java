package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constant.TagEnum;
import com.group.R;
import com.group.VoucherDetailActivity;
import com.imageLoader.ImageLoader;
import com.model.Merchant;
import com.model.Voucher;
import com.util.UIUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

import java.util.List;

/**
 * Created by cwj on 16/3/26.
 * 优惠券adapter
 */
public class VoucherAdapter extends RecyclerViewAdapter<Voucher> implements LoadMoreRecyclerView.OnItemClickListener {

    private Merchant merchant;

    public VoucherAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Voucher voucher = getDataItem(position);
            VoucherViewHolder viewHolder = (VoucherViewHolder) holder;
            viewHolder.nameTv.setText(voucher.getName());
            viewHolder.soldTv.setText("已售" + voucher.getSellNum());
            viewHolder.curPriceTv.setText("" + voucher.getCurrentPrice());
            viewHolder.oriPriceTv.setText("¥" + voucher.getOriginPrice());
            setTag(viewHolder.tagTv, voucher, position);//tag
            setTags(viewHolder.tagsLayout, voucher);//tags
            setImage(viewHolder.imageView, voucher);//image
        }
    }

    private void setTags(LinearLayout tagsLayout, Voucher voucher) {
        tagsLayout.removeAllViews();
        if (voucher.getRewardLottery() != null) {
            TextView textView = TagEnum.getTagView(context, TagEnum.SEND_LOTTERY.getId());
            if (textView != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(context, 16), UIUtils.dp2px(context, 16));
                params.leftMargin = UIUtils.dp2px(context, 8);
                tagsLayout.addView(textView, params);
            }
        }
        if (voucher.getRewardPoint() != null) {
            TextView textView = TagEnum.getTagView(context, TagEnum.SEND_POINT.getId());
            if (textView != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(context, 16), UIUtils.dp2px(context, 16));
                params.leftMargin = UIUtils.dp2px(context, 8);
                tagsLayout.addView(textView, params);
            }
        }
    }

    private void setImage(ImageView imageView, Voucher voucher) {
        List<String> imgs = voucher.getImages();
        if (imgs != null && imgs.size() > 0) {//load first
            imageView.setVisibility(View.VISIBLE);
            ImageLoader.displayImage(imageView, imgs.get(0));
        } else {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
        }
    }

    private void setTag(TextView tagTv, Voucher voucher, int position) {
        int tagId = voucher.getTag();
        if (position - 1 < 0 || getDataItem(position - 1).getTag() != tagId) {//上一个和其一样或其为第一个,显示tag,否则不显示
            tagTv.setVisibility(View.VISIBLE);
            tagTv.setText(TagEnum.getTagText(tagId));
            tagTv.setBackground(TagEnum.getTagBack(context, tagId));
        } else {
            tagTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new VoucherViewHolder(layoutInflater.inflate(R.layout.voucher_item, parent, false));
    }

    @Override
    public void onItemClick(int position) {
        //跳转到voucher详情页
        Intent intent = new Intent(context, VoucherDetailActivity.class);
        intent.putExtra(VoucherDetailActivity.VOUCHER_KEY, getDataItem(position));
        intent.putExtra(VoucherDetailActivity.MERCHANT_KEY, merchant);
        context.startActivity(intent);
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {

        TextView tagTv;
        TextView nameTv;
        TextView soldTv;
        TextView curPriceTv;
        TextView oriPriceTv;
        ImageView imageView;
        LinearLayout tagsLayout;

        public VoucherViewHolder(View itemView) {
            super(itemView);
            tagTv = (TextView) itemView.findViewById(R.id.voucher_tag_tv);
            nameTv = (TextView) itemView.findViewById(R.id.voucher_name_tv);
            soldTv = (TextView) itemView.findViewById(R.id.voucher_sold_tv);
            curPriceTv = (TextView) itemView.findViewById(R.id.voucher_cur_price_tv);
            oriPriceTv = (TextView) itemView.findViewById(R.id.voucher_origin_price_tv);
            imageView = (ImageView) itemView.findViewById(R.id.voucher_img);
            tagsLayout = (LinearLayout) itemView.findViewById(R.id.voucher_tags);
        }
    }
}
