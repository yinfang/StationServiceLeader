package com.kunbo.app.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kunbo.app.view.lemonhello.LemonHelloAction;
import com.kunbo.app.view.lemonhello.LemonHelloInfo;
import com.kunbo.app.view.lemonhello.LemonHelloView;
import com.kunbo.app.view.lemonhello.interfaces.LemonHelloActionDelegate;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查相关权限基类，适用于android 6.0 运行时权限
 * 使用方法：需要运行时权限的activity继承此类
 * 1.在Androidmanifest.xml文件声明相关权限
 * 2.通过ContextCompat.checkSelfPermission方法检查某项权限被授予情况
 * 3.申请授权
 * 4.权限回调处理
 */
public abstract class BaseCheckPermissionActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CODE_PERMISSON = 2020; //权限请求码

    public static boolean isNeedCheckPermission = true; //判断是否需要检测，防止无限弹框申请权限
    private LemonHelloInfo lemonHello = null;
    private boolean dinied = false;
    public int diniedCount = 0;

    //     目前只检查和申请指定权限，需要检查全部权限时放开，子类不需要任何处理
    @Override
    protected void onResume() {
        super.onResume();
        diniedCount = 0;
        checkAllNeedPermissions();
    }

    /**
     * 检查所有权限，无权限则开始申请相关权限
     */
    protected void checkAllNeedPermissions() {
        List<String> permissonList = getDeniedPermissions(getNeedPermissions());
        if (!isGrantedAllPermission()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0以下版本回调后单独判断
                for (String permission : permissonList) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager
                            .PERMISSION_GRANTED) {
                        diniedCount++;
                    }
                }
                if (diniedCount > 0) {
                    showTipsDialog();
                } else {
                    if (lemonHello != null) {
                        lemonHello.hide();
                    }
                }
            } else {//6.0以上动态申请权限
                ActivityCompat.requestPermissions(this, permissonList.toArray(
                        new String[permissonList.size()]), REQUEST_CODE_PERMISSON);
            }
        }
    }

    /**
     * 所有当前应用需要的权限均在此动态申请
     */
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissonList.add(permission);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 所有权限是否都已授权
     *
     * @return
     */
    protected boolean isGrantedAllPermission() {
        List<String> needRequestPermissonList = getDeniedPermissions(getNeedPermissions());
        return needRequestPermissonList.size() == 0;
    }

    /**
     * 权限授权结果回调
     *
     * @param requestCode
     * @param permissions
     * @param paramArrayOfInt
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] paramArrayOfInt) {
        if (requestCode == REQUEST_CODE_PERMISSON) {
            verifyPermissions(paramArrayOfInt);
        }
        super.onRequestPermissionsResult(requestCode, permissions, paramArrayOfInt);
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private void verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                diniedCount++;
            }
        }
        if (diniedCount > 0) {
            showTipsDialog();
        } else {
            if (lemonHello != null) {
                lemonHello.hide();
            }
        }
    }

    /**
     * 显示提示对话框
     */
    protected void showTipsDialog() {
        if (lemonHello == null) {
            lemonHello = new LemonHelloInfo().setTitle("温馨提示").setContent("当前应用缺少必要权限，某些功能暂时无法使用。请单击【确定】按钮前往设置中心进行权限授权。")
                    .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
                        @Override
                        public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                            helloView.hide();
                        }
                    }))
                    .addAction(new LemonHelloAction("确定", new LemonHelloActionDelegate() {
                        @Override
                        public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                            helloView.hide();
                            startAppSettings();
                        }
                    }));
            lemonHello.show(this);
        }
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}