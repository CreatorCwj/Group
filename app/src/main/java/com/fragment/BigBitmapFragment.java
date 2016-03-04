package com.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fragment.base.BaseViewPagerFragment;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.imageLoader.listener.ImageProgressStateListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.RoundProgressBar;

import roboguice.inject.InjectView;

public class BigBitmapFragment extends BaseViewPagerFragment {

    private static final String INTENT_URL_KEY = "imgUrl";

    public static BigBitmapFragment newInstance(String imgUrl) {
        Bundle args = new Bundle();
        args.putString(INTENT_URL_KEY, imgUrl);
        BigBitmapFragment fragment = new BigBitmapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.bitBitmapIV)
    private ImageView imageView;

    @InjectView(R.id.bitBitmapProgress)
    private RoundProgressBar progressCircle;

    private String imgUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgUrl = getArguments().getString(INTENT_URL_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_big_bitmap, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageLoader.displayImage(imageView, imgUrl, new ImageProgressStateListener() {
            @Override
            public void onProgress(ImageView imageView, String imgUrl, int current, int total, int progress) {
                super.onProgress(imageView, imgUrl, current, total, progress);
                //设置进度
                progressCircle.setTextIsDisplayable(true);
                progressCircle.setProgress(progress);
            }

            @Override
            public void onLoadingStarted(ImageView imageView, String imgUrl) {
                super.onLoadingStarted(imageView, imgUrl);
                startLoad();
            }

            @Override
            public void onLoadingFailed(ImageView imageView, String imgUrl, FailReason.FailType failType) {
                super.onLoadingFailed(imageView, imgUrl, failType);
                //会用默认的option里指定的失败图片
                Utils.showToast(getActivity(), "加载图片失败");
            }

            @Override
            public void onLoadingComplete(ImageView imageView, String imgUrl, Bitmap loadedImage) {
                super.onLoadingComplete(imageView, imgUrl, loadedImage);
                //要根据图片大小决定iv大小
                adjustImageView(loadedImage);
            }

            @Override
            public void onLoadingCancelled(ImageView imageView, String imgUrl) {
                super.onLoadingCancelled(imageView, imgUrl);
                Utils.showToast(getActivity(), "加载图片失败");
            }

            @Override
            public void onLoadingFinally(ImageView imageView, String imgUrl) {
                super.onLoadingFinally(imageView, imgUrl);
                loaded();//加载最终要执行的
            }
        });
    }

    /**
     * 1.宽度为原来的4倍或屏幕宽度(直接拉伸到全屏可能会模糊)
     * 2.宽高成比例
     * 3.如果高度已经超过屏幕可用高度,那么可以滚动,正常设置(marginTop为0)
     * 4.如果高度没用超过屏幕可用高度,那么就要设置居中
     *
     * @param loadedImage
     */
    private void adjustImageView(Bitmap loadedImage) {
        int[] screenSize = UIUtils.getScreenWidthHeightPX(getActivity());
        //width
        int newWidth = loadedImage.getWidth() * 4;
        if (newWidth > screenSize[0])
            newWidth = screenSize[0];
        //height
        float radius = (float) loadedImage.getHeight() / loadedImage.getWidth();
        int newHeight = (int) (newWidth * radius);
        //setIV
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        if (newHeight >= screenSize[1]) {
            params.topMargin = 0;
        } else {
            params.topMargin = (screenSize[1] - newHeight) / 2;
        }
        imageView.setLayoutParams(params);
    }

    private void startLoad() {
        progressCircle.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        //一开始不显示进度
        progressCircle.setProgress(100);
        progressCircle.setTextIsDisplayable(false);
    }

    private void loaded() {
        progressCircle.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }

}
