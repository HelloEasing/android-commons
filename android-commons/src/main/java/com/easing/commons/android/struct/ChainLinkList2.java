package com.easing.commons.android.struct;

import java.util.LinkedList;

//添加几个常用方法
@Deprecated
public class ChainLinkList2<T> extends LinkedList<T> {

    public T first() {
        if (this.size() == 0)
            return null;
        return super.getFirst();
    }

    public T last() {
        if (this.size() == 0)
            return null;
        return super.getLast();
    }

    public T previous(T obj) {
        int index = indexOf(obj);
        if (index == -1)
            throw new RuntimeException("list do not contain the item");
        if (index == 0)
            return null;
        return super.get(index - 1);
    }

    public T next(T obj) {
        int index = indexOf(obj);
        if (index == -1)
            throw new RuntimeException("list do not contain the item");
        if (index == super.size() - 1)
            return null;
        return super.get(index + 1);
    }
}
