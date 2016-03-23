package com.group;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.group.base.BaseActivity;
import com.widget.AdvancedWebView;
import com.widget.CustomToolBar;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_h5)
public class H5Activity extends BaseActivity {

    public static final String TITLE_KEY = "title";
    public static final String URL_KEY = "url";

    @InjectView(R.id.h5_toolbar)
    private CustomToolBar toolBar;

    @InjectView(R.id.h5_progressbar)
    private ProgressBar progressBar;

    @InjectView(R.id.webView)
    private AdvancedWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initWebView();
        loadUrl();
    }

    private void loadUrl() {
        String url = getIntent().getStringExtra(URL_KEY);
        if (!TextUtils.isEmpty(url))
            webView.loadUrl(url);
    }

    private void initWebView() {
        webView.clearCache(true);
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setListener(this, new AdvancedWebView.Listener() {
            @Override
            public void onPageStarted(String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }

            @Override
            public void onExternalPageRequest(String url) {

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.cancel();
                Toast.makeText(H5Activity.this, message, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initToolbar() {
        String title = getIntent().getStringExtra(TITLE_KEY);
        toolBar.setTitleText(title);
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}
