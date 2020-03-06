package com.easing.commons.android.ui.control.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;

//图标居左，文字居中，不可调整格式
public class ImageButtonM2 extends LinearLayout {

    ViewGroup root;
    ImageView iv;
    TextView tv;

    public ImageButtonM2(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        customStyle(context, attrs, style);
    }

    public ImageButtonM2(Context context, AttributeSet attrs) {
        super(context, attrs);
        customStyle(context, attrs, 0);
    }

    public ImageButtonM2(Context context) {
        super(context);
        customStyle(context, null, 0);
    }

    private void customStyle(Context context, AttributeSet attrs, int style) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonM2);
        String text = array.getText(R.styleable.ImageButtonM2_text).toString();
        int textColor = array.getColor(R.styleable.ImageButtonM2_textColor, getResources().getColor(R.color.color_black_70));
        Drawable imageDrawable = array.getDrawable(R.styleable.ImageButtonM2_image);
        Drawable backgroundDrawable = array.getDrawable(R.styleable.ImageButtonM2_background);

        root = Views.inflate(context, R.layout.layout_image_button_m02);

        if (backgroundDrawable == null)
            backgroundDrawable = getResources().getDrawable(R.drawable.button_m06);
        setBackground(backgroundDrawable);

        iv = root.findViewById(R.id.iv);
        if (imageDrawable != null)
            iv.setImageDrawable(imageDrawable);

        tv = root.findViewById(R.id.tv);
        tv.setText(text);
        tv.setTextColor(textColor);

        super.setClickable(true);
        super.addView(root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void showIcon(boolean show) {
        iv.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
