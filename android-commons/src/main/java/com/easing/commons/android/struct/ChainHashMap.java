package com.easing.commons.android.struct;

import java.util.HashMap;

public class ChainHashMap<K, V> extends HashMap<K, V> {

    public static <K, V> ChainHashMap<K, V> create() {
        return new ChainHashMap<K, V>();
    }

    public ChainHashMap<K, V> set(K key, V value) {
        super.put(key, value);
        return this;
    }
}
