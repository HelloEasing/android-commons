package com.easing.commons.android.value.http;

public enum HttpMethod {

    GET("Get"),
    URL_ENCODED_POST("Post"),
    MULTI_FORM_POST("Post"),
    RAW_POST("Post"),
    BINARY_POST("Post"),
    PUT("Put"),
    PATCH("Patch"),
    DELETE("Delete");

    String originMethod;

    HttpMethod(String originMethod) {
        this.originMethod = originMethod;
    }

    public String originMethod() {
        return originMethod;
    }
}

