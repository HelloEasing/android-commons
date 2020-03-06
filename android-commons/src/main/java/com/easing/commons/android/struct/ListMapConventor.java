package com.easing.commons.android.struct;

import com.easing.commons.android.helper.callback.Conventor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//List转Map
public class ListMapConventor {

    public static <T, K, V> Map<K, V> toMap(List<T> list, Conventor<T, K> keyConventor, Conventor<T, V> valueConventor) {
        Map map = new HashMap();
        for (T item : list)
            map.put(keyConventor.convert(item), valueConventor.convert(item));
        return map;
    }
}
