package com.kunbo.app.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Iterator;


/**
 * @summary 将json格式的字符串转换成Map<String, Object>或者List<Map<String, Object>>
 */
public class JsonUtil {

    /**
     * @param jsonString 要转换的json格式的字符串
     * @return 集合格式： MyRow  Map<String, Object>
     * @summary 将JSON格式的字符串转换成Map
     */
    public static MyRow getRow(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            MyRow valueRow = new MyRow();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    MyRow row = getRow((JSONObject) value);
                    valueRow.put(key, row);
                } else if (value instanceof JSONArray) {
                    MyData data = getMyData((JSONArray) value);
                    valueRow.put(key, data);
                } else {
                    valueRow.put(key, value);
                }
            }
            return valueRow;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }


    /**
     * summary  将JSON格式的字符串转换成List
     *
     * @param jsonString 要转换的json格式的字符串
     * @return 集合格式：MyData  List<Map<String, Object>>
     */
    public static MyData getMyData(String jsonString) {
        MyData list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new MyData();
            for (int i = 0; i < jsonArray.length(); i++) {
                Object o = jsonArray.get(i);
                if (o instanceof JSONObject) {
                    jsonObject = jsonArray.getJSONObject(i);
                    list.add(getRow(jsonObject.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private static MyRow getRow(JSONObject jobj) throws Exception {
        MyRow row = new MyRow();
        Iterator<String> keys = jobj.keys();
        Object obj;
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            obj = jobj.get(key);
            //嵌套数组
            if (obj instanceof JSONArray) {
                row.put(key, getMyData((JSONArray) obj));
            } else if (obj instanceof JSONObject) {
                row.put(key, getRow((JSONObject) obj));
            } else {
                row.put(key, getObject(obj));
            }
        }
        return row;
    }


    private static String getObject(Object obj) {
        if (obj == null) {
            return "";
        }
        String str = obj.toString();
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (str.equals("null")) {
            return "";
        }
        return str;
    }


    private static MyData getMyData(Object obj) throws Exception {
        MyData list = new MyData();
        if (obj instanceof JSONArray) {
            JSONArray jsonArr = (JSONArray) obj;
            //JSONArray
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jobj = jsonArr.getJSONObject(i);
                MyRow row1 = getRow(jobj);
                list.add(row1);
            }
        } else if (obj instanceof JSONObject) {
            JSONObject jobj = (JSONObject) obj;
            MyRow row2 = getRow(jobj);
            list.add(row2);
        }
        return list;
    }

    public static Object stringToJson(String str) {
        Gson gson = new Gson();
        return gson.fromJson(str, MyRow.class);
    }
}

	

