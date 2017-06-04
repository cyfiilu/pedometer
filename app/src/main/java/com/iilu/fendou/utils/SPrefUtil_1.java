package com.iilu.fendou.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.iilu.fendou.MainApplication;

import java.util.Map;

public class SPrefUtil_1 {

    private SharedPreferences mSPref;
    private SharedPreferences.Editor mEditor;

    public SPrefUtil_1(Context context, String spName) {
        mSPref = context.getSharedPreferences(spName, Context.MODE_APPEND);
        mEditor = mSPref.edit();
    }

    /**
     * 保存以及更新数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param value
     */
    public void setParam( String key, Object value){
        if (value == null) return;

        String type = value.getClass().getSimpleName();
        if("String".equals(type)){
            mEditor.putString(key, (String)value);
        }
        else if("Integer".equals(type)){
            mEditor.putInt(key, (Integer)value);
        }
        else if("Boolean".equals(type)){
            mEditor.putBoolean(key, (Boolean)value);
        }
        else if("Float".equals(type)){
            mEditor.putFloat(key, (Float)value);
        }
        else if("Long".equals(type)){
            mEditor.putLong(key, (Long)value);
        }
        mEditor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key
     * @param defaultValue
     * @return
     */
    public Object getParam( String key, Object defaultValue){
        if (defaultValue == null) return null;

        String type = defaultValue.getClass().getSimpleName();
        if("String".equals(type)){
            return mSPref.getString(key, (String)defaultValue);
        }
        else if("Integer".equals(type)){
            return mSPref.getInt(key, (Integer)defaultValue);
        }
        else if("Boolean".equals(type)){
            return mSPref.getBoolean(key, (Boolean)defaultValue);
        }
        else if("Float".equals(type)){
            return mSPref.getFloat(key, (Float)defaultValue);
        }
        else if("Long".equals(type)){
            return mSPref.getLong(key, (Long)defaultValue);
        }
        return null;
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSPref.contains(key);
    }

    /**
     * 返回所有的键值对
     * @return
     */
    public Map<String, ?> getAll() {
        return mSPref.getAll();
    }

    /**
     * 删除所有信息
     */
    public void clearAll() {
        mEditor.clear();
        mEditor.commit();
    }

    public void remove(String key){
        mEditor.remove(key);
        mEditor.commit();
    }
}
