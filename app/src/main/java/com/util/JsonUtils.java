package com.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 16/1/24.
 * JSON相关工具
 */
public class JsonUtils {

    /**
     * 将字符串转换成指定类对象,出错返回null
     */
    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        T result;
        try {
            result = new Gson().fromJson(jsonStr, clazz);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    /**
     * 将字符串转换成指定类数组,出错返回null
     */
    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        List<T> result = null;
        try {
            JsonElement ele = new JsonParser().parse(jsonStr);
            if (ele.isJsonArray()) {
                JsonArray array = ele.getAsJsonArray();
                for (JsonElement item : array) {
                    T t = new Gson().fromJson(item, clazz);
                    if (result == null)
                        result = new ArrayList<>();
                    result.add(t);
                }
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    /**
     * 将对象转换为json串,出错返回null
     */
    public static String toJsonStr(Object obj) {
        if (obj == null)
            return null;
        String result;
        try {
            result = new Gson().toJson(obj);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
}
