package com.kunbo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kunbo.app.C;
import com.kunbo.app.MyApplication;

import java.util.Map;
import java.util.Set;

/**
 * Created by long on 17-6-22.
 */

public class SPUtil {


    private static SharedPreferences getSP() {
        return MyApplication.getContext().getSharedPreferences(C.APP_ID, Context
                .MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSP().edit();
    }

    /**
     * 获取
     */

    public static boolean getBoolean(String name, boolean defvalue) {

        return getSP().getBoolean(name, defvalue);
    }

    public static int getInt(String name, int defvalue) {
        return getSP().getInt(name, defvalue);
    }

    public static float getFloat(String name, float defvalue) {
        return getSP().getFloat(name, defvalue);
    }

    public static long getLong(String name, long defvalue) {
        return getSP().getLong(name, defvalue);
    }

    public static String getString(String name, String defvalue) {
        return getSP().getString(name, defvalue);
    }

    public static Set<String> getStringSet(String name, Set<String> defvalue) {
        return getSP().getStringSet(name, defvalue);
    }

    /**
     * 此方法的返回的字典不要修改
     * @return
     */
    public static Map<String, ?> getAll() {
        return getSP().getAll();
    }


    /**
     *单个保存,不需要调用commit或者apply
     */
    public static boolean saveSetting(String name, boolean value) {
        return getEditor().putBoolean(name, value).commit();
    }

    public static boolean saveSetting(String name, String value) {
        return getEditor().putString(name, value).commit();
    }

    public static boolean saveSetting(String name, int value) {
        return getEditor().putInt(name, value).commit();
    }

    public static boolean saveSetting(String name, float value) {
        return getEditor().putFloat(name, value).commit();
    }

    public static boolean saveSetting(String name, long value) {
        return getEditor().putLong(name, value).commit();
    }

    public static boolean saveSetting(String name, Set<String> value) {
        return getEditor().putStringSet(name, value).commit();
    }


    /**
     * 多个保存,要调用commit或者apply
     */

    public static SharedPreferences.Editor putBoolean(String name, boolean value) {
        return getEditor().putBoolean(name, value);
    }

    public static SharedPreferences.Editor putString(String name, String value) {
        return getEditor().putString(name, value);
    }

    public static SharedPreferences.Editor putInt(String name, int value) {
        return getEditor().putInt(name, value);
    }

    public static SharedPreferences.Editor putFloat(String name, float value) {
        return getEditor().putFloat(name, value);
    }

    public static SharedPreferences.Editor putLong(String name, long value) {
        return getEditor().putLong(name, value);
    }

    public static SharedPreferences.Editor putStringSet(String name, Set<String> value) {
        return getEditor().putStringSet(name, value);
    }


    /**
     *移除单个
     */
    public static boolean removeSetting(String name) {
        return getEditor().remove(name).commit();
    }

    public static boolean clear() {
        return getEditor().clear().commit();
    }
}
