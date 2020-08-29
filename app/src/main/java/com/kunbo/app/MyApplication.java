package com.kunbo.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.kunbo.app.service.MyService;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import java.util.Stack;


public class MyApplication extends MultiDexApplication {
    public Stack<Activity> activities = new Stack<>();
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    startForegroundService(serviceIntent);
        //} else {
        //注册应用到bugly
        CrashReport.initCrashReport(getApplicationContext(), "12fb310377", false);
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d("Application", "X5内核加载是否成功" + b);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getContext() {
        return context;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void finishAllActivites() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    public void exit(boolean exitApp) {
        finishAllActivites();
        if (exitApp) {
            //这种方式退出应用，会结束本应用程序的一切活动,因为本方法会根据应用程序的包名杀死所有进程包括Activity,Service,Notifications等。
       /*     ActivityManager am = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(getApplicationContext().getPackageName());*/
            System.exit(0);//等同于Runtime.getRuntime().exit(n)
        }
    }
}
