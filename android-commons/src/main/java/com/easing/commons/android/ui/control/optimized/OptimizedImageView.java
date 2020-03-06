package com.easing.commons.android.ui.control.optimized;

import android.content.Context;
import android.util.AttributeSet;

import com.easing.commons.android.time.Times;
import com.easing.commons.android.ui.control.image.RippleImageView;

//优化响应时间，连续点击无效
//自带波纹效果，不用自己设置多种图片状态
public class OptimizedImageView extends RippleImageView {

    OnClickListener listener;
    long lastClickTime = 0;

    public OptimizedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    protected void init(Context context, AttributeSet attributeSet) {
        super.init(context, attributeSet);
        super.setOnClickListener(v -> {
            if (listener != null && Times.millisOfNow() - lastClickTime > 400) {
                lastClickTime = Times.millisOfNow();
                listener.onClick(v);
            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }


}
