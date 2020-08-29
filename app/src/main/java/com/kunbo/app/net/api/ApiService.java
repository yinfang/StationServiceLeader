package com.kunbo.app.net.api;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @Headers({"Content-type:application/json;charset=UTF-8"}) 1. JSON  格式 请求体
 * RequestBody body = RequestBody.create(JSON, "json格式的字符串");
 * <p>
 * 2.  单文件上传
 * <p>
 * RequestBody requestBody = new MultipartBody.Builder()
 * .setType(MultipartBody.FORM)
 * .addFormDataPart("file", file.getName(), RequestBody.create(PNG, file))
 * .build();
 * 多文件上传
 * MultipartBody.Builder imgRequest = new MultipartBody.Builder().setType(MultipartBody.FORM);
 * for（File file：files）
 * RequestBody requestBody = OkHttpUtil.toRequestBodyOfImage(file);
 * imgRequest.addFormDataPart("mFile", file.getName(), requestBody);
 * <p>
 * 3. 表单      把接口参数通过Map的形式来提交。使用@Body，@POST注解
 * FormBody body = new FormBody.Builder()
 * .add("limit", String.valueOf(LIMIT))
 * .add("page", String.valueOf(pageValue))
 * .build();
 * 4.Json提交  Retrofit会帮我们把对象转换成Json，然后传递给后台服务器。使用@Body ，@POST注解
 * <p>
 * 定义接口
 */
public interface ApiService {
    /**
     * 获取token
     */
    @POST("/auth/login/android/leader/token")
//login/android/token
    Observable<ResponseBody> getToken(@Query("username") String username,
                                      @Query("password") String password);

    /**
     * 获取用户信息
     */
    @GET("android/webapp/user")
    Observable<ResponseBody> getUserInfo();

    /**
     * 站点列表
     */
    @POST("/base/station/profile/dept")
    Observable<ResponseBody> getStationList(@Query("deptId") String deptId);

    /**
     * 获取轮播图
     */
    @GET("android/webapp/user")
    Observable<ResponseBody> getBanner();

    /**
     * 获取首页未提交记录数字
     */
    @GET("android/index/countNum/isCommit")
    Observable<ResponseBody> getUncommitNum();

    /**
     * 站长值班记录列表
     */
    @GET("android/station/duty/isCommit")
    Observable<ResponseBody> getAgentOndutyList(@Query("dateTime") String dateTime);

    /**
     * 班长值班记录列表
     */
    @GET("android/team/duty/isCommit")
    Observable<ResponseBody> getMonitorOndutyList(@Query("dateTime") String dateTime);

    /**
     * 票证值班记录列表
     */
    @GET("android/ticket/duty/isCommit")
    Observable<ResponseBody> getTicketOndutyList(@Query("dateTime") String dateTime);

    /**
     * 获取车道特情列表
     */
    @GET("android/secret/appRecord/list")
    Observable<ResponseBody> getSpecialOndutyList(@Query("page") String page,
                                                  @Query("limit") String limit,
                                                  @Query("stationId") String deptId,
                                                  @Query("startDate") String startDate,
                                                  @Query("endDate") String endDate);

    /**
     * 综合统计
     */
    @POST("/base/station/loopholes/car/comprehensiveStatistics")
    Observable<ResponseBody> getStatistics(@Query("stationId") String stationId);


    /**
     * 值班表（每月）
     */
    @GET("/base/arrange/duty/manage")
    Observable<ResponseBody> getOndutyList(@Query("stationId") String stationId,
                                           @Query("dutyDate") String month);

    /**
     * 获取站人员信息
     */
    @GET("android/webapp/getDeptPerson")
    Observable<ResponseBody> getDeptPerson(@Query("deptId") String deptId);

    /**
     * 检测新版本
     */
    @GET("android/version/latestVersion")
    Observable<ResponseBody> checkNewVersion();

    /**
     * 下载文件或apk
     *
     * @param url
     */
    @GET()
    @Streaming
    Observable<ResponseBody> downloadFileOrApk(@Url String url);

}