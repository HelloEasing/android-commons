package com.easing.commons.android.value.color;

import android.content.Context;
import android.view.View;

import com.easing.commons.android.format.Maths;
import com.easing.commons.android.app.CommonApplication;

public class Colors {
    public static final int TRANSPARENT = 0x00000000;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int WHITE_50 = 0x7FFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int BLACK_10 = 0x19000000;
    public static final int BLACK_20 = 0x33000000;
    public static final int BLACK_40 = 0x66000000;
    public static final int BLACK_50 = 0x7F000000;
    public static final int BLACK_70 = 0xB2000000;
    public static final int BLACK_80 = 0xCC000000;
    public static final int LIGHT_GREY = 0xFF989898;
    public static final int RED = 0xFFFF0000;
    public static final int RED_50 = 0x7FFF0000;
    public static final int APPLE_GREEN = 0xFF99CC00;
    public static final int YELLOW = 0xFFFFFF00;
    public static final int ORANGE = 0xFFFF6600;
    public static final int ORANGE_50 = 0x7FFF6600;
    public static final int ORANGE_70 = 0xBBFF6600;
    public static final int LIGHT_BLUE = 0xFF3399FF;
    public static final int LIGHT_BLUE_50 = 0x7F3399FF;
    public static final int LIGHT_BLUE_70 = 0xBB3399FF;
    public static final int TEST = 0xFFFFEE00;

    public static int reverseColor(int color, int alpha) {
        String str = Maths.hexToString(color);
        if (str.length() != 8)
            throw new RuntimeException("need a color value of 8 chars");

        int r = 255 - Maths.hexStringToInt(str.substring(2, 4));
        int g = 255 - Maths.hexStringToInt(str.substring(4, 6));
        int b = 255 - Maths.hexStringToInt(str.substring(6, 8));
        return alpha * 0x01000000 + r * 0x010000 + g * 0x0100 + b * 0x01;
    }

    public static int getColor(int resourceId) {
        return CommonApplication.ctx.getResources().getColor(resourceId, null);
    }

    public static int getColor(Context ctx, int resourceId) {
        return ctx.getResources().getColor(resourceId);
    }

    public static int getColor(View v, int resourceId) {
        return v.getContext().getResources().getColor(resourceId);
    }

    public static int randomColor() {
        return Maths.randomInt(0xFF000000, 0xFFFFFFFF);
    }
}
