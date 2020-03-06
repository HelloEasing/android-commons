package com.easing.commons.android.value.option;

//下拉框选项
public class Option<T> {

    public String id;
    public String name;

    public T data;

    public static <T> Option<T> wrap(T data, IdTranslator<T> idTranslator, NameTranslator<T> nameTranslator) {
        Option<T> option = new Option();
        if (idTranslator != null) option.id = idTranslator.getId(data);
        if (nameTranslator != null) option.name = nameTranslator.getName(data);
        return option;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    public interface IdTranslator<T> {
        String getId(T data);
    }

    public interface NameTranslator<T> {
        String getName(T data);
    }
}
