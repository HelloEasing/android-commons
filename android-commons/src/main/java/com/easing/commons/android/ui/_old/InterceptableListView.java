package com.easing.commons.android.ui._old;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import lombok.Setter;

public class InterceptableListView extends SwipeMenuRecyclerView {

    @Setter
    private boolean interceptable = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (interceptable)
            return true;
        return super.onInterceptTouchEvent(e);
    }

    public InterceptableListView(Context context) {
        super(context);
    }

    public InterceptableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
