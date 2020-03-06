package com.easing.commons.android.struct;

import java.util.ArrayList;

//添加两个常用方法
@Deprecated
public class ChainArrayList2<T> extends ArrayList<T> {

    public T first() {
        if (this.size() == 0)
            return null;
        return this.get(0);
    }

    public T last() {
        if (this.size() == 0)
            return null;
        return this.get(this.size() - 1);
    }
}
