package com.widget.viewPagers.imageViewPager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group.R;
import com.imageLoader.ImageLoader;
import com.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/2/9.
 * 可滑动查看图片的自定义view
 * 1.可设置导航点的位置(底部中间和底部右侧)
 * 2.可设置导航点的选中/未选中颜色
 * 3.可设置title的样式(导航点在右侧显示时会有,title将显示在底部左侧)
 * 4.无限循环播放,可手动暂停和滑动
 * 5.可多次设置不同的urls和titles
 * 6.可设置点击监听器
 * 7.可设置页数
 * 8.可设置默认图片
 * 9.自定义导航点view{@link PointView}可单独使用,绘制圆形可选view
 */
public class ImageViewPager extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private static final int DEFAULT_PAGE_NUM = 1;//默认多少页
    private static final int SLIDE_DELAY = 3000;//2秒间隔
    private static final int MIDDLE_PAGE = Integer.MAX_VALUE / 2;//开始页数

    /**
     * 底部中央
     */
    public static final int CENTER = 0;

    /**
     * 居底部右侧,左侧显示文本
     */
    public static final int RIGHT = 1;

    public interface OnItemClickListener {
        void onItemClick(ImageViewPager imageViewPager, int position, ImageView imageView);
    }

    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private List<PointView> pointViews;
    private List<String> urls;
    private List<String> titles;

    private RelativeLayout bottomLayout;//底部layout
    private LinearLayout pointsContainer;//pointViews的父布局
    private TextView bottomTextView;//底部文本

    private OnItemClickListener onItemClickListener;

    private int nums;
    private int textColor;
    private int textSize;
    private Drawable defaultDrawable;
    private int pointGravity;

    private MyHandler handler = new MyHandler();

    public ImageViewPager(Context context) {
        this(context, null);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);//解析属性
        initPointContainer();//创建point的布局
        initBottomLayout();//创建底部栏
        initImageViews();//创建ImageView
        initViewPager();//创建viewPager
        assembleView();//装载view
        startSlide();//开始滚动
    }

    private void assembleView() {
        addView(viewPager);
        addView(bottomLayout);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ImageViewPager);
            nums = typedArray.getInt(R.styleable.ImageViewPager_imageNum, DEFAULT_PAGE_NUM);
            textColor = typedArray.getColor(R.styleable.ImageViewPager_bottomTextColor, Color.WHITE);
            textSize = typedArray.getDimensionPixelSize(R.styleable.ImageViewPager_bottomTextSize, UIUtils.sp2px(getContext(), 15));
            defaultDrawable = typedArray.getDrawable(R.styleable.ImageViewPager_defaultImage);
            pointGravity = typedArray.getInt(R.styleable.ImageViewPager_pointGravity, CENTER);
            typedArray.recycle();
        }
        initPointView(attrs);//解析创建pointView数组
    }

    private void initPointView(AttributeSet attrs) {
        pointViews = new ArrayList<>();
        for (int i = 0; i < nums; ++i) {
            PointView pointView = new PointView(getContext(), attrs);
            pointView.setLayoutParams(new LinearLayout.LayoutParams(UIUtils.dp2px(getContext(), 7), UIUtils.dp2px(getContext(), 7)));
            pointViews.add(pointView);
        }
    }

    private void initPointContainer() {
        int gap = UIUtils.dp2px(getContext(), 5);//间隔5dp
        pointsContainer = new LinearLayout(getContext());
        pointsContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pointsContainer.setOrientation(LinearLayout.HORIZONTAL);
        pointsContainer.setBackgroundColor(Color.TRANSPARENT);
        pointsContainer.setPadding(gap, gap, gap, gap);
        for (int i = 0; i < nums; ++i) {
            PointView pointView = pointViews.get(i);
            if (i != 0) {//保证间隔相等
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pointView.getLayoutParams();
                params.leftMargin = gap;
            }
            pointsContainer.addView(pointView);
        }
        pointsContainer.setId(generateViewId());
    }

    private void initBottomLayout() {
        bottomLayout = new RelativeLayout(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomLayout.setLayoutParams(params);
        if (pointGravity == CENTER) {//居中
            bottomLayout.setBackgroundColor(Color.TRANSPARENT);
            ((LayoutParams) pointsContainer.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
            bottomLayout.addView(pointsContainer);
        } else if (pointGravity == RIGHT) {
            bottomLayout.setBackgroundColor(Color.parseColor("#50000000"));
            ((LayoutParams) pointsContainer.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ((LayoutParams) pointsContainer.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
            bottomLayout.addView(pointsContainer);
            initTextView();
            bottomLayout.addView(bottomTextView);
        }
    }

    private void initTextView() {
        int gap = UIUtils.dp2px(getContext(), 5);//间隔5dp
        bottomTextView = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.LEFT_OF, pointsContainer.getId());
        bottomTextView.setLayoutParams(params);
        bottomTextView.setPadding(gap, gap, gap, gap);
        bottomTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        bottomTextView.setTextColor(textColor);
        bottomTextView.setSingleLine(true);
        bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
        bottomTextView.setText("我的照片");
    }

    private void initImageViews() {
        imageViews = new ArrayList<>();
        for (int i = 0; i < nums; ++i) {
            final ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (defaultDrawable != null)
                imageView.setImageDrawable(defaultDrawable);
            imageViews.add(imageView);
        }
        setImageViewListener();//点击监听器
    }

    private void setImageViewListener() {
        for (int i = 0; i < imageViews.size(); ++i) {
            final int index = i;
            imageViews.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(ImageViewPager.this, index, imageViews.get(index));
                }
            });
        }
    }

    private void initViewPager() {
        viewPager = new ViewPager(getContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setAdapter(new ImageViewPagerAdapter(imageViews));
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopSlide();
                        break;
                    default:
                        startSlide();
                        break;
                }
                return false;
            }
        });
        viewPager.setCurrentItem(MIDDLE_PAGE);//从中间开始
    }

    private void startSlide() {
        handler.postDelayed(runnable, SLIDE_DELAY);
    }

    private void stopSlide() {
        handler.removeCallbacks(runnable);
    }

    /**
     * 设置监听器
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置圆点边框宽度,px
     *
     * @param width
     */
    public void setPointStrokeWidth(int width) {
        for (int i = 0; i < pointViews.size(); ++i) {
            pointViews.get(i).setStrokeWidth(width);
        }
    }

    /**
     * 设置圆点边框颜色
     *
     * @param color
     */
    public void setPointStrokeColor(int color) {
        for (int i = 0; i < pointViews.size(); ++i) {
            pointViews.get(i).setStrokeColor(color);
        }
    }

    /**
     * 设置point选中颜色
     *
     * @param selectColor
     */
    public void setSelectColor(int selectColor) {
        for (int i = 0; i < pointViews.size(); ++i) {
            pointViews.get(i).setSelectColor(selectColor);
        }
    }

    /**
     * 设置point未选中颜色
     *
     * @param unSelectColor
     */
    public void setUnSelectColor(int unSelectColor) {
        for (int i = 0; i < pointViews.size(); ++i) {
            pointViews.get(i).setUnSelectColor(unSelectColor);
        }
    }

    /**
     * 设置title颜色
     *
     * @param titleColor
     */
    public void setTitleColor(int titleColor) {
        if (bottomTextView != null)
            bottomTextView.setTextColor(titleColor);
    }

    /**
     * 设置title大小,单位sp
     *
     * @param spSize
     */
    public void setTitleSize(int spSize) {
        if (bottomTextView != null)
            bottomTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, spSize);
    }

    /**
     * 设置显示文字
     * note:设置进来时要更新一下当前的显示
     *
     * @param titles
     */
    public void setTitles(List<String> titles) {
        this.titles = titles;
        updateTitle(getRealPosition(viewPager.getCurrentItem()));
    }

    /**
     * 开始加载图片,可多次提交不同urls
     *
     * @param urls
     */
    public void setUrls(List<String> urls) {
        this.urls = urls;
        startLoad();
    }

    private void startLoad() {
        for (int i = 0; i < nums && urls != null && i < urls.size(); ++i) {
            ImageLoader.displayImage(imageViews.get(i), urls.get(i));
        }
    }

    //得到真实的position
    private int getRealPosition(int position) {
        int real = (position - MIDDLE_PAGE) % nums;
        if (real < 0) {
            real = nums + real;
        }
        return real;
    }

    //更新title
    private void updateTitle(int realPosition) {
        if (bottomTextView != null && titles != null && realPosition < titles.size()) {
            bottomTextView.setText(titles.get(realPosition));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        position = getRealPosition(position);
        //更新pointView
        for (int i = 0; i < nums; ++i) {
            if (i == position)
                pointViews.get(i).setSelected(true);
            else pointViews.get(i).setSelected(false);
        }
        //更新显示文字
        updateTitle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int cur = viewPager.getCurrentItem() + 1;
            viewPager.setCurrentItem(cur, true);
            startSlide();//循环
        }
    };

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private class ImageViewPagerAdapter extends PagerAdapter {

        private List<ImageView> list;

        public ImageViewPagerAdapter(List<ImageView> list) {
            this.list = list;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = getRealPosition(position);
            ImageView imageView = list.get(position);
            ViewParent vp = imageView.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
