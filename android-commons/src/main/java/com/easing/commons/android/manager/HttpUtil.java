package com.easing.commons.android.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.easing.commons.android.io.Files;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    public static String paramToString(Map<String, ?> paramMap) {
        String appendUrl = "";
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null) {
                if (appendUrl.startsWith("?"))
                    appendUrl = appendUrl + "&" + key + "=" + value;
                else
                    appendUrl = appendUrl + "?" + key + "=" + value;
            }
        }
        return appendUrl;
    }

    public static void saveCookies(Context ctx, String name, List<Cookie> cookies) {
        SharedPreferences sp = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Cookie cookie : cookies)
            editor.putString(cookie.name(), cookie.value());
        editor.commit();
    }

    public static List<Cookie> loadCookies(Context ctx, String name) {
        SharedPreferences sp = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, ?> kvs = sp.getAll();
        List<Cookie> cookies = new ArrayList();
        for (String key : kvs.keySet()) {
            Cookie.Builder builder = new Cookie.Builder();
            builder.name(key);
            builder.value(kvs.get(key).toString());
            Cookie cookie = builder.build();
            cookies.add(cookie);
        }
        return cookies;
    }

    public static List<Cookie> EMPTY_COOKIE() {
        return new ArrayList();
    }

    public static FormBody paramToForm(Map<String, ?> paramMap) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null)
                builder.add(key, value.toString());
        }
        FormBody formBody = builder.build();
        return formBody;
    }

    public static MultipartBody paramToMultipartForm(Map<String, Object> paramMap, Map<File, String> fileMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse("multipart/form-data; charset=utf-8"));
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null)
                builder.addFormDataPart(key, value.toString());
        }
        for (File file : fileMap.keySet()) {
            RequestBody fileBody = createFileBody(file);
            builder.addFormDataPart(fileMap.get(file), file.getName(), fileBody);
        }
        MultipartBody formBody = builder.build();
        return formBody;
    }

    public static RequestBody createFileBody(File file) {
        String type = "*/*";
        String ext = Files.getExtensionName(file);
        if (ext.equals("png")) type = "image/png";
        if (ext.equals("jpg")) type = "image/jpeg";
        if (ext.equals("jpeg")) type = "image/jpeg";
        if (ext.equals("bmp")) type = "image/bmp";
        if (ext.equals("gif")) type = "image/gif";
        if (ext.equals("mp3")) type = "audio/mp3";
        if (ext.equals("mp4")) type = "video/mpeg4";
        if (ext.equals("txt")) type = "text/plain";
        if (ext.equals("html")) type = "text/html";
        if (ext.equals("css")) type = "text/css";
        if (ext.equals("js")) type = "application/x-javascript";
        return RequestBody.create(MediaType.parse(type), file);
    }
}
