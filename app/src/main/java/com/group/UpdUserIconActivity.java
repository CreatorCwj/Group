package com.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.constant.CloudFunction;
import com.group.base.BaseActivity;
import com.imageLoader.FileCache;
import com.imageLoader.ImageLoader;
import com.imageLoader.listener.ImageProgressStateListener;
import com.leancloud.SafeFunctionCallback;
import com.leancloud.SafeSaveCallback;
import com.model.User;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.util.Utils;
import com.widget.CustomToolBar;
import com.widget.RoundImageView;
import com.widget.dialog.SelectDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_upd_user_icon)
public class UpdUserIconActivity extends BaseActivity implements View.OnClickListener {

    private static final int CAMERA_KEY = 0;
    private static final int PHOTO_KEY = 1;

    @InjectView(R.id.upd_user_icon_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.upd_user_icon_iv)
    private RoundImageView imageView;

    @InjectView(R.id.upd_user_icon_btn)
    private Button selectBtn;

    private SelectDialog selectDialog;

    private Uri tmpUri;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInfo();
        setListener();
    }

    private void setInfo() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null && !TextUtils.isEmpty(user.getImageUrl())) {
            ImageLoader.displayImage(imageView, user.getImageUrl());
        } else {
            imageView.setImageResource(R.drawable.no_user_icon);
        }
    }

    private void setListener() {
        selectBtn.setOnClickListener(this);
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit
                submit();
            }
        });
    }

    private void submit() {
        if (AVUser.getCurrentUser() == null)
            return;
        if (imageUri == null) {
            Utils.showToast(this, "请选择图片");
            return;
        }
        showLoadingDialog("上传头像...");
        loadBitmapAndSave();
    }

    private void loadBitmapAndSave() {
        ImageLoader.loadImage(imageUri.toString(), new ImageProgressStateListener() {

            @Override
            public void onLoadingFailed(ImageView imageView, String imgUrl, FailReason.FailType failType) {
                Utils.showToast(UpdUserIconActivity.this, "上传失败:加载图片出错");
                cancelLoadingDialog();
            }

            @Override
            public void onLoadingComplete(ImageView imageView, String imgUrl, Bitmap loadedImage) {
                if (loadedImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    loadedImage.compress(Bitmap.CompressFormat.PNG, 70, baos);
                    AVFile file = new AVFile("user_icon.png", baos.toByteArray());
                    saveFile(file);
                } else {
                    Utils.showToast(UpdUserIconActivity.this, "上传失败:加载图片出错");
                    cancelLoadingDialog();
                }
            }
        });
    }

    private void saveFile(final AVFile file) {
        try {
            AVObject.saveFileBeforeSave(Arrays.asList(file), false, new SafeSaveCallback(this) {
                @Override
                public void save(AVException e) {
                    if (e != null) {
                        Utils.showToast(UpdUserIconActivity.this, "上传头像失败");
                        cancelLoadingDialog();
                    } else {//success upload file
                        saveUser(file);
                    }
                }
            });
        } catch (AVException e) {
            e.printStackTrace();
            Utils.showToast(this, "上传头像失败");
            cancelLoadingDialog();
        }
    }

    private void saveUser(AVFile file) {
        Map<String, Object> params = new HashMap<>();
        params.put("fileId", file.getObjectId());
        AVCloud.rpcFunctionInBackground(CloudFunction.UPD_USER_ICON, params, new SafeFunctionCallback<User>(this) {
            @Override
            protected void functionBack(User user, AVException e) {
                if (e != null) {
                    Utils.showToast(UpdUserIconActivity.this, "上传头像失败");
                } else {
                    Utils.showToast(UpdUserIconActivity.this, "上传头像成功");
                    Intent intent = new Intent();
                    intent.putExtra(PersonActivity.REFRESH_KEY, true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                cancelLoadingDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //拿到新图片的uri
            if (requestCode == CAMERA_KEY) {
                imageUri = tmpUri;
            } else if (requestCode == PHOTO_KEY) {
                imageUri = data.getData();
            }
            //更新图片显示
            if (imageUri != null)
                ImageLoader.displayImage(imageView, imageUri.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upd_user_icon_btn:
                openDialog();
                break;
        }
    }

    private void openDialog() {
        if (selectDialog == null) {
            selectDialog = new SelectDialog(this, Arrays.asList("打开相机", "打开相册"));
            selectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {//打开相机
                        //创建临时文件
                        File dir = FileCache.getCacheDir();
                        if (dir == null) {
                            Utils.showToast(UpdUserIconActivity.this, "打开相机失败,可能是SD卡无法访问");
                            return;
                        }
                        File currentTmpFile = new File(dir.getAbsolutePath() + File.separator + UUID.randomUUID() + ".png");
                        tmpUri = Uri.fromFile(currentTmpFile);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);//照片临时存储
                        startActivityForResult(intent, CAMERA_KEY);
                    } else if (position == 1) {//打开相册
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PHOTO_KEY);
                    }
                }
            });
        }
        selectDialog.show();
    }

}
