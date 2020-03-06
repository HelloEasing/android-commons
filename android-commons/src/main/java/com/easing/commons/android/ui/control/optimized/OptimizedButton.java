package com.easing.commons.android.ui.control.optimized;

import android.content.Context;
import android.util.AttributeSet;

import com.easing.commons.android.time.Times;

import androidx.appcompat.widget.AppCompatButton;

//优化响应时间，连续点击无效
public class OptimizedButton extends AppCompatButton {

    OnClickListener listener;
    long lastClickTime = 0;

    public OptimizedButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
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
