package com.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.group.R;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.util.WxUtils;
import com.widget.dialog.base.BaseAlertDialog;
import com.widget.radio.RadioView;

/**
 * Created by cwj on 16/4/24.
 * 分享对话框
 */
public class ShareDialog extends BaseAlertDialog implements View.OnClickListener {

    private String url;
    private String title;
    private String text;

    private RadioView sessionRv;
    private RadioView timelineRv;

    public ShareDialog(Context context, String url, String title, String text) {
        super(context);
        this.url = url;
        this.title = title;
        this.text = text;
        setTitle("分享到");
        hideButton(true);
    }

    @Override
    protected View onCreateContentView() {
        return LayoutInflater.from(context).inflate(R.layout.share_dialog, contentLayout, false);
    }

    @Override
    protected void onContentViewCreated(View view) {
        sessionRv = (RadioView) view.findViewById(R.id.send_to_session_rv);
        timelineRv = (RadioView) view.findViewById(R.id.send_to_timeline_rv);
        sessionRv.setOnClickListener(this);
        timelineRv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_to_session_rv://朋友
                shareToWx(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.send_to_timeline_rv://朋友圈
                shareToWx(SendMessageToWX.Req.WXSceneTimeline);
                break;
        }
        cancel();
    }

    private void shareToWx(int type) {
        WxUtils.registWxApi(context);
        WxUtils.sendWebPageWx(context, url, title, text, type);
    }
}
