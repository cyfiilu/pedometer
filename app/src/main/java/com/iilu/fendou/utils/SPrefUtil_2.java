package com.iilu.fendou.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.iilu.fendou.MainApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPrefUtil_2 {

    /**
     * 保存数据的方法，需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String spName , String key, Object object) {
        if (context == null) context = MainApplication.getAppContext();
        if (context == null) return;

        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SPrefCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static <T> T get(Context context, String spName, String key, T defaultValue) {
        if (context == null) context = MainApplication.getAppContext();
        if (context == null) return null;

        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);

        if (defaultValue instanceof String) {
            return (T) sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(sp.getInt(key, (Integer) defaultValue));
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(sp.getBoolean(key, (Boolean) defaultValue));
        } else if (defaultValue instanceof Float) {
            return (T) Float.valueOf(sp.getFloat(key, (Float) defaultValue));
        } else if (defaultValue instanceof Long) {
            return (T) Long.valueOf(sp.getLong(key, (Long) defaultValue));
        }

        return defaultValue;
    }

    /**
     * 清除所有数据
     * @param context
     */
    public static void clear(Context context,String FILE_NAME) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SPrefCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context,String spName, String key) {
        if (context == null) return false;
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context, String spName) {
        if (context == null) return null;
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SPrefCompat {

        private static Method applyMethod = null;

        /**
         * 反射查找apply的方法
         */
        static {
            try {
                Class clazz = SharedPreferences.Editor.class;
                applyMethod =  clazz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            if (applyMethod != null) {
                try {
                    applyMethod.invoke(editor);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                editor.commit();
            }
        }
    }


}
