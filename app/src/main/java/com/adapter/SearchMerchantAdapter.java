package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dao.dbHelpers.SearchRecordHelper;
import com.google.inject.Inject;
import com.group.MerchantDetailActivity;
import com.group.R;
import com.model.Merchant;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

/**
 * Created by cwj on 16/4/16.
 * 搜索商家的adapter
 */
public class SearchMerchantAdapter extends RecyclerViewAdapter<Merchant> implements LoadMoreRecyclerView.OnItemClickListener {

    @Inject
    private SearchRecordHelper searchRecordHelper;

    public SearchMerchantAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Merchant merchant = getDataItem(position);
            SearchMerchantViewHolder viewHolder = (SearchMerchantViewHolder) holder;
            viewHolder.nameTv.setText(merchant.getName());
            viewHolder.catAreaTv.setText(getCatAreaName(merchant));
            viewHolder.addressTv.setText(merchant.getAddress());
        }
    }

    private String getCatAreaName(Merchant merchant) {
        StringBuilder sb = new StringBuilder("");
        if (merchant.getArea() != null)
            sb.append(merchant.getArea().getName());
        if (merchant.getSubArea() != null)
            sb.append("/").append(merchant.getSubArea().getName());
        sb.append("    ");
        if (merchant.getCategory() != null)
            sb.append(merchant.getCategory().getName());
        if (merchant.getSubCategory() != null)
            sb.append("/").append(merchant.getSubCategory().getName());
        return sb.toString();
    }

    @Override
    public void onItemClick(int position) {
        Merchant merchant = getDataItem(position);
        //调到商家详情页
        Intent intent = new Intent(context, MerchantDetailActivity.class);
        intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, merchant);
        context.startActivity(intent);
        //更新搜索历史本地数据库
        searchRecordHelper.addRecentlyUsed(merchant.getName());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new SearchMerchantViewHolder(layoutInflater.inflate(R.layout.search_merchant_item, parent, false));
    }

    public class SearchMerchantViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView catAreaTv;
        TextView addressTv;

        public SearchMerchantViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.search_merchant_item_name_tv);
            catAreaTv = (TextView) itemView.findViewById(R.id.search_merchant_item_cat_area_tv);
            addressTv = (TextView) itemView.findViewById(R.id.search_merchant_item_address_tv);
        }
    }
}
