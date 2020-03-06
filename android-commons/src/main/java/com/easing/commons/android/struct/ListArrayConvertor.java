package com.easing.commons.android.struct;

import java.util.ArrayList;
import java.util.List;

public interface ListArrayConvertor<R> {

    R[] buildArray(int length);

    default R[] toArray(List<R> list) {
        R[] array = buildArray(list.size());
        for (int i = 0; i < list.size(); i++)
            array[i] = list.get(i);
        return array;
    }

    public static <T> void main(String[] args) {
        ListArrayConvertor<String> convertor = String[]::new;
        List<String> list = new ArrayList();
        list.add("A");
        list.add("B");
        String[] array1 = convertor.buildArray(5);
        String[] array2 = convertor.toArray(list);
    }
}
