package com.easing.commons.android.helper.data;

import com.easing.commons.android.data.JSON;

import java.io.Serializable;

public interface JsonSerial extends Serializable {

    long SerializableID = 0L;

    default String stringfy() {
        return JSON.stringfy(this);
    }
}
