package com.kunbo.app.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.kunbo.app.net.OkHttpUtil;
import com.kunbo.app.net.api.ApiService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class AppUtil {
    private static ProgressDialog pdialog;
    private static ProgressInfo mLastDownloadingInfo;

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取应用程序版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 用服务器版本号和本地版本号对比
     *
     * @param versionCode
     * @return
     */
    public static boolean shouldUpdate(Context context, int versionCode) {
        int localVersionCode = getVersionCode(context);
        if (localVersionCode == -1) {
            return false;
        }
        if (versionCode > localVersionCode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取设备唯一码
     */
    public static String getUdid(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    /**
     * 获取设备型号 ,版本即最终用户可见的名称  如 Redmi note2
     *
     * @return
     */
    public static String getDevmodel() {
        return Build.MODEL + "";
    }

    /**
     * 判断当前设备是手机还是平板
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 当前是否为横屏
     *
     * @param context
     * @return
     */
    public static boolean isScreenHorizontal(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) { //横屏
            return true;
        }
        return false;
    }

    private static final X500Principal DEBUG_DN = new X500Principal(
            "CN=Android Debug,O=Android,C=US");

    /**
     * 判断当前运行的程序是否是debug包
     *
     * @param ctx
     * @return
     */
    public static boolean isDebug(Context ctx) {
        boolean debuggable = false;

        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (int i = 0; i < signatures.length; i++) {
                ByteArrayInputStream stream = new ByteArrayInputStream(
                        signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf
                        .generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // debuggable variable will remain false
        } catch (CertificateException e) {
            // debuggable variable will remain false
        }
        return true;
    }


//---------------------------------------------------------------------网络相关方法-------------------------------------------------------------------------

    /**
     * 获取IP
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected() || gprs.isConnected();

    }

    /**
     * 当前是否使用3G
     *
     * @param context
     * @return
     */
    public static boolean is3G(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return gprs.isConnected();
    }

    /**
     * 判断当前网络是否是wifi网络
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    /**
     * 判断当前网络是否是2G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null
                && (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || networkInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
            return true;
        }
        return false;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 获取下载路径
     *
     * @return
     */
    public static String getDownloadPath() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "/Download/");
        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    /**
     * 下载新版本
     */
    public static void downLoadApk(final Context context, String path) {
        pdialog = new ProgressDialog(context);
        // dialog.setTitle("Indeterminate");
        pdialog.setMessage("正在下载下载新版本，请稍后......");
        pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdialog.setIndeterminate(false);
        pdialog.setMax(100);
        pdialog.incrementProgressBy(1);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.setCancelable(false);
        pdialog.show();

        Retrofit ref = OkHttpUtil.getInstance().getRetrofit();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(context, "apk下载地址无效！", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressManager.getInstance().addResponseListener(path, getDownloadListener());
        ref.create(ApiService.class)
                .downloadFileOrApk(path)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, File>() {
                         @Override
                         public File apply(ResponseBody responseBody) {
                             return saveApk(context, responseBody.byteStream());
                         }
                     }
                )
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }
                        AppUtil.installApk(context, file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "apk下载失败," + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }
                    }
                });
    }

    private static ProgressListener getDownloadListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                // 如果你不屏蔽用户重复点击上传或下载按钮,就可能存在同一个 Url 地址,上一次的上传或下载操作都还没结束,
                // 又开始了新的上传或下载操作,那现在就需要用到 id(请求开始时的时间) 来区分正在执行的进度信息
                // 这里我就取最新的下载进度用来展示,顺便展示下 id 的用法
                if (mLastDownloadingInfo == null) {
                    mLastDownloadingInfo = progressInfo;
                }
                //因为是以请求开始时的时间作为 Id ,所以值越大,说明该请求越新
                if (progressInfo.getId() < mLastDownloadingInfo.getId()) {
                    return;
                } else if (progressInfo.getId() > mLastDownloadingInfo.getId()) {
                    mLastDownloadingInfo = progressInfo;
                }
                int progress = mLastDownloadingInfo.getPercent();
                pdialog.setProgress(progress);
            }

            @Override
            public void onError(long id, Exception e) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (pdialog != null) {
                            pdialog.dismiss();
                        }
                    }
                });
            }
        };
    }

    /**
     * 下载保存apk到file
     */
    private static File saveApk(Context contexct, InputStream input) {
        try {
            File f = File.createTempFile("temp_name", "apk", contexct.getCacheDir());
            OutputStream output = new FileOutputStream(f);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return f;
        } catch (Exception e) {
            pdialog.dismiss();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 安装apk
     */
    private static void installApk(Context context, File file) {
        String command = "chmod 777 " + file.getAbsolutePath();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            uri = FileProvider.getUriForFile(context, AppUtil.getPackageName(context) + ".fileprovider", file);  // 参数二为apk的包名加上.fileprovider
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
        } else {
            uri = Uri.fromFile(file);
        }
        //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 运行 assets 目录下的apk文件。
     *
     * @param context
     * @param fileName 要运行的文件名
     */
    public static void runAssetsApk(Context context, String fileName) {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + fileName;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            installApk(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件写入本地
     *
     * @param fileName 目标文件名
     * @return 写入完成的文件
     */
    public static File saveFile(Context context, InputStream is, String fileName) {
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            File dir = new File(getDownloadPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return file;
        } catch (IOException e) {
            Toast.makeText(context, "文件写入失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
