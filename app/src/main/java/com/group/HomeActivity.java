package com.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adapter.SlideFragmentAdapter;
import com.fragment.TestFragment;
import com.group.base.BaseFragmentActivity;
import com.widget.radio.RadioLayout;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_home)
public class HomeActivity extends BaseFragmentActivity {

    @InjectView(R.id.main_radioLayout)
    private RadioLayout radioLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            fragments.add(TestFragment.getInstance("pos:" + i));
        }
        new SlideFragmentAdapter<>(this, fragments, radioLayout, R.id.main_content);
    }

}
