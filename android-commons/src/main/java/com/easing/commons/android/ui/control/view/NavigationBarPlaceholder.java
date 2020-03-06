package com.easing.commons.android.ui.control.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.app.CommonActivity;

public class NavigationBarPlaceholder extends View {

    CommonActivity ctx;

    public NavigationBarPlaceholder(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        init(context, attrSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    private void init(Context context, AttributeSet attrSet) {
        this.ctx = (CommonActivity) context;
        //View.post会等布局加载完毕再执行，这时可以正确取得各控件的大小
        //通过各控件的大小，可以判断出手机是否是全面屏
        post(() -> {
            ctx.detectNavigationBar();
        });
    }

}
