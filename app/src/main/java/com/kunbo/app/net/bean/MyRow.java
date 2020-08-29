package com.kunbo.app.net.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 数据量不大，最好在千级以内
 * 数据结构类型为Map类型
 */
public class MyRow extends HashMap<String, Object> implements Serializable {

/*	public K keyAt(int index)
    public V valueAt(int index)*/


    public String getString(String name) {
        String ret = "";
        try {
            if (!containsKey(name)) {
                return "";
            } else {
                ret = get(name).toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int getInt(String name) {
        int ret = 0;
        try {
            if (!containsKey(name)) {
                return 0;
            } else {
                ret = Integer.parseInt(get(name).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public double getDouble(String name) {
        double ret = 0;
        try {
            if (!containsKey(name)) {
                return 0.00;
            } else {
                ret = Double.parseDouble(get(name).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public float getFloat(String name) {
        float ret = 0;
        try {
            if (!containsKey(name)) {
                return 0.00f;
            } else {
                ret = Float.parseFloat(get(name).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean getBoolean(String name) {
        boolean ret = false;
        try {
            if (!containsKey(name)) {
                return false;
            } else {
                ret = Boolean.parseBoolean(get(name).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static MyRow fromMap(Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        MyRow row = new MyRow();
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            row.put(key.toString(), map.get(key));
        }
        return row;
    }


//    @Override
//    public Object put(String key, Object value) {
//        if (value instanceof String) {
//            if (!TextUtils.isEmpty((String) value)) {
//                return super.put(key, value);
//            } else {
//				return this;
//            }
//        } else {
//            return super.put(key, value);
//        }
//    }
}
