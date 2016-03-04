package com.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.adapter.base.BaseFragmentPagerAdapter;
import com.fragment.BigBitmapFragment;
import com.imageLoader.ImageLoader;
import com.widget.dialog.SelectDialog;
import com.widget.viewPagers.ClickViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_big_bitmap)
public class BigBitmapActivity extends RoboFragmentActivity implements ViewPager.OnPageChangeListener {

    public static final String INTENT_URL_KEY = "imageUrls";
    public static final String INTENT_FIRST_PAGE_KEY = "firstPage";

    @InjectView(R.id.bigBitmapVP)
    private ClickViewPager viewPager;

    @InjectView(R.id.bigBitmapNum)
    private TextView imageNum;

    private List<String> imgUrls;
    private int firstPage;//当前(第一个)显示页

    private SelectDialog selectDialog;//长按弹出框
    private String[] items = new String[]{"保存图片"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        initTextView();
        initViewPager();
        initSelectDialog();
    }

    private void initSelectDialog() {
        selectDialog = new SelectDialog(this, Arrays.asList(items));
        selectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//保存图片
                    ImageLoader.saveImage(BigBitmapActivity.this, imgUrls.get(viewPager.getCurrentItem()));
                }
            }
        });
    }

    private void initTextView() {
        if (imgUrls == null || imgUrls.size() < 1) {
            imageNum.setVisibility(View.GONE);
        } else {
            imageNum.setText((firstPage + 1) + "/" + imgUrls.size());//初始化为第一页
        }
    }

    private void initViewPager() {
        if (imgUrls != null && imgUrls.size() > 0) {
            List<BigBitmapFragment> fragments = new ArrayList<>();
            for (String url : imgUrls) {
                fragments.add(BigBitmapFragment.newInstance(url));
            }
            new BaseFragmentPagerAdapter<>(this, viewPager, fragments, firstPage);
            setViewPagerListener();
        }
    }

    private void setViewPagerListener() {
        viewPager.addOnPageChangeListener(this);
        //点击关闭
        viewPager.setOnClickListener(new ClickViewPager.OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        //长按保存
        viewPager.setOnLongClickListener(new ClickViewPager.OnLongClickListener() {
            @Override
            public void onLongClick() {
                selectDialog.show();
            }
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_URL_KEY)) {
            imgUrls = intent.getStringArrayListExtra(INTENT_URL_KEY);
        }
        if (intent != null && intent.hasExtra(INTENT_FIRST_PAGE_KEY)) {
            firstPage = intent.getIntExtra(INTENT_FIRST_PAGE_KEY, 0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //切换页后更新textView
        imageNum.setText((position + 1) + "/" + imgUrls.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
