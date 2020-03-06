package com.easing.commons.android.ui.control.list.easy_list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import lombok.Setter;

public class BaseList extends SwipeMenuRecyclerView {

    @Setter
    private boolean interceptable = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (interceptable)
            return true;
        return super.onInterceptTouchEvent(e);
    }

    public BaseList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
    }

}
