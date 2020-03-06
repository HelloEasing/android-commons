package com.easing.commons.android.code;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface Remark {

    String value();
}
