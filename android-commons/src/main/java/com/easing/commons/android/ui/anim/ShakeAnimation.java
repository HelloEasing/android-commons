package com.easing.commons.android.ui.anim;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ShakeAnimation extends Animation {

    private float dx;
    private float dy;

    public ShakeAnimation(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    protected void applyTransformation(float interpolatedValue, Transformation transfer) {
        Matrix matrix = transfer.getMatrix();
        matrix.postTranslate(interpolatedValue * dx, interpolatedValue * dy);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        setDuration(500);
        setFillAfter(true);
        setRepeatCount(-1);
        setInterpolator(new ShakeInterpolator());
    }
}
