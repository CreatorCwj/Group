package com.widget.remarkImagesLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.group.R;
import com.imageLoader.FileCache;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.dialog.SelectDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by cwj on 16/4/12.
 * 操作上传图片的view,两行,8张
 */
public class OperateImagesLayout extends LinearLayout {

    public static final int CAMERA_KEY = 0;
    public static final int PHOTO_KEY = 1;

    private static final int NUM_OF_ROW = 4;

    private LinearLayout firstLine;
    private LinearLayout secondLine;

    private Drawable cameraDrawable;
    private Drawable cancelDrawable;

    private File currentTmpFile;
    private int currentImages;

    private List<OperateImageItemView> itemViews = new ArrayList<>();

    private List<String> urls = new ArrayList<>();

    private SelectDialog selectDialog = new SelectDialog(getContext(), Arrays.asList("打开相机", "打开相册"));

    public OperateImagesLayout(Context context) {
        this(context, null);
    }

    public OperateImagesLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OperateImagesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OperateImagesLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDrawable();
        initView();
        setListener();
        setSelectDialog();
    }

    private void setSelectDialog() {
        selectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(getContext() instanceof Activity))
                    return;
                if (position == 0) {//打开相机
                    //创建临时文件
                    File dir = FileCache.getCacheDir();
                    if (dir == null) {
                        Utils.showToast(getContext(), "打开相机失败,可能是SD卡无法访问");
                        return;
                    }
                    currentTmpFile = new File(dir.getAbsolutePath() + File.separator + UUID.randomUUID() + ".png");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentTmpFile));//照片临时存储
                    ((Activity) getContext()).startActivityForResult(intent, CAMERA_KEY);
                } else if (position == 1) {//打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    ((Activity) getContext()).startActivityForResult(intent, PHOTO_KEY);
                }
            }
        });
    }

    public File getCurrentTmpFile() {
        return currentTmpFile;
    }

    private void initDrawable() {
        cameraDrawable = getContext().getResources().getDrawable(R.drawable.camera_icon);
        cancelDrawable = getContext().getResources().getDrawable(R.drawable.cancel_icon);
    }

    private void initView() {
        setOrientation(VERTICAL);
        addLines();
        addItems(firstLine);
        addItems(secondLine);
        initItemViews();
    }

    private void initItemViews() {
        for (OperateImageItemView itemView : itemViews) {//设置图片
            itemView.setClearDrawable(cameraDrawable);
            itemView.setCancelDrawable(cancelDrawable);
        }
        refresh();
    }

    private void addItems(LinearLayout line) {
        for (int i = 0; i < NUM_OF_ROW; i++) {
            //container
            RelativeLayout container = new RelativeLayout(getContext());
            LayoutParams containerParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            containerParams.weight = 1;
            line.addView(container, containerParams);
            //item
            OperateImageItemView itemView = new OperateImageItemView(getContext());
            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(UIUtils.dp2px(getContext(), 68), UIUtils.dp2px(getContext(), 68));
            itemParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            container.addView(itemView, itemParams);
            itemViews.add(itemView);
        }
    }

    private void addLines() {
        firstLine = new LinearLayout(getContext());
        firstLine.setOrientation(HORIZONTAL);
        firstLine.setGravity(Gravity.CENTER_VERTICAL);
        addView(firstLine, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        secondLine = new LinearLayout(getContext());
        secondLine.setOrientation(HORIZONTAL);
        secondLine.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dp2px(getContext(), 13);
        addView(secondLine, params);
    }

    private void setListener() {
        for (int i = 0; i < itemViews.size(); i++) {
            final OperateImageItemView itemView = itemViews.get(i);
            itemView.setOnClickListener(new OperateImageItemView.OnClickListener() {
                @Override
                public void onAddClick(OperateImageItemView imageItemView) {
                    selectDialog.show();
                }

                @Override
                public void onCancelClick(OperateImageItemView imageItemView) {
                    int index = -1;
                    for (int i = 0; i < itemViews.size(); i++) {
                        if (itemViews.get(i) == imageItemView) {
                            index = i;
                            break;
                        }
                    }
                    if (index >= 0 && index < urls.size()) {//移除,刷新
                        urls.remove(index);
                        refresh();
                    }
                }
            });
        }
    }

    private boolean isFully() {
        return currentImages >= itemViews.size();
    }

    public void addImage(String url) {
        if (isFully())//满了
            return;
        urls.add(url);
        refresh();
    }

    public int getCurrentImages() {
        return currentImages;
    }

    public List<String> getUrls() {
        return urls;
    }

    private void refresh() {
        currentImages = urls.size();//当前图片个数
        for (int i = 0; i < urls.size(); i++) {
            itemViews.get(i).setImage(urls.get(i));
        }
        if (!isFully()) {//没满则最后一个位置为可添加
            itemViews.get(currentImages).reset();
            for (int i = currentImages + 1; i < itemViews.size(); i++) {//后面的不可见
                itemViews.get(i).setVisibility(INVISIBLE);
            }
        }
    }
}
