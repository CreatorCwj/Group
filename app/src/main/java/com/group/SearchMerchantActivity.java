package com.group;

import android.os.Bundle;

import com.fragment.SearchMerchantFragment;
import com.group.base.BaseFragmentActivity;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_merchant)
public class SearchMerchantActivity extends BaseFragmentActivity {

    private SearchMerchantFragment merchantFragment = new SearchMerchantFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        String searchWord = getIntent().getStringExtra(SearchMerchantFragment.SEARCH_WORD_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(SearchMerchantFragment.SEARCH_WORD_KEY, searchWord);
        merchantFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.merchant_container, merchantFragment)
                .commitAllowingStateLoss();
    }

}
