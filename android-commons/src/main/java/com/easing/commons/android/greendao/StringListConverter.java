package com.easing.commons.android.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;

public class StringListConverter implements PropertyConverter<List<String>, String> {

    private static final String SPLIT = "#####";

    @Override
    public List<String> convertToEntityProperty(String dbValue) {
        if (dbValue != null)
            return Arrays.asList(dbValue.split(SPLIT));
        return null;
    }

    @Override
    public String convertToDatabaseValue(List<String> objValue) {
        if (objValue != null) {
            if (objValue.size() == 0)
                return "";
            StringBuilder builder = new StringBuilder();
            builder.append(objValue.get(0));
            for (int i = 1; i < objValue.size(); i++)
                builder.append(SPLIT).append(objValue.get(i));
            return builder.toString();
        }
        return null;
    }
}
