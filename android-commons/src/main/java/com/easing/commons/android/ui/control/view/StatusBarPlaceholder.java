package com.easing.commons.android.ui.control.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.view.Views;

public class StatusBarPlaceholder extends View {

    boolean navigationBarDetected = false;

    public StatusBarPlaceholder(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
        CommonActivity ctx = (CommonActivity) context;
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int rootHeight = Views.getRootView(ctx).getMeasuredHeight();
            if (rootHeight > 0 && !navigationBarDetected) {
                navigationBarDetected = true;
                ((CommonActivity) context).detectNavigationBar();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

}
