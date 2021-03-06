package com.kunbo.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class MyWebView extends WebView {
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    public MyWebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        this.setWebViewClient(client);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.setClickable(true);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 不支持缩放(如果要支持缩放，html页面本身也要支持缩放：不能加user-scalable=no)
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        // 自适应屏幕
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计

        webSetting.setDisplayZoomControls(false);
        // 不使用缓存
        webSetting.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        webSetting.setAllowFileAccess(true);//允许访问文件
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.supportMultipleWindows();
        webSetting.setAllowContentAccess(true);
        // 自适应屏幕
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setSaveFormData(true);
        webSetting.setLoadsImagesAutomatically(true);

        this.setVerticalScrollBarEnabled(true);
        this.setHorizontalScrollBarEnabled(true);
        this.setScrollBarStyle(android.webkit.WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    /*@Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        canvas.save();
        Paint paint = new Paint();
        paint.setColor(0x7fff0000);
        paint.setTextSize(24.f);
        paint.setAntiAlias(true);
        if (getX5WebViewExtension() != null) {
            canvas.drawText(this.getContext().getPackageName() + "-pid:"
                    + android.os.Process.myPid(), 10, 50, paint);
            canvas.drawText(
                    "X5  Core:" + QbSdk.getTbsVersion(this.getContext()), 10,
                    100, paint);
        } else {
            canvas.drawText(this.getContext().getPackageName() + "-pid:"
                    + android.os.Process.myPid(), 10, 50, paint);
            canvas.drawText("Sys Core", 10, 100, paint);
        }
        canvas.drawText(Build.MANUFACTURER, 10, 150, paint);
        canvas.drawText(Build.MODEL, 10, 200, paint);
        canvas.restore();
        return ret;
    }*/

    public MyWebView(Context arg0) {
        super(arg0);
        setBackgroundColor(Color.parseColor("#00000000")); // 设置背景色避免闪屏
    }

}
