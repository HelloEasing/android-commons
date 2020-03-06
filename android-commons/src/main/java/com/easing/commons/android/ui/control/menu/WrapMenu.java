package com.easing.commons.android.ui.control.menu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easing.commons.android.R;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.measure.Size;

public class WrapMenu extends RelativeLayout {

    private View layout;
    private ImageView iv;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    private Boolean opend = false;

    private int radius;

    private AnimatorSet animatorSet;

    public WrapMenu(Context context) {
        this(context, null);
    }

    public WrapMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapMenu(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        layout = LayoutInflater.from(context).inflate(R.layout.layout_wrap_menu, this);
        iv = layout.findViewById(R.id.iv);
        iv1 = layout.findViewById(R.id.iv1);
        iv2 = layout.findViewById(R.id.iv2);
        iv3 = layout.findViewById(R.id.iv3);

        iv.setOnClickListener(v -> {
            synchronized (opend) {
                if (animatorSet != null)
                    animatorSet.end();
                if (opend)
                    animateIn();
                else
                    animateOut();
            }
        });

        layout.setOnClickListener(v -> {
            animateIn();
        });

        iv.setOnTouchListener(new OnTouchListener() {

            private int sx;
            private int sy;
            private long t;
            private Boolean longDrag;
            private Size size;

            @Override
            public boolean onTouch(View v, MotionEvent me) {
                switch (me.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sx = (int) me.getX();
                        sy = (int) me.getY();
                        t = Times.millisOfNow();
                        longDrag = null;
                        size = Device.getScreenSize(getContext());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        long offsetTime = Times.millisOfNow() - t;
                        int offsetX = (int) me.getX() - sx;
                        int offsetY = (int) me.getY() - sy;
                        if (longDrag == null) {
                            if (offsetTime > 500)
                                if (offsetX < 10 && offsetY < 10)
                                    longDrag = true;
                            if (offsetTime < 500)
                                if (offsetX > 10 || offsetY > 10)
                                    longDrag = false;
                        } else if (longDrag) {
                            int left = layout.getLeft() + offsetX;
                            int top = layout.getTop() + offsetY;
                            layout.layout(left, top, size.w - left, size.h - top);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        radius = Views.size(iv).w * 2;
    }

    //展开
    private void animateOut() {
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                //透明
                ObjectAnimator.ofFloat(iv, "alpha", 0.8f, 1.0f),
                ObjectAnimator.ofFloat(iv1, "alpha", 0.5f, 1.0f),
                ObjectAnimator.ofFloat(iv2, "alpha", 0.5f, 1.0f),
                ObjectAnimator.ofFloat(iv3, "alpha", 0.5f, 1.0f),
                //平移
                ObjectAnimator.ofFloat(iv1, "translationY", -radius * 0.3f, -radius),
                ObjectAnimator.ofFloat(iv1, "translationX", 0, -radius * 0.2f),
                ObjectAnimator.ofFloat(iv2, "translationX", -radius * 0.3f, -radius),
                ObjectAnimator.ofFloat(iv2, "translationY", 0, 0),
                ObjectAnimator.ofFloat(iv3, "translationY", radius * 0.3f, radius),
                ObjectAnimator.ofFloat(iv3, "translationX", 0, -radius * 0.2f),
                //翻转
                ObjectAnimator.ofFloat(iv1, "rotationX", 0f, 90f, 0f),
                ObjectAnimator.ofFloat(iv2, "rotationY", 0f, 90f, 0f),
                ObjectAnimator.ofFloat(iv3, "rotationX", 0f, 90f, 0f),
                //旋转
                ObjectAnimator.ofFloat(iv, "rotation", 0f, 120f, 0f)
        );
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(500);
        animatorSet.start();
        opend = true;
        layout.setClickable(true);
    }

    //收缩
    private void animateIn() {
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                //透明
                ObjectAnimator.ofFloat(iv, "alpha", 1.0f, 0.8f),
                ObjectAnimator.ofFloat(iv1, "alpha", 1.0f, 0.5f, 0.1f, 0.0f),
                ObjectAnimator.ofFloat(iv2, "alpha", 1.0f, 0.5f, 0.1f, 0.0f),
                ObjectAnimator.ofFloat(iv3, "alpha", 1.0f, 0.5f, 0.1f, 0.0f),
                //平移
                ObjectAnimator.ofFloat(iv1, "translationY", -radius, 0),
                ObjectAnimator.ofFloat(iv1, "translationX", -radius * 0.2f, 0),
                ObjectAnimator.ofFloat(iv2, "translationX", -radius, 0),
                ObjectAnimator.ofFloat(iv2, "translationY", 0, 0),
                ObjectAnimator.ofFloat(iv3, "translationY", radius, 0),
                ObjectAnimator.ofFloat(iv3, "translationX", -radius * 0.2f, 0),
                //翻转
                ObjectAnimator.ofFloat(iv1, "rotationX", 0f, -90f, 0f),
                ObjectAnimator.ofFloat(iv2, "rotationY", 0f, -90f, 0f),
                ObjectAnimator.ofFloat(iv3, "rotationX", 0f, -90f, 0f),
                //旋转
                ObjectAnimator.ofFloat(iv, "rotation", 0f, -120f, 0f)
        );
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(500);
        animatorSet.start();
        opend = false;
        layout.setClickable(false);
    }
}
