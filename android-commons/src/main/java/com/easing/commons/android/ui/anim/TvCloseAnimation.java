package com.easing.commons.android.ui.anim;

import android.graphics.Matrix;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

//模拟TV关闭时的动画
public class TvCloseAnimation extends Animation {

    private float cx;
    private float cy;

    public TvCloseAnimation() {
    }

    //通过Matrix来控制每个插值点的变换参数
    @Override
    protected void applyTransformation(float interpolatedValue, Transformation transfer) {
        Matrix matrix = transfer.getMatrix();
        //通过矩阵来控制缩放参数
        //四个参数分别指：横向缩放比例，纵向缩放比例，缩放中心横坐标，缩放中心纵坐标
        matrix.postScale(1, 1 - interpolatedValue, cx, cy);
    }

    //这个方法可以拿到控件和容器的初始大小
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        setDuration(500);
        setFillAfter(true);
        setInterpolator(new AccelerateInterpolator());
        cx = width / 2f;
        cy = height / 2f;
    }
}
