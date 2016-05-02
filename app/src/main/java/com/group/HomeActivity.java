package com.group;

import android.os.Bundle;

import com.adapter.SlideFragmentAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.dao.dbHelpers.AreaHelper;
import com.dao.dbHelpers.CategoryHelper;
import com.fragment.HomeFragment;
import com.fragment.MerchantFragment;
import com.fragment.MineFragment;
import com.fragment.MoreFragment;
import com.fragment.base.BaseSlideFragment;
import com.google.inject.Inject;
import com.group.base.BaseFragmentActivity;
import com.leancloud.SafeFindCallback;
import com.model.Category;
import com.util.AppSetting;
import com.util.Utils;
import com.widget.radio.RadioLayout;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_home)
public class HomeActivity extends BaseFragmentActivity {

    private final int DELAY_TIME = 1500;
    private long preTime = -1;

    @InjectView(R.id.main_radioLayout)
    private RadioLayout radioLayout;

    @Inject
    private CategoryHelper categoryHelper;

    @Inject
    private AreaHelper areaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进入一次应用(选择城市后)
        AppSetting.addStartCount();
        //每次进入时判断是否加载品类做缓存
        loadCategory();
        //添加界面
        List<BaseSlideFragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MerchantFragment());
        fragments.add(new MineFragment());
        fragments.add(new MoreFragment());
        new SlideFragmentAdapter<>(this, fragments, radioLayout, R.id.main_content, false);
    }

    private void loadCategory() {
        if (!categoryHelper.isEmpty())
            return;
        AVQuery<Category> query = AVQuery.getQuery(Category.class);
        query.setLimit(1000);
        query.findInBackground(new SafeFindCallback<Category>(this) {

            @Override
            public void findResult(List<Category> objects, AVException e) {
                if (e == null) {
                    //转成本地类
                    List<com.dao.generate.Category> categories = new ArrayList<>();
                    for (Category category : objects) {
                        categories.add(new com.dao.generate.Category(category.getCategoryId(), category.getName(), category.getParentId()));
                    }
                    //放入数据库
                    categoryHelper.insertData(categories);
                }
            }
        });
    }

    @Override
    public boolean onBack() {
        if ((System.currentTimeMillis() - preTime) >= DELAY_TIME) {//第一次按,提示,并重置第一次按的时间
            Utils.showToast(this, "再按一次退出应用");
            preTime = System.currentTimeMillis();
        } else {//第二次按有效,退出界面和应用
            finish();
        }
        return true;
    }
}
