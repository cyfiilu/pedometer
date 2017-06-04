package com.iilu.fendou.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class GsonUtil {

    /**
     * @param object 包括map集合
     * @return
     */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * 转换成实体
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T toBean(String json, Class<T> cls) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }

    /**
     * json字符串转换成list集合
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    /**
     * json字符串转换成嵌套map的list
     * @param json
     * @param <T>
     * @return
     */
    public static <T> List<Map<String, T>> toListMaps(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {}.getType());
    }

    /**
     * json字符串转成map
     * @param json
     * @return
     */
    public static <T> Map<String, T> toMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Map<String, T>>() {}.getType());
    }
}
