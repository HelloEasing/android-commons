package com.easing.commons.android.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONObject;

import lombok.SneakyThrows;

public class StringJsonConverter implements PropertyConverter<JSONObject, String> {

    @Override
    @SneakyThrows
    public JSONObject convertToEntityProperty(String dbValue) {
        return new JSONObject(dbValue);
    }

    @Override
    public String convertToDatabaseValue(JSONObject objValue) {
        return objValue.toString();
    }


}
