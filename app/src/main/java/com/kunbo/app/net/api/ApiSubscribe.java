package com.kunbo.app.net.api;

import com.kunbo.app.net.OkHttpUtil;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.http.Query;

/**
 * 具体接口调用
 * 建议：把功能模块来分别存放不同的请求方法，比如登录注册类LoginSubscribe、电影类MovieSubscribe
 */
public class ApiSubscribe {
    /**
     * 获取admin权限token
     */
    public static void getToken(String username, String password, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getToken(username, password);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfo(DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getUserInfo();
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取用户所能看到的站
     */
    public static void getStationList(String deptId, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getStationList(deptId);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取轮播图
     */
    public static void getBanner(DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getBanner();
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取首页未提交记录数字
     */
    public static void getUncommitNum( DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getUncommitNum( );
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取站长值班记录列表
     */
    public static void getAgentOndutyList(String dateTime, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getAgentOndutyList(dateTime);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取班长值班记录列表
     */
    public static void getMonitorOndutyList(String dateTime, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getMonitorOndutyList(dateTime);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取票证值班记录列表
     */
    public static void getTicketOndutyList(String dateTime, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getTicketOndutyList(dateTime);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 获取车道特情列表
     */
    public static void getSpecialOndutyList(String pageIndex,String pageSize,String deptId, String startTime, String endTime, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getSpecialOndutyList(pageIndex,pageSize,deptId, startTime, endTime);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 综合统计
     */
    public static void getStatistics(String stationId, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getStatistics(stationId);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 站值班表（每月）
     */
    public static void getOndutyList(String stationId, String month, DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().getOndutyList(stationId, month);
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

    /**
     * 检测新版本
     */
    public static void checkNewVersion(DisposableObserver<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = OkHttpUtil.getInstance().getApiService().checkNewVersion();
        OkHttpUtil.getInstance().toSubscribe(observable, subscriber);
    }

}

