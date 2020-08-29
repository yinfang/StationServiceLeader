package com.kunbo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.kunbo.app.C;
import com.kunbo.app.activitys.BaseActivity;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.lemonhello.LemonHelloAction;
import com.kunbo.app.view.lemonhello.LemonHelloInfo;
import com.kunbo.app.view.lemonhello.LemonHelloView;
import com.kunbo.app.view.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Utils {
    public static byte[] getBytes(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getObject(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ObjectInputStream in = new ObjectInputStream(bais);
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(MyRow row, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                try {
                    if (row.containsKey(f.getName())) {
                        if (f.getType().equals(Integer.TYPE)) {
                            f.set(t, row.getInt(f.getName()));
                        } else if (f.getType().equals(Boolean.TYPE)) {
                            f.set(t, row.getBoolean(f.getName()));
                        } else if (f.getType().equals(Double.TYPE)) {
                            f.set(t, row.getDouble(f.getName()));
                        } else {
                            f.set(t, row.getString(f.getName()));
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MyRow toRow(Object o) {
        MyRow row = new MyRow();
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f : fields) {
            String name = f.getName();
            try {
                row.put(name, f.get(o));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return row;
    }

    public static MyRow getRow(Bundle b, String name) {
        return MyRow.fromMap((Map<?, ?>) b.getSerializable(name));
    }

    public static MyData getData(Bundle b, String name) {
        @SuppressWarnings("unchecked")
        List<Map> list = (List<Map>) b.getSerializable(name);
        MyData data = new MyData();
        for (Map<?, ?> map : list) {
            data.add(MyRow.fromMap(map));
        }
        return data;
    }

    public static double max(double[] items) {
        double ret = 0;
        for (int i = 0; i < items.length; i++) {
            ret = Math.max(ret, items[i]);
        }
        return ret;
    }

    public static double min(double[] items) {
        double ret = Integer.MAX_VALUE;
        for (int i = 0; i < items.length; i++) {
            ret = Math.min(ret, items[i]);
        }
        return ret;
    }


//--------------------------------------------------------------------------日期时间相关工具---------------------------------------------------------------------------------------

    /**
     * 获取当前日期(yyyy-MM-dd)
     *
     * @return date
     */
    public static Date getNowDate() {
        try {
            return C.df_yMd.parse(C.df_yMd.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取现在时间(yyyy-MM-dd HH:mm:ss)
     *
     * @return String
     */
    public static String getStringDateTime() {
        return C.df_yMdHms.format(new Date());
    }

    /**
     * 获取现在时间(yyyy-MM-dd)
     *
     * @return String
     */
    public static String getStringDate() {
        return C.dfs_yMd.format(new Date());
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getStringTime() {
        return C.df_Hms.format(new Date());
    }

    /**
     * 获取当前日期是星期几
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 获得标准格式的日期字符串 如 2014-07-18
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDateString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return C.df_yMd.format(c.getTime());
    }

    /**
     * 获得标准格式的时间字符串 如 08:51
     *
     * @param hour
     * @param minute
     * @return
     */

    public static String getTimeString(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return C.df_Hm.format(c.getTime());
    }

    /**
     * 格式化String为  年月日时分  如 2019-06-12 16:33
     *
     * @param sDate
     * @return
     */
    public static String getMDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMdHm.parse(sDate);
                result = C.df_yMdHm.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化String为  年月日 如 2019-06-12
     *
     * @param sDate
     * @return
     */
    public static String getDDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMd.parse(sDate);
                result = C.df_yMd.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化String为  年月日 如 2019-06-12   16:33:12
     *
     * @param sDate
     * @return
     */
    public static String getSDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMdHms.parse(sDate);
                result = C.df_yMdHms.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化String为 时分 如 16:33
     *
     * @param sDate
     * @return
     */
    public static String getHMDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate)) {

            } else {
                Date date = C.df_Hm.parse(sDate);
                result = C.df_Hm.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取日期差
     */
    public static int getMonthInterval(String startDate, String endDate)
            throws Exception {
        int result = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(C.df_yMd.parse(startDate));
        c2.setTime(C.df_yMd.parse(endDate));
        result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return result == 0 ? 1 : Math.abs(result);
    }

//---------------------------------------------------------------------------String相关工具-----------------------------------------------------------------------

    /**
     * 数字每三位添加逗号
     *
     * @param data
     * @return
     */
    public static String numberAddSepara(double data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }

    /**
     * String是否为空
     */
    public static boolean checkNullString(String s) {
        if (s == null || s.equals("null") || TextUtils.isEmpty(s) || s.equals("NULL")) {
            return true;
        }
        return false;
    }

    /**
     * 格式化字符串，去除特殊字符和空格
     */
    public static String handleString(String s) {
        String handleExpre = "</?[^<]+>\\s*|\t|\r|&nbsp;";
        return s.replaceAll(handleExpre, "");
    }

    /**
     * 判断一个字符串是否为数字型， 至少包含一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    /**
     * 判断一个字符串是否可转换为int
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * String转float
     */
    public static float stringToFloat(String s) {
        float a = 0.00f;
        try {
            if (checkNullString(s)) {
                a = 0.00f;
            } else {
                a = Float.parseFloat(s);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * String转int
     *
     * @param doubleStr
     * @return
     */
    public static int doubleToInt(String doubleStr) {
        if (doubleStr.indexOf(".") != -1) {
            String playernum = doubleStr.substring(0, doubleStr.indexOf("."));
            return Integer.parseInt(playernum);
        }
        if (TextUtils.isEmpty(doubleStr)) {
            return 0;
        }
        return Integer.parseInt(doubleStr);
    }

    /**
     * String数组转List集合
     */
    public static List<String> stringsToList(final String[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < src.length; i++) {
            result.add(src[i]);
        }
        return result;
    }

    /**
     * 字节数组转为16进制字符
     *
     * @param bytes
     * @return
     */
    public static String bytes2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 根据表示二进制的字符串得到实际的字节数组
     *
     * @param src 表示二进制的字符串
     * @return 得到的字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 获得指定正则表达式匹配的字符按串
     *
     * @param target 要检测的字符串
     * @param regex  正则表达式
     * @return 匹配的字符串数组
     */
    public static String[] getRegResult(String target, String regex) {
        List<String> result = new ArrayList<String>();
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(target);
            while (matcher.find()) {
                result.add(matcher.group());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toArray(new String[]{});
    }

    /**
     * 对远程获得的经过Base64解码的字符串进行解码。目前是为了防止不可见字符出现。<br/>
     * 不可见字符会造成SOAP编码错误不能进行传输
     *
     * @param str 经过Base64编码的UTF8字符串
     * @return
     */
    public static String getDecoded(String str) {
        try {
            str = new String(Base64.decode(str, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将字符串进行Base64编码。目前是为了防止不可见字符出现。<br/>
     * 不可见字符会造成SOAP编码错误不能进行传输
     *
     * @param str 源字符串
     * @return 经过Base64编码的字符串
     */
    public static String toEncoded(String str) {
        try {
            str = Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * String截取
     */
    public static String subStr(String str, int len) {
        if (str == null) {
            return "";
        } else {
            int len1 = len;
            String ret = str.substring(0, str.length() < len ? str.length()
                    : len);
            try {
                int len2 = ret.getBytes("GBK").length;
                while (len2 > len1) {
                    int len3 = --len;
                    ret = str.substring(0, len3 > str.length() ? str.length()
                            : len3);
                    len2 = ret.getBytes("GBK").length;
                    // subStrByetsL = subStr.getBytes().length;
                }
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 字符串进行SHA1算法签名
     */
    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }


//----------------------------------------------------------------------正则表达式过滤输入项------------------------------------------------------------------

    /**
     * 过滤html标签正则
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        //定义script的正则表达式
        String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式
        String REGEX_HTML = "<[^>]+>";
        //定义空格回车换行符
        String REGEX_SPACE = "\\s*|\t|\r|\n";
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim(); // 返回文本字符串
    }


    /**
     * 验证邮箱地址正则
     */
    public static boolean validateEmail(String email) {
        return email != null
                && (!email.equals(""))
                && email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\" +
                ".[A-Za-z0-9]+)*(\\.[A-Za-z]{2,5})$");
    }

    /**
     * 验证护照正则
     *
     * @return
     */
    public static boolean checkPassport(String passport) {
        return passport != null && passport.matches("^1[45][0-9]{7}|G[0-9]{8}|P[0-9]{7}|S[0-9]{7," +
                "8}|D[0-9]+$");
    }

    /**
     * 验证身份证号正则
     */
    public static boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证车牌号正则
     * 1.常规车牌号：仅允许以汉字开头，后面可录入六个字符，由大写英文字母和阿拉伯数字组成。如：粤B12345；
     * 2.武警车牌：允许前两位为大写英文字母，后面可录入五个或六个字符，由大写英文字母和阿拉伯数字组成，其中第三位可录汉字也可录大写英文字母及阿拉伯数字，第三位也可空，如：WJ警00081、WJ京1234J、WJ1234X。
     * 3.最后一个为汉字的车牌：允许以汉字开头，后面可录入六个字符，前五位字符，由大写英文字母和阿拉伯数字组成，而最后一个字符为汉字，汉字包括“挂”、“学”、“警”、“港”、“澳”。如：粤Z1234港。
     * 4.新军车牌：以两位为大写英文字母开头，后面以5位阿拉伯数字组成。如：BA12345。
     * 5.新能源车牌：省份简称（1位汉字）+发牌机关代号（1位字母）+序号（6位），总计8个字符，序号不能出现字母I和字母O
     * 通用规则：不区分大小写，第一位：省份简称（1位汉字），第二位：发牌机关代号（1位字母）
     * —小型车：
     * ------第一位：只能用字母D或字母F
     * ------第二位：字母或者数字
     * ------后四位：必须使用数字
     * ------([DF][A-HJ-NP-Z0-9][0-9]{4})
     * —大型车：
     * ------前五位：必须使用数字
     * ------第六位：只能用字母D或字母F
     * ------([0-9]{5}[DF])
     */
    public static boolean isCarNum(String carnumber) {

        String rex = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[a-zA-Z](([DF]((?![IO])[a-zA-Z0-9](?![IO]))[0-9]{4})|([0-9]{5}[DF]))|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1})$";
        if (TextUtils.isEmpty(carnumber)) return false;
        else return carnumber.matches(rex);
    }

    /**
     * 验证手机号正则
     * 有效手机号集合：
     * 166，
     * 176，177，178
     * 180，181，182，183，184，185，186，187，188，189
     * 145，147
     * 130，131，132，133，134，135，136，137，138，139
     * 150，151，152，153，155，156，157，158，159
     * 198，199
     */
    public static boolean checkMobile(String mobileNo)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobileNo);
        return m.matches();
    }

    /**
     * 验证固定电话正则
     */
    public static boolean validatePhoneAndFax(String in) {
        // ("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$");
        // 第1组：区号，以0开头，只允许输入0-9的数字，长度为2-3位，后面连接-号.
        // 第2组：固话，以2-9开头，只允许输入0-9的数字，长度为6-7位。
        // 第3组：分机号，前面连接-号,以0-9开头，长度为1-4位。
        return in != null && (!in.equals(""))
                && in.matches("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,15})?$");
        // var RegExp = ^\d{3,4}-\d{7,8}(-\d{3,4})?$;
    }


    /*----------------------------------------屏幕相关--------------------------------------------------------*/

    public static int dpToPx(Context context, int dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dpValue + 0.5f);
    }

    /**
     * 获取屏幕高度（除掉顶部状态栏的高度）
     *
     * @param context
     * @return in pixels
     */
    public static int getPureHeight(Context context) {
        int heightPixels = 0;
        try {
            Class<?> cl = null;
            Object obj = null;
            Field field = null;
            int x = 0, sbar = 0;
            cl = Class.forName("com.android.internal.R$dimen");
            obj = cl.newInstance();
            field = cl.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
            // 获取屏幕高度和宽度
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindow().getWindowManager()
                    .getDefaultDisplay().getMetrics(dm);
            heightPixels = dm.heightPixels - sbar;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return heightPixels;
    }

    /**
     * 获取屏幕像素宽度 px
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕像素高度 px
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 设置全屏
     */
    protected void setFullScreen(Activity activity, boolean isFullScreen) {
        if (isFullScreen == true) {
            //隐藏状态栏
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            }
        }
    }

    public static void showOKCancelDialog(BaseActivity context, int titleResId, int msgResId, int type) {
        showOKCancelDialog(context, titleResId, msgResId, type, null);
    }

    public static void showOKCancelDialog(BaseActivity context,
                                          int titleResId, int msgResId, int type, Object tag) {
        showOKCancelDialog(context, context.getText(titleResId).toString(), context.getText(msgResId).toString(), type, tag);
    }

    /**
     * 带确认和取消按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param type
     * @param tag
     */
    public static void showOKCancelDialog(final BaseActivity context, String title, String message, final int type, final Object tag) {
        new LemonHelloInfo().setTitle(title).setTitleFontSize(18).setContent(message).setContentFontSize(15)
                .addAction(new LemonHelloAction("确定", new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        lemonView.hide();
                        if (type != -1) {
                            context.processDialogOK(type, tag);
                        }
                    }
                }))
                .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        lemonView.hide();
                        if (type != -1) {
                            context.processDialogCancel(type, tag);
                        }
                    }
                })).show(context);
    }

}
