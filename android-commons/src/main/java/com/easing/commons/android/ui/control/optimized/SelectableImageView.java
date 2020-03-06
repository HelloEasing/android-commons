package com.easing.commons.android.ui.control.optimized;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class SelectableImageView extends AppCompatImageView {

    public SelectableImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    protected void init(Context context, AttributeSet attributeSet) {
        setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
        });
    }

    public void friendController(View controller) {
        controller.setOnClickListener(v -> {
            SelectableImageView.this.performClick();
        });
    }

}
