package com.easing.commons.android.data;

import com.easing.commons.android.code.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.SneakyThrows;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

public class JSON {

    public static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    public static String stringfy(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T parse(String json, Class<T> clz) {
        try {
            return gson.fromJson(json, clz);
        } catch (Exception e) {
            Console.error(e);
            return null;
        }
    }

    public static <T> T parse(String json, Type type) {
        try {
            return (T) gson.fromJson(json, type);
        } catch (Exception e) {
            Console.error(e);
            return null;
        }
    }

    @SneakyThrows
    public static <T> T parse(Map<String, Object> paramMap, Class<T> clz) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : paramMap.keySet())
                jsonObject.put(key, paramMap.get(key));
            return JSON.parse(jsonObject.toString(), clz);
        } catch (Exception e) {
            Console.error(e);
            return null;
        }
    }

    @SneakyThrows
    public static String getString(String json, String key) {
        return new JSONObject(json).getString(key);
    }

    @SneakyThrows
    public static int getInt(String json, String key) {
        return new JSONObject(json).getInt(key);
    }

    @SneakyThrows
    public static Integer getInt(JSONObject json, String key) {
        return json.getInt(key);
    }

    @SneakyThrows
    public static String getString(JSONObject json, String key) {
        if (json.has(key) && !json.isNull(key))
            return json.getString(key);
        return null;
    }

    @SneakyThrows
    public static JSONObject getJsonObject(JSONObject json, String key) {
        return json.getJSONObject(key);
    }

    @SneakyThrows
    public static JSONArray getJsonArray(JSONObject json, String key) {
        return json.getJSONArray(key);
    }

    @SneakyThrows
    public static JSONObject toJsonObject(String json) {
        return new JSONObject(json);
    }

    //判断一个对象是不是JsonObject
    public static boolean isJsonObject(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //判断一个对象是不是JsonArray
    public static boolean isJsonArray(String json) {
        try {
            new JSONArray(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //JSON字符串美化
    @SneakyThrows
    public static String beautify(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson beautyGson = builder.create();
        if (JSON.isJsonObject(json))
            return beautyGson.toJson(new JSONObject(json));
        if (JSON.isJsonArray(json))
            return beautyGson.toJson(new JSONArray(json));
        return json;
    }
}
