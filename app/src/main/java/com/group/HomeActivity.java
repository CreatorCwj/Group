package com.group;

import android.os.Bundle;

import com.group.base.BaseFragmentActivity;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_home)
public class HomeActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
