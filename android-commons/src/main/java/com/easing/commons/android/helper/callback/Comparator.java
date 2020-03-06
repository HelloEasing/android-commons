package com.easing.commons.android.helper.callback;

public interface Comparator<T> {

    //比较对象大小，较小的排在前头
    int compare(T v1, T v2);
}
