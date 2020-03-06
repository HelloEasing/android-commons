package com.easing.commons.android.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

public class StringArrayConverter implements PropertyConverter<String[], String> {

    private static final String SPLIT = "#####";

    @Override
    public String[] convertToEntityProperty(String dbValue) {
        if (dbValue != null)
            return dbValue.split(SPLIT);
        return null;
    }

    @Override
    public String convertToDatabaseValue(String[] objValue) {
        if (objValue != null) {
            if (objValue.length == 0)
                return "";
            StringBuilder builder = new StringBuilder();
            builder.append(objValue[0]);
            for (int i = 1; i < objValue.length; i++)
                builder.append(SPLIT).append(objValue[i]);
            return builder.toString();
        }
        return null;
    }
}
