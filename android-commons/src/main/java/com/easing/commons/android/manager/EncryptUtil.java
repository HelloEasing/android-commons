package com.easing.commons.android.manager;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtil {

    public static String sha1(String data) {
        return new String(Hex.encodeHex(DigestUtils.sha1(data)));
    }
}
