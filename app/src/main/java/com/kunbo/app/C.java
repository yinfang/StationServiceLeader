package com.kunbo.app;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class C {
    //---------------------------------------------数字格式化--------------------------------------------------
    public static final DecimalFormat nf_i = new DecimalFormat("#,##0");// integer
    public static final DecimalFormat nf_a = new DecimalFormat("###0.00");// amount
    public static final DecimalFormat nf_l = new DecimalFormat("###0.000000");// GPS
    public static final DecimalFormat nf_s = new DecimalFormat("#,###.#");// 1位小数

    //----------------------------------------------日期格式化--------------------------------------------------
    private static Locale loc = Locale.getDefault();
    public static final DateFormat df_y = new SimpleDateFormat("yyyy", loc);
    public static final DateFormat df_yM = new SimpleDateFormat("yyyy-MM", loc);
    public static final DateFormat df_yMd = new SimpleDateFormat("yyyy-MM-dd", loc);
    public static final DateFormat df_ydotMdotd = new SimpleDateFormat("yyyy.MM.dd", loc);
    public static final DateFormat dff_yMdHm = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss", loc);
    public static final DateFormat df_yMdHm = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", loc);
    public static final DateFormat df_yMdH = new SimpleDateFormat(
            "yyyy-MM-dd HH:00", loc);
    public static final DateFormat df_yMdHms = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", loc);
    public static final DateFormat df_dot_yMdHms = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss", loc);
    public static final DateFormat dfs_yMdHms = new SimpleDateFormat(
            "yyyy年MM月dd日 HH:mm:ss", loc);
    public static final DateFormat dfs_yMd = new SimpleDateFormat(
            "yyyy年MM月dd日", loc);
    public static final DateFormat df_Hm = new SimpleDateFormat("HH:mm", loc);
    public static final DateFormat df_Hms = new SimpleDateFormat("HH:mm:ss", loc);
    //----------------------------------------------网络及app配置相关--------------------------------------------------
    //url不存在
    public static int URL_NO_EXIST = 404;//url不存在
    public static int NET_ERROR = 504;// 网络错误
    public static int SUCCESS = 0;// 操作成功

    public static int dialogTheme = -1;//自定义dialog样式默认白色（android.R.style.Theme_Holo_DialogWhenLarge 灰色）
    public static String APP_ID = "StaitonService";
    public static String TOKEN_TYPE = "Bearer";
    public static String TOKEN = "8e627d82-76a2-4b5a-ae8d-76619a24575c";
    public static int pageIndex = 1;
    public static int pageSize = 10;
    public static String apiDataKey = "data";//json 数据对象的key可在项目中配置
    public static String apiState = "code";
    public static String apiMsg = "message";

    private static final int PACKAGE_TYPE = 1;//1正式网  2开发网
    public static final String BASE_URL_INTERNET;//服务端地址
    public static final String BASE_URL_IMAGE;//图片地址

    public static final String PUSH_IP;//推送ip
    public static final int PUSH_PORT = 5672;//推送端口
    public static final String PUSH_USERNAME = "test";//推送账号
    public static final String PUSH_PASSWORD = "123@456";//推送密码
    public static final String PUSH_QUENE_NAME = "openCloud.android.message";

    public static String DEPT_ID = "1138362649422352385";//西渭分公司deptId

    static {
        if (PACKAGE_TYPE == 1) {//正式网
            BASE_URL_INTERNET = "http://39.98.206.226:8888/";
            BASE_URL_IMAGE = "http://39.98.206.226:8888";
            PUSH_IP = "39.98.206.226";
        } else if (PACKAGE_TYPE == 2) {//开发网
            BASE_URL_INTERNET = "http://192.168.0.183:8888/";//whg
            BASE_URL_IMAGE = "http://192.168.0.183:8888";
            /*BASE_URL_INTERNET = "http://192.168.0.137:8888/";//sz
            BASE_URL_IMAGE = "http://192.168.0.137:8888";*/
             /*  BASE_URL_INTERNET = "http://192.168.0.115:8888/";//lxm
            BASE_URL_IMAGE = "http://192.168.0.115:8888";*/
            /*BASE_URL_INTERNET = "http://192.168.0.170:8888/";//下线服务器测试环境
            BASE_URL_IMAGE = "http://192.168.0.170:8888";*/
            PUSH_IP = "192.168.0.170";
        }
    }
}
