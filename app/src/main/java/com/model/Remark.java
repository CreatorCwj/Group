package com.model;

import android.text.TextUtils;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.leancloud.SafeSaveCallback;
import com.model.base.BaseModel;
import com.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
@SuppressWarnings("unchecked")
@AVClassName("Remark")
public class Remark extends BaseModel {

    public static final String CLASS_NAME = "Remark";

    public static final String CONTENT = "content";
    public static final String POINT = "point";
    public static final String IMAGES = "images";
    public static final String VOUCHER = "voucher";
    public static final String USER = "user";
    public static final String ORDER = "order";

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public void setPoint(int point) {
        put(POINT, point);
    }

    public int getPoint() {
        return getInt(POINT);
    }

    public void resetImages(List<AVFile> images) {
        put(IMAGES, images);
    }

    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        List<AVFile> list = getList(IMAGES);
        if (list != null && list.size() > 0) {
            for (AVFile file : list) {
                images.add(file.getUrl());
            }
        }
        return images;
    }

    public List<AVFile> getImagesFiles() {
        return getList(IMAGES);
    }

    /**
     * 先存图片后保存数据(一定要用这个方法)
     *
     * @param callback:一定是SafeSaveCallback类型的
     */
    public void saveInBackground(final SafeSaveCallback callback) {
        List<AVFile> files = getList(IMAGES);
        if (files == null || files.size() <= 0 || getNeedSaveFiles(files).size() <= 0) {//无图片需要上传,直接保存
            super.saveInBackground(callback);
        } else {//先保存图片(没有上传过的)
            try {
                saveFileBeforeSave(getNeedSaveFiles(files), false, new SafeSaveCallback(callback.getContext()) {
                    @Override
                    public void save(AVException e) {
                        if (e != null) {//失败
                            Utils.showToast(this.getContext(), "上传图片失败");
                            callback.save(e);//报错
                        } else {//成功,后续保存
                            Remark.this.saveInBackground((SaveCallback) callback);
                        }
                    }
                });
            } catch (AVException e) {
                e.printStackTrace();
                //异常出错
                Utils.showToast(callback.getContext(), "上传图片失败");
                callback.save(e);//报错
            }
        }
    }

    private List<AVFile> getNeedSaveFiles(List<AVFile> allFiles) {
        List<AVFile> files = new ArrayList<>();
        for (AVFile file : allFiles) {
            if (TextUtils.isEmpty(file.getObjectId())) {//没有上传过
                files.add(file);
            }
        }
        return files;
    }

    public void setVoucher(String voucherId) {
        try {
            put(VOUCHER, AVObject.createWithoutData(Voucher.class, voucherId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setVoucher(Voucher voucher) {
        put(VOUCHER, voucher);
    }

    public Voucher getVoucher() {
        try {
            return getAVObject(VOUCHER, Voucher.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOrder(String orderId) {
        try {
            put(ORDER, AVObject.createWithoutData(Order.class, orderId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setOrder(Order order) {
        put(ORDER, order);
    }

    public Order getOrder() {
        try {
            return getAVObject(ORDER, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUser(String userId) {
        try {
            put(USER, AVUser.createWithoutData(User.class, userId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        put(USER, user);
    }

    public User getUser() {
        return getAVUser(USER, User.class);
    }
}
