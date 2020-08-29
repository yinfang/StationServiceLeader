package com.kunbo.app.activitys;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.utils.SPUtil;

import static com.kunbo.app.net.api.ApiSubscribe.getToken;

public class SplashActivity extends BaseCheckPermissionActivity implements OnSuccessAndFaultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                C.TOKEN_TYPE = "";
                C.TOKEN = "";
                String account = SPUtil.getString("account", "");
                String password = SPUtil.getString("password", "");
                if (!TextUtils.isEmpty(password)) {
                    isAutoLogin = true;
                    getUserToken(account, password);
                } else {
                    isAutoLogin = false;
                    goLogin();
                }
            }
        };
        countDownTimer.start();
    }

    public void getUserToken(String account, String password) {
        getToken(account, password, new OnSuccessAndFaultSub(this,
                "getToken", false, this));
    }

    @Override
    public void onFault(Result result, String method) {
        goLogin();
    }
}