package com.group.wxapi;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.util.Utils;
import com.util.WxUtils;

/**
 * @author cwj这个类必须要，否则回调时没有地方去就报错
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);
        IWXAPI api = WxUtils.registWxApi(this);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法

    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法

    @Override
    public void onResp(BaseResp resp) {
        // TODO Auto-generated method stub
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Utils.showToast(WXEntryActivity.this, "分享成功", Toast.LENGTH_LONG);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Utils.showToast(WXEntryActivity.this, "分享被取消", Toast.LENGTH_LONG);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Utils.showToast(WXEntryActivity.this, "分享失败", Toast.LENGTH_LONG);
                break;
            default:
                Utils.showToast(WXEntryActivity.this, "分享失败", Toast.LENGTH_LONG);
                break;
        }
        finish();
    }
}