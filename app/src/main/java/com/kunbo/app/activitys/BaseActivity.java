package com.kunbo.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.kunbo.app.C;
import com.kunbo.app.MyApplication;
import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.utils.SPUtil;
import com.kunbo.app.view.ListDialog;
import com.kunbo.app.view.lemonbubble.LemonBubble;
import com.kunbo.app.view.lemonbubble.LemonBubbleView;

import butterknife.OnClick;

import static com.kunbo.app.net.api.ApiSubscribe.getToken;

public class BaseActivity extends ComponentActivity implements OnSuccessAndFaultListener {
    private String account, password;
    public boolean isAutoLogin = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取状态栏 标题栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @OnClick(R.id.header_back)
    public void back(View view) {
        if (view.getId() == R.id.header_back) {
            finish();
            overridePendingTransition(R.anim.backward_enter, R.anim.backward_exit);
        }
    }

    /**
     * 通用弹出窗口点OK按钮后的回调方法
     *
     * @param type 标识，如果有多个地方触发，用于区分触发的是哪次。
     * @param tag  弹出窗口附加的自定义对象
     */
    public void processDialogOK(int type, Object tag) {
    }

    /**
     * 通用弹出窗口点取消按钮后的回调方法
     *
     * @param type 标识，如果有多个地方触发，用于区分触发的是哪次。
     * @param tag  弹出窗口附加的自定义对象
     */
    public void processDialogCancel(int type, Object tag) {
    }


    public void setEText(int resId, CharSequence value) {
        if (value != null) {
            TextView tv = this.findViewById(resId);
            if (tv != null) {
                tv.setText(value.toString().trim());
            }
        }
    }

    public String getEText(int resId) {
        TextView tv = findViewById(resId);
        if (tv != null) {
            return tv.getText().toString();
        }
        return "";
    }

    public String getEText(View view, int resId) {
        TextView tv = view.findViewById(resId);
        if (tv != null) {
            return tv.getText().toString();
        }
        return "";
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view       触发控件
     * @param resTitleId 显示标题栏文字，0 不显示标题栏
     * @param captions   列表文本
     */
    public void showListDialog(View view, int resTitleId, String[] captions) {
        ListDialog dialog = new ListDialog(this);
        dialog.setOnSelectedListener(new ListDialog.OnSelectedListener() {
            public void onSelected(View view, int index) {
                listSelected(view, index);
            }
        });
        dialog.show(view, resTitleId, captions);
    }

    /**
     * 列表选择的回调方法，子类覆盖此方法。
     *
     * @param view
     * @param index
     */
    public void listSelected(View view, int index) {
    }

    public static int getId(Context context, String resCode, String type) {
        int resId = context.getResources().getIdentifier(resCode, type,
                context.getPackageName());
        return resId;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!(this instanceof MainActivity)) {
                finish();
                overridePendingTransition(R.anim.backward_enter, R.anim.backward_exit);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出系统关闭推送消息 这个方法在应用没有用极光推送的情况下都传false.
     */
    protected void exit(boolean exitApp) {
        ((MyApplication) getApplication()).exit(exitApp);
    }

    public void getUserToken(String account, String password) {
        this.account = account;
        this.password = password;
        getToken(account, password, new OnSuccessAndFaultSub(this,
                "getToken", "登录中....", true, true, this));
    }

    @Override
    public void onSuccess(Result result, String method) {
        switch (method) {
            case "getToken":
                SPUtil.saveSetting("account", account);
                SPUtil.saveSetting("password", password);
                MyRow ro = (MyRow) result.obj;
                C.TOKEN_TYPE = ro.getString("token_type") + " ";
                C.TOKEN = ro.getString("access_token");
                goMain();
                break;
        }
    }

    @Override
    public void onFault(Result result, String method) {
        Toast.makeText(this, result.msg, Toast.LENGTH_SHORT).show();
        if (result.msg.contains("Unauthorized")) {//登陆过期
            goLogin();
        }
    }

    public void goMain() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
        finish();
    }

    public void goLogin() {
        Intent in = new Intent(this, LoginActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
        finish();
    }
}


