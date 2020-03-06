package com.easing.commons.android.struct;

import com.easing.commons.android.helper.callback.Comparator;
import com.easing.commons.android.helper.callback.Predication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Collections {

    public static <T> T[] toArray(Set<T> set, T[] array) {
        return set.toArray(array);
    }

    public static <T> T[] toArray(List<T> list, ListArrayConvertor<T> convertor) {
        return convertor.toArray(list);
    }

    public static String[] toStringArray(Set<String> set) {
        return set.toArray(new String[set.size()]);
    }

    public static Object[][] toArray2(List<Object[]> list) {
        if (list == null)
            return null;
        if (list.size() == 0)
            return new Object[0][0];
        int len1 = list.size();
        int len2 = list.get(0).length;
        Object[][] array = new Object[len1][len2];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len2; j++)
                array[i][j] = list.get(i)[j];
        return array;
    }

    public static <T> T getFirst(List<T> objs) {
        return objs.get(0);
    }

    public static <T> T getLast(List<T> objs) {
        return objs.get(objs.size() - 1);
    }

    //过滤数据
    public static <T> List<T> filter(List<T> datas, Predication<T> predication) {
        List<T> filtered = new ArrayList();
        for (T data : datas)
            if (predication.predicate(data))
                filtered.add(data);
        return filtered;
    }

    //排序数据
    public static <T> void sort(List<T> datas, Comparator<T> comparator) {
        java.util.Collections.sort(datas, comparator::compare);
    }

    //数组转List
    public static <T> LinkedList<T> asList(T[] array) {
        if (array == null) return null;
        LinkedList<T> list = new LinkedList();
        for (T item : array)
            list.add(item);
        return list;
    }

    //创建一个空的List
    public static <T> LinkedList<T> emptyList() {
        return new LinkedList();
    }

    //创建一个空的Map
    public static <K, V> LinkedHashMap<K, V> emptyMap() {
        return new LinkedHashMap();
    }

    //数组拷贝
    public static <T> LinkedList<T> listCopy(List<T> source) {
        LinkedList<T> dest = new LinkedList();
        for (T item : source)
            dest.add(item);
        return dest;
    }

    //MAP拷贝
    public static <K, T> Map<K, T> mapCopy(Map<K, T> source) {
        Map dest = new LinkedHashMap();
        for (K key : source.keySet())
            dest.put(key, source.get(key));
        return dest;
    }

}
