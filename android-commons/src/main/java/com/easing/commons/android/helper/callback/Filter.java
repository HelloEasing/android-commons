package com.easing.commons.android.helper.callback;

public interface Filter<T> {

    boolean keep(T v);
}
