package com.easing.commons.android.helper.data;

//包装数据
//当基本类型需要final才能访问时，同时又必须修改数值时，可以通过一个Data对象来包装
public class Data<T> {

    public T data;

    public static Data create() {
        return new Data();
    }

    public static <T> Data create(T data) {
        Data<T> d = new Data();
        d.data = data;
        return d;
    }

    public Data<T> set(T data) {
        this.data = data;
        return this;
    }
}
