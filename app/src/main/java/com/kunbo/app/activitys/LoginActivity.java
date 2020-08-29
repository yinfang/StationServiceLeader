package com.kunbo.app.activitys;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.utils.SPUtil;
import com.kunbo.app.utils.Utils;
import com.kunbo.app.view.statusBarUtil.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements OnSuccessAndFaultListener {
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPwd;
    @BindView(R.id.radio_see_pwd)
    CheckBox radioSeePwd;
    private long clickTime = 0; // 第一次点击返回键的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setStatusBarImageIn(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String account = SPUtil.getString("account", "");
        etAccount.setText(account);
        etAccount.setSelection(account.length());//将光标移至文字末尾
        etAccount.requestFocus();

        radioSeePwd.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });
        //checkNewVersion(new OnSuccessAndFaultSub(this, "checkNewVersion", "正在检测新版本...", this));
    }

    @OnClick(R.id.btn_login)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (Utils.checkNullString(getEText(R.id.et_account)) || Utils.checkNullString(getEText(R.id.et_password))) {
                    Toast.makeText(this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                C.TOKEN_TYPE = "";
                C.TOKEN = "";
                getUserToken(getEText(R.id.et_account), getEText(R.id.et_password));
                break;
        }
    }

    @Override
    public void onFault(Result result, String method) {
        Toast.makeText(this, result.msg, Toast.LENGTH_SHORT).show();
    }
}

