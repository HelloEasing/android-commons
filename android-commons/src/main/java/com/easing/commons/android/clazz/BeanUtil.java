package com.easing.commons.android.clazz;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil {

    @SneakyThrows
    public static Map<String, Object> attributeMap(Object source) {
        Map<String, Object> attributeMap = new HashMap();
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(source) != null)
                attributeMap.put(field.getName(), field.get(source));
        }
        return attributeMap;
    }

    @SneakyThrows
    public static void copyAttribute(Object source, Map<String, Object> attributeMap) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(source) != null)
                attributeMap.put(field.getName(), field.get(source));
        }
    }

    @SneakyThrows
    public static void copyAttribute(String key, Object value, Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        if (fieldMap.containsKey(key))
            if (fieldMap.get(key).getType().getName().equals(value.getClass().getName()))
                fieldMap.get(key).set(target, value);
    }

    @SneakyThrows
    public static void copyAttribute(Map<String, Object> attributeMap, Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        for (String attribute : attributeMap.keySet())
            if (fieldMap.containsKey(attribute))
                if (attributeMap.get(attribute) != null)
                    fieldMap.get(attribute).set(target, attributeMap.get(attribute));
    }

    @SneakyThrows
    public static void copyAttribute(Object source, Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        Map<String, Field> targetFieldMap = new HashMap();
        for (Field targetField : fields) {
            targetField.setAccessible(true);
            targetFieldMap.put(targetField.getName(), targetField);
        }
        for (Field sourceField : source.getClass().getDeclaredFields()) {
            sourceField.setAccessible(true);
            if (targetFieldMap.containsKey(sourceField.getName()))
                if (sourceField.get(source) != null)
                    copyField(source, target, sourceField, targetFieldMap.get(sourceField.getName()));
        }
    }

    @SneakyThrows
    public static void copyAttribute(Object source, Object target, String fieldName) {
        for (Field sourceField : source.getClass().getDeclaredFields())
            if (sourceField.getName().equals(fieldName)) {
                sourceField.setAccessible(true);
                Object fieldValue = sourceField.get(fieldName);
                for (Field targetField : target.getClass().getDeclaredFields())
                    if (targetField.getName().equals(fieldName)) {
                        targetField.setAccessible(true);
                        targetField.set(target, fieldValue);
                        return;
                    }
            }
    }

    @SneakyThrows
    public static void copyField(Object source, Object target, Field sourceField, Field targetField) {
        if (targetField.getType().getName().equals(sourceField.getType().getName()))
            targetField.set(target, sourceField.get(source));
    }
}
