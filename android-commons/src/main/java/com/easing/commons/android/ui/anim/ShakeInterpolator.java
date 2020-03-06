package com.easing.commons.android.ui.anim;

import android.view.animation.Interpolator;

public class ShakeInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float timeElapsed) {
        if (timeElapsed <= 0.25f)
            return timeElapsed / 0.25f;
        else if (timeElapsed <= 0.75f)
            return 1 - (timeElapsed - 0.25f) / 0.25f;
        else
            return -1 + (timeElapsed - 0.75f) / 0.25f;
    }
}
