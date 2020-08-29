package com.kunbo.app.net;

import androidx.annotation.NonNull;

import com.kunbo.app.MyApplication;
import com.kunbo.app.C;
import com.kunbo.app.utils.AppUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class HttpInterceptor {
    // cookie 文件存储
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    public static final File chachefile = new File(MyApplication.getContext().getExternalCacheDir(), "HttpCache");
    //缓存文件
    public static final Cache cache = new Cache(chachefile, 1024 * 1024 * 50);

    /**
     * 管理Cookie
     */
    @NonNull
    public static CookieJar getCookieJar() {
        return new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }

    /**
     * 设置请求头公共参数
     * 打印请求和返回log
     */
    public static final Interceptor headerParamsInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", C.TOKEN_TYPE + " " + C.TOKEN)//添加请求头信息，服务器进行token有效性验证
                    .method(originalRequest.method(), originalRequest.body());
            Request request = requestBuilder.build();

           /* StringBuilder logSB = new StringBuilder();
            logSB.append("请求头" + request.headers() + "\n|");
            logSB.append(request.method().equalsIgnoreCase("POST") ? "post参数{" + request.body() + "}\n|" : "");
            logSB.append("请求url" + request.url() + "\n|");
            logSB.append("————网络访问————End:耗时" + duration + "毫秒----------");
            Log.d("————网络访问————start: ", "\n" + logSB.toString());*/

            return chain.proceed(request);
        }
    };

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 设置缓存拦截器
     */
    public static final Interceptor cacheControlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!AppUtil.isNetworkConnected(MyApplication.getContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            if (AppUtil.isNetworkConnected(MyApplication.getContext())) {
                int maxAge = 0;
                // 有网络时 设置缓存立即超时
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("HttpCache") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                // 无网络时 设置超时为1周
                int maxStale = 60 * 60 * 24 * 7;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("HttpCache")
                        .build();
            }
        }
    };
}
