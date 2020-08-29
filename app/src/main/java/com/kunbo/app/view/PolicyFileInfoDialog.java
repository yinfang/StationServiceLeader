package com.kunbo.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kunbo.app.R;

/**
 * 政策制度文件预览
 */
public class PolicyFileInfoDialog {
    private Context context;

    public PolicyFileInfoDialog(@NonNull Context context) {
        this.context = context;
    }

    public void show(String title, String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        AlertDialog dialog = builder.create();
        dialog.show();  // show之后在dialog的window上添加布局

        WindowManager windowManager = dialog.getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = point.x;  // 设置宽度和高度
        lp.height = point.y;
        window.setAttributes(lp);

        window.setContentView(R.layout.webview_demo);
        LinearLayout backll = window.findViewById(R.id.header_back);
        backll.setOnClickListener(view -> dialog.dismiss());

        TextView tvtitle = window.findViewById(R.id.header_title);
        tvtitle.setText(title);

        final LinearLayout prolay = window.findViewById(R.id.pro_lay);
        final ProgressBar bar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        bar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Drawable drawable = context.getResources().getDrawable(R.drawable.blue_progress);
        bar.setProgressDrawable(drawable);
        prolay.addView(bar);

        MyWebView webview = window.findViewById(R.id.webView);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    prolay.setVisibility(View.GONE);
                } else {
                    if (View.GONE == prolay.getVisibility()) {
                        prolay.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
            }
        });
        webview.loadUrl(url);

        dialog.setCanceledOnTouchOutside(false);
    }
}
