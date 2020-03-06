package com.easing.commons.android.struct;

import java.util.LinkedList;

//使用链式调用风格包装一个LinkedList，添加一些实用方法
public class ChainLinkedList<T> {

    public final LinkedList<T> baseList = new LinkedList();

    public static <T> ChainLinkedList<T> create(Class<T> clazz) {
        return new ChainLinkedList();
    }

    public ChainLinkedList<T> add(T item) {
        baseList.add(item);
        return this;
    }

    public ChainLinkedList<T> add(T... itemArray) {
        for (T item : itemArray)
            baseList.add(item);
        return this;
    }
}
