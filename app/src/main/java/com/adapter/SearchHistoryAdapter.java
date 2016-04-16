package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dao.dbHelpers.SearchRecordHelper;
import com.dao.generate.SearchRecord;
import com.fragment.SearchMerchantFragment;
import com.google.inject.Inject;
import com.group.R;
import com.group.SearchMerchantActivity;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

/**
 * Created by cwj on 16/4/16.
 * 搜索历史的adapter
 */
public class SearchHistoryAdapter extends RecyclerViewAdapter<SearchRecord> implements LoadMoreRecyclerView.OnItemClickListener {

    @Inject
    private SearchRecordHelper searchRecordHelper;

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            SearchRecord searchRecord = getDataItem(position);
            SearchHistoryViewHolder viewHolder = (SearchHistoryViewHolder) holder;
            viewHolder.nameTv.setText(searchRecord.getName());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new SearchHistoryViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onItemClick(int position) {
        SearchRecord searchRecord = getDataItem(position);
        //跳转到搜索页面
        Intent intent = new Intent(context, SearchMerchantActivity.class);
        intent.putExtra(SearchMerchantFragment.SEARCH_WORD_KEY, searchRecord.getName());
        context.startActivity(intent);
        //更新搜索历史本地数据库
        searchRecordHelper.addRecentlyUsed(searchRecord.getName());
    }

    public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.search_item_name_tv);
        }
    }
}
