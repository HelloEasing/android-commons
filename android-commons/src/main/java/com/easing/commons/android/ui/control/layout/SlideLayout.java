package com.easing.commons.android.ui.control.layout;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

//使用方法：
//布局的第一个子View是菜单
//布局的第二个子View是内容
public class SlideLayout extends FrameLayout {

    private Context ctx;

    private ViewDragHelper dragHelper;

    private View mainView;
    private View menuView;

    public SlideLayout(Context context) {
        this(context, null, 0);
    }

    public SlideLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        ctx = context;

        ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
            @Override
            //是否监听触摸事件
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return child == mainView;
            }

            @Override
            //处理水平滑动
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                int menuWidth = Math.min(menuView.getMeasuredWidth(), getMeasuredWidth() * 4 / 5);
                int offsetX = Math.min(left, menuWidth);
                if (offsetX > menuWidth) offsetX = menuWidth;
                if (offsetX < 0) offsetX = 0;
                return offsetX;
            }

            @Override
            //处理竖直滑动
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                return 0;
            }

            @Override
            //拖拽结束
            public void onViewReleased(@NonNull View child, float xvel, float yvel) {
                super.onViewReleased(child, xvel, yvel);
                int menuWidth = Math.min(menuView.getMeasuredWidth(), getMeasuredWidth() * 4 / 5);
                boolean slideOut = mainView.getLeft() >= menuWidth * 2 / 3;
                if (slideOut) {
                    //滑出
                    dragHelper.smoothSlideViewTo(mainView, menuWidth, 0);
                    ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
                } else {
                    //滑入
                    dragHelper.smoothSlideViewTo(mainView, 0, 0);
                    ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
                }
            }
        };
        dragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    //交给DragHelper处理
    public void computeScroll() {
        if (dragHelper.continueSettling(true))
            ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    //交给DragHelper处理
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    //交给DragHelper处理
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }
}
