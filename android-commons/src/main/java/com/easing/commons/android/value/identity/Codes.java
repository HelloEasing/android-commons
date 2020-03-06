package com.easing.commons.android.value.identity;

import com.easing.commons.android.format.Maths;

import java.util.Vector;

//常用事件或消息的ID标识
public class Codes {

    private static final Vector<Integer> codes = new Vector();

    public static final int CODE_PICK_FILE = randomCode();
    public static final int CODE_PICK_IMAGE = randomCode();
    public static final int CODE_IMAGE_CAPTURE = randomCode();
    public static final int CODE_VIDEO_CAPTURE = randomCode();
    public static final int CODE_AUDIO_CAPTURE = randomCode();

    public static int randomCode() {
        int code = Maths.randomInt(1000, 9000);
        code = codes.contains(code) ? randomCode() : code;
        codes.add(code);
        return code;
    }
}
