package com.easing.commons.android.manager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

    public static final TranslateAnimation TRANSLATE_UP = AnimationUtil.translate(0f, 0f, 0f, -1f, 0.5f);
    public static final TranslateAnimation TRANSLATE_DOWN = AnimationUtil.translate(0f, 0f, -1f, 0f, 0.5f);

    //平移动画
    public static TranslateAnimation translate(float sx, float dx, float sy, float dy, float sec) {
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, sx, Animation.RELATIVE_TO_SELF, dx, Animation.RELATIVE_TO_SELF, sy, Animation.RELATIVE_TO_SELF, dy);
        anim.setDuration((long) (sec * 1000));
        anim.setFillAfter(true);
        return anim;
    }

    //控件逐渐缩小，直至隐藏
    public static Animator scaleIn(View v, long duration) {
        int h = v.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(h, 0);
        animator.addUpdateListener(animation -> {
            float ch = (float) animation.getAnimatedValue();
            v.getLayoutParams().height = (int) ch;
            v.requestLayout();
            if (ch == 0) {
                v.setVisibility(View.GONE);
                v.getLayoutParams().height = h;
                v.requestLayout();
            }
        });
        animator.setDuration(duration);
        animator.start();
        return animator;
    }

    //控件逐渐放大，直至显示
    public static Animator scaleOut(View v, long duration) {
        v.setVisibility(View.VISIBLE);
        int h = v.getLayoutParams().height;
        ValueAnimator animator = ValueAnimator.ofFloat(0, h);
        animator.addUpdateListener(animation -> {
            float ch = (float) animation.getAnimatedValue();
            v.getLayoutParams().height = (int) ch;
            v.requestLayout();
        });
        animator.setDuration(duration);
        animator.start();
        return animator;
    }
}
