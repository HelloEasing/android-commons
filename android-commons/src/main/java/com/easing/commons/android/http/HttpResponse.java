package com.easing.commons.android.http;

import com.easing.commons.android.data.JSON;
import com.easing.commons.android.helper.data.JsonSerial;

import org.json.JSONObject;

import lombok.SneakyThrows;
import okhttp3.Response;

public class HttpResponse<T> implements JsonSerial {

    public Integer code;
    public String message;
    public T data;

    public String body;

    public String detail;

    private HttpResponse(){
    }

    public HttpResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> HttpResponse success(T data) {
        return new HttpResponse(0, "success", data);
    }

    public static HttpResponse fail(String message) {
        return new HttpResponse(-1, message, null);
    }

    public static HttpResponse fail(Exception e) {
        return new HttpResponse(-1, e.getClass().getName(), "");
    }

    public HttpResponse<T> detail(String detail) {
        this.detail = detail;
        return this;
    }

    @SneakyThrows
    public static HttpResponse parse(Response okhttpResponse) {
        String body = okhttpResponse.body().string();
        JSONObject jsonObject = JSON.toJsonObject(body);
        HttpResponse response = new HttpResponse();
        response.body = body;
        response.code = jsonObject.getInt("code");
        response.message = jsonObject.getString("msg");
        return response;
    }

    @SneakyThrows
    public static HttpResponse parse(String json) {
        JSONObject jsonObject = JSON.toJsonObject(json);
        HttpResponse response = new HttpResponse();
        response.code = jsonObject.getInt("code");
        response.message = jsonObject.getString("msg");
        return response;
    }

    @SneakyThrows
    public static <T> HttpResponse<T> parse(String json, Class<T> clazz) {
        JSONObject jsonObject = JSON.toJsonObject(json);
        HttpResponse<T> response = new HttpResponse();
        response.code = jsonObject.getInt("code");
        response.message = jsonObject.getString("msg");
        if (jsonObject.has("data"))
            response.data = JSON.parse(jsonObject.get("data").toString(), clazz);
        return response;
    }

    @SneakyThrows
    public static <T> HttpResponse<T> parse(Response response, Class<T> clazz) {
        String body = response.body().string();
        HttpResponse<T> resp = parse(body, clazz);
        resp.body = body;
        return resp;
    }

    public HttpResponse code(int coode) {
        this.code = coode;
        return this;
    }

    public HttpResponse message(String message) {
        this.message = message;
        return this;
    }

    public HttpResponse data(T data) {
        this.data = data;
        return this;
    }
}
