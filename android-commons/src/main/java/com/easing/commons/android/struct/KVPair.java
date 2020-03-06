package com.easing.commons.android.struct;

//键值対
public class KVPair<U, V> {

    private U u;
    private V v;

    public KVPair(U u, V v) {
        this.u = u;
        this.v = v;
    }

    public U getKey() {
        return u;
    }

    public V getValue() {
        return v;
    }
}
