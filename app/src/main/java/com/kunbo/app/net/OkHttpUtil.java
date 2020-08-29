package com.kunbo.app.net;

import android.util.Log;


import com.kunbo.app.BuildConfig;
import com.kunbo.app.C;
import com.kunbo.app.net.api.ApiService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.progressmanager.ProgressManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * okhttp3+retrofit2 网络请求util
 * json数据交互
 */
public class OkHttpUtil {

    static final int CONNECT_TIMEOUT = 10;
    static final int READ_TIMEOUT = 10;
    static final int WRITE_TIMEOUT = 30;
    private static final int TIMEOUT_CONNECT = 5; //5秒
    private OkHttpClient.Builder builder;
    private Retrofit retrofit;
    private ApiService apiService;
    //请求失败重连次数
    private int RETRY_COUNT = 5;

    private OkHttpUtil() {
        builder = ProgressManager.getInstance().with(new OkHttpClient.Builder());//监听上传下载进度
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("————Retrofit————: ", "————网络访问————OkHttp: " + message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        //是否允许请求失败重连
        builder.retryOnConnectionFailure(true);
        //添加缓存
        builder.cache(HttpInterceptor.cache);
        //添加缓存拦截
        builder.addInterceptor(HttpInterceptor.cacheControlInterceptor);
        //添加cookie缓存
        builder.cookieJar(HttpInterceptor.getCookieJar());
        //添加请求头部公共参数
        builder.addInterceptor(HttpInterceptor.headerParamsInterceptor);
        //添加HTTPS 证书
        builder.sslSocketFactory(HttpsConfig.createSSLSocketFactory(), new HttpsConfig.AppTrustManager());
        //Hostname 的验证
        builder.hostnameVerifier(new HttpsConfig.TrustAllHostnameVerifier());

        retrofit = new Retrofit.Builder()
                .baseUrl(C.BASE_URL_INTERNET)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())//json转换成JavaBean
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 支持RxJava
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    //在访问OkHttpUtil时创建单例
    private static class SingletonHolder {
        private static final OkHttpUtil INSTANCE = new OkHttpUtil();

    }

    //获取单例
    public static OkHttpUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取retrofit
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * 获取retrofit
     */
    public OkHttpClient getClient() {
        return builder.build();
    }

    public void changeBaseUrl(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取httpService
     */
    public ApiService getApiService() {
        return apiService;
    }

    /**
     * 设置订阅 和 所在的线程环境
     */
    public <T> void toSubscribe(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(RETRY_COUNT)//请求失败重连次数
                .subscribe(s);
    }

    //键值对以RequestBody对象形式上传，适用于确定数量的图片和文字一起上传
    public static RequestBody toRequestBodyOfText(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    //生成图片文件，适用于数量确定或不定的多图片上传
    public static RequestBody toRequestBodyOfImage(File pFile) {
//        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), pFile);  //以图片形式上传文件
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), pFile);//以 form形式上传图片
        return fileBody;
    }

    //以二进制形式上传文件，适用于数量确定或不定的多图片上传
    public static RequestBody toRequestBodyOfImageFile(File pFile) {
        RequestBody fileBody = RequestBody.create(MediaType.parse(" application/octet-stream"), pFile);  //以二进制形式上传文件
        return fileBody;
    }
}