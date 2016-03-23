package com.group;

import android.os.Bundle;

import com.dao.generate.Category;
import com.fragment.MerchantFragment;
import com.group.base.BaseFragmentActivity;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_merchant)
public class MerchantActivity extends BaseFragmentActivity {

    private MerchantFragment merchantFragment = new MerchantFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        Category initCat = (Category) getIntent().getSerializableExtra(MerchantFragment.INIT_CATEGORY_KEY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerchantFragment.INIT_CATEGORY_KEY, initCat);
        bundle.putSerializable(MerchantFragment.NEED_BACK_KEY, true);
        merchantFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.merchant_container, merchantFragment)
                .commitAllowingStateLoss();
    }

}
