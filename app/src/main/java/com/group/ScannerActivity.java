package com.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.util.Utils;
import com.widget.dialog.MessageDialog;

import java.util.List;

/**
 * Created by cwj on 16/2/14.
 * 自定义扫一扫处理
 */
public class ScannerActivity extends CaptureActivity {

    private MessageDialog dialog;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        dialog = new MessageDialog(this);
    }

    @Override
    protected void handleDecodeResult(Result rawResult, List<String> schemas) {
        //解析识别
        String result = rawResult.getText();
        if (!TextUtils.isEmpty(result)) {
            Uri uri = Uri.parse(rawResult.getText());
            if (uri.getScheme() != null && schemas.contains(uri.getScheme().toLowerCase())) {
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                intent.addCategory("android.intent.category.BROWSABLE");
                this.startActivity(intent);
                this.finish();
                return;
            }
        }
        //识别不了
        dialog.setTitle("提示");
        dialog.setMessage("识别不了此二维码");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ScannerActivity.this.restartPreviewAfterDelay(10L);
            }
        });
        dialog.show();
    }

    @Override
    protected void handleOpenCameraException() {
        super.handleOpenCameraException();
        dialog.setTitle("提示");
        dialog.setMessage("无法打开照相机,请确认是否开启权限");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ScannerActivity.this.finish();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.showToast(this, "取消二维码扫描");
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
