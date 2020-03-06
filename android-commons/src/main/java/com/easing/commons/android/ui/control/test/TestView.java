package com.easing.commons.android.ui.control.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.value.measure.Size;
import com.easing.commons.android.manager.FontUtil;
import com.easing.commons.android.manager.FontUtil.Font;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.color.Colors;

import java.util.Random;

@SuppressLint("DrawAllocation")
public class TestView extends LinearLayout {

    private Object value;
    private Paint paint;

    private int textWidth;
    private int textHeight;

    public TestView(Context context, Object value) {
        super(context);
        if (value == null)
            value = "";
        this.value = value;
        init(context, null);
    }

    public TestView(Context context) {
        super(context);
        init(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TestView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrSet) {
        //尺寸铺满容器
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT);
        super.setLayoutParams(params);

        //随机取背景色
        int color = new Random().nextInt(0xFFFFFF - 0x000000) + 0xBB000000;
        super.setBackgroundColor(color);

        //背景色字体色互补
        paint = new Paint();
        int reverseColor = Colors.reverseColor(color, 0xFF);
        paint.setColor(reverseColor);
        paint.setTextSize(100f);
        paint.setTypeface(FontUtil.getTypeface(this, Font.LIBIAN));
        paint.setAntiAlias(true);

        //设置画笔阴影
        MaskFilter blur = new BlurMaskFilter(0.5f, Blur.SOLID);
        paint.setMaskFilter(blur);
        paint.setShadowLayer(1f, 0.5f, 0.5f, reverseColor);


        //测量文字大小
        Rect textBounds = new Rect();
        paint.getTextBounds(value.toString(), 0, value.toString().length(), textBounds);
        textWidth = textBounds.width();
        textHeight = textBounds.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Console.info("on draw", textWidth, textHeight, value.toString());
        Size size = Views.size(this);
        canvas.drawText(value.toString(), (size.w - textWidth) / 2, (size.h - textHeight) / 2, paint);
    }
}
