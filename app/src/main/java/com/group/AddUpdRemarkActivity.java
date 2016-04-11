package com.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.group.base.BaseActivity;
import com.imageLoader.ImageLoader;
import com.imageLoader.listener.ImageProgressStateListener;
import com.leancloud.SafeSaveCallback;
import com.model.Merchant;
import com.model.Order;
import com.model.Remark;
import com.model.User;
import com.model.Voucher;
import com.util.Utils;
import com.widget.CustomToolBar;
import com.widget.RatingView;
import com.widget.remarkImagesLayout.OperateImagesLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_add_upd_remark)
public class AddUpdRemarkActivity extends BaseActivity {

    public static final String ORDER_KEY = "order";
    public static final String VOUCHER_KEY = "voucher";
    public static final String MERCHANT_KEY = "merchant";
    public static final String REMARK_KEY = "remark";
    public static final String IS_UPD_KEY = "isUpd";

    private static final int MIN_WORDS = 15;
    private static final int MAX_WORDS = 500;

    @InjectView(R.id.add_upd_remark_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.add_upd_remark_iv)
    private ImageView voucherIv;

    @InjectView(R.id.add_upd_remark_name_tv)
    private TextView merchantNameTv;

    @InjectView(R.id.add_upd_remark_voucher_name_tv)
    private TextView voucherNameTv;

    @InjectView(R.id.add_upd_remark_rating)
    private RatingBar ratingBar;

    @InjectView(R.id.add_upd_remark_point_tv)
    private TextView pointTv;

    @InjectView(R.id.add_upd_remark_ratingView)
    private RatingView ratingView;

    @InjectView(R.id.add_upd_remark_content_et)
    private EditText contentEt;

    @InjectView(R.id.add_upd_remark_limit_tv)
    private TextView limitTv;

    @InjectView(R.id.add_upd_remark_tip_tv)
    private TextView tipTv;

    @InjectView(R.id.add_upd_remark_image_layout)
    private OperateImagesLayout imagesLayout;

    private Order order;
    private Voucher voucher;
    private Merchant merchant;
    private Remark remark;
    private boolean isUpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog.setCancelable(false);
        receiveIntent();
        setInfo();
        setEditText();
        setRemarkInfo();
        toolBar.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void setRemarkInfo() {
        if (remark != null) {
            contentEt.setText(remark.getContent());
            ratingView.setPoint(remark.getPoint());
            if (remark.getImages() != null && remark.getImages().size() > 0) {//init images
                for (String url : remark.getImages()) {
                    imagesLayout.addImage(url);
                }
            }
        }
    }

    private void submit() {
        if ((isUpd && remark == null) || order == null || voucher == null) {
            Utils.showToast(this, "订单信息有误");
            return;
        }
        if (AVUser.getCurrentUser() == null) {
            Utils.showToast(this, "请先登录");
            return;
        }
        int point = ratingView.getPoint();
        if (point <= 0) {
            Utils.showToast(this, "请打分");
            return;
        }
        String content = contentEt.getText().toString();
        if (content.length() < MIN_WORDS) {
            Utils.showToast(this, "请输入" + MIN_WORDS + "字以上评论");
            return;
        }
        //提交
        showLoadingDialog("上传评论...");
        if (!isUpd) {//更新评论不新建
            remark = new Remark();
        }
        remark.setOrder(order);
        remark.setVoucher(voucher);
        remark.setUser(AVUser.getCurrentUser(User.class));
        remark.setPoint(point);
        remark.setContent(content);
        getImagesAndSave();
    }

    private void getImagesAndSave() {
        final List<AVFile> newFiles = new ArrayList<>();
        //加载原有AVFile,统计本地上传图片个数
        final List<String> needUpload = new ArrayList<>();
        for (String url : imagesLayout.getUrls()) {
            AVFile file = getOriginFile(url);
            if (file != null) {
                newFiles.add(file);
            } else {//稍后本地上传
                needUpload.add(url);
            }
        }
        if (needUpload.size() <= 0) {//没有需要上传的本地图片(已经构建好files),直接上传即可
            remark.resetImages(newFiles);
            save();
            return;
        }
        //本地上传AVFile
        final List<Integer> flags = new ArrayList<>();
        for (String str : needUpload) {
            ImageLoader.loadImage(str, new ImageProgressStateListener() {

                @Override
                public void onLoadingComplete(ImageView imageView, String imgUrl, Bitmap loadedImage) {
                    if (loadedImage != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        loadedImage.compress(Bitmap.CompressFormat.PNG, 80, baos);
                        newFiles.add(new AVFile("remark_img.png", baos.toByteArray()));
                    }
                    flags.add(0);
                }

                @Override
                public void onLoadingFinally(ImageView imageView, String imgUrl) {
                    if (flags.size() == needUpload.size()) {//全部加载完成
                        remark.resetImages(newFiles);
                        save();
                    }
                }
            });
        }
    }

    private AVFile getOriginFile(String url) {
        List<AVFile> files = remark.getImagesFiles();
        if (files == null || files.size() <= 0)
            return null;
        for (AVFile file : files) {
            if (!TextUtils.isEmpty(file.getObjectId()) && file.getUrl().equals(url)) {//原有网络数据
                return file;
            }
        }
        return null;
    }

    private void save() {
        remark.saveInBackground(new SafeSaveCallback(this) {
            @Override
            public void save(AVException e) {
                cancelLoadingDialog();
                if (e != null) {//失败
                    Utils.showToast(AddUpdRemarkActivity.this, "上传评论失败");
                } else {//成功
                    Utils.showToast(AddUpdRemarkActivity.this, "上传评论成功");
                    setResult(RESULT_OK);//通知想要接到的界面
                    finish();
                }
            }
        });
    }

    private void setEditText() {
        limitTv.setText("0/" + MAX_WORDS);
        tipTv.setText("输入" + MIN_WORDS + "个字即可发表");
        contentEt.setHint("写够" + MIN_WORDS + "个字,才是好同志哦~");
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                limitTv.setText(contentEt.getText().toString().length() + "/" + MAX_WORDS);
            }
        });
    }

    private void setInfo() {
        if (voucher != null && merchant != null) {
            if (voucher.getImages() != null && voucher.getImages().size() > 0) {
                ImageLoader.displayImage(voucherIv, voucher.getImages().get(0));
            } else if (merchant.getImages() != null && merchant.getImages().size() > 0) {
                ImageLoader.displayImage(voucherIv, merchant.getImages().get(0));
            }
            merchantNameTv.setText(merchant.getName());
            voucherNameTv.setText(voucher.getName());
            ratingBar.setRating((float) voucher.getPoint());
            pointTv.setText(voucher.getPoint() + "分");
        }
    }

    private void receiveIntent() {
        order = getIntent().getParcelableExtra(ORDER_KEY);
        voucher = getIntent().getParcelableExtra(VOUCHER_KEY);
        merchant = getIntent().getParcelableExtra(MERCHANT_KEY);
        remark = getIntent().getParcelableExtra(REMARK_KEY);
        isUpd = getIntent().getBooleanExtra(IS_UPD_KEY, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OperateImagesLayout.CAMERA_KEY) {
                File file = imagesLayout.getCurrentTmpFile();
                if (file != null) {
                    Uri uri = Uri.fromFile(file);
                    imagesLayout.addImage(uri.toString());
                }
            } else if (requestCode == OperateImagesLayout.PHOTO_KEY) {
                Uri uri = data.getData();
                imagesLayout.addImage(uri.toString());
            }
        }
    }
}
