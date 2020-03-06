package com.easing.commons.android.struct;

import java.util.Arrays;
import java.util.List;

public interface ArrayListConvertor {

    public static <T> List<T> toList(T[] array) {
        return Arrays.asList(array);
    }
}
