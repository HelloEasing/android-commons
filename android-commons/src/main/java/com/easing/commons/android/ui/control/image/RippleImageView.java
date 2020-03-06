package com.easing.commons.android.ui.control.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.value.color.Colors;

@SuppressWarnings("all")
public class RippleImageView extends ImageView {

    public RippleImageView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        init(context, attrSet);
    }

    protected void init(Context context, AttributeSet attrSet) {
        GlobalHandler.postLater(() -> {
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Colors.BLACK_20), getDrawable(), null);
            super.setImageDrawable(rippleDrawable);
        }, 1000);
    }

    public void setImageDrawable(Drawable drawable) {
        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Colors.BLACK_20), drawable, null);
        super.setImageDrawable(rippleDrawable);
    }

}
