package com.easing.commons.android.struct;

import com.easing.commons.android.helper.exception.BizException;

import java.util.LinkedHashMap;

import lombok.Getter;

//通过多个索引来存储数据的MAP
public class MultiDimenMap<T> {

    @Getter
    private LinkedHashMap coreMap;

    private int dimen = 1;

    private MultiDimenMap() {
    }

    public static MultiDimenMap init(int dimen) {
        if (dimen <= 0)
            throw BizException.of("key dimen must be positive integer");
        MultiDimenMap map = new MultiDimenMap();
        map.dimen = dimen;
        map.coreMap = new LinkedHashMap();
        return map;
    }

    public void put(T value, Object... keys) {
        if (keys.length != dimen)
            throw BizException.of("key dimen must equal to init length");
        LinkedHashMap map = coreMap;
        for (int i = 0; i < keys.length - 1; i++) {
            if (map.get(keys[i]) == null)
                map.put(keys[i], new LinkedHashMap());
            map = (LinkedHashMap) map.get(keys[i]);
        }
        map.put(keys[keys.length - 1], value);
    }

    public T get(Object... keys) {
        if (keys.length != dimen)
            throw BizException.of("key dimen must equal to init length");
        LinkedHashMap map = coreMap;
        for (int i = 0; i < keys.length - 1; i++) {
            if (map.get(keys[i]) == null)
                return null;
            map = (LinkedHashMap) map.get(keys[i]);
        }
        return (T) map.get(keys[keys.length - 1]);
    }

    public LinkedHashMap getMap(Object... keys) {
        LinkedHashMap map = coreMap;
        if (keys.length >= dimen)
            throw BizException.of("key dimen must be less than init length");
        for (int i = 0; i < keys.length; i++) {
            if (map.get(keys[i]) == null)
                return null;
            map = (LinkedHashMap) map.get(keys[i]);
        }
        return map;
    }

    public static void main(String[] args) {
        MultiDimenMap<String> map = MultiDimenMap.init(3);
        map.put("hello", "2017-11-05", "CS14", 1);
        map.put("hello", "2017-11-05", "CS14", 11);
        String value = map.get("2017-11-05", "CS14", 1);
        System.out.println(value);
    }
}
