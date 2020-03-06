package com.easing.commons.android.ui.control.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.color.Colors;

// 默认样式：
// 字体颜色：淡灰-淡蓝
// 字体大小：14dp
// 标题和图标间距：2dp
// Item上下内间距：5dp
// 使用方法：放在TabLayout中，设置属性即可
public class TabItemM2 extends LinearLayout {

    private String title;
    private float fontSize;
    private int normalColor;
    private int selectedColor;

    private TextView tv;
    private View slider;

    public TabItemM2(Context context) {
        super(context);
        init(context, null);
    }

    public TabItemM2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TabItemM2(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        //解析属性
        TypedArray attrs = context.obtainStyledAttributes(attrSet, R.styleable.TabItemM2);
        CharSequence charSequence = attrs.getText(R.styleable.TabItemM2_text);
        title = (charSequence == null) ? "" : charSequence.toString();
        fontSize = attrs.getDimension(R.styleable.TabItemM2_fontSize, Dimens.toPx(context, 18));
        normalColor = attrs.getColor(R.styleable.TabItemM2_normalColor, getResources().getColor(R.color.color_black_70));
        selectedColor = attrs.getColor(R.styleable.TabItemM2_activeColor, Colors.LIGHT_BLUE);

        //设置Item布局和内边距
        super.setOrientation(LinearLayout.VERTICAL);
        super.setLayoutParams(new LayoutParams(0, Views.WRAP_CONTENT, 1));
        //添加文字
        tv = new TextView(context);
        tv.setText(title);
        tv.setTextColor(normalColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        tv.setGravity(Gravity.CENTER);
        LayoutParams lp1 = new LayoutParams(Views.MATCH_PARENT, Views.WRAP_CONTENT, 0);
        lp1.topMargin = Dimens.toPx(context, 8);
        super.addView(tv, lp1);
        //添加滑块
        slider = new View(context);
        slider.setBackgroundColor(Colors.TRANSPARENT);
        LayoutParams lp2 = new LayoutParams(Views.MATCH_PARENT, Dimens.toPx(context, 3), 0);
        lp2.topMargin = Dimens.toPx(context, 5);
        super.addView(slider, lp2);
        //设置默认为非选中状态
        super.setSelected(false);
        //设置选中监听器
        super.setOnClickListener(v -> {
            TabLayout parent = (TabLayout) super.getParent();
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                TabItemM2 child = (TabItemM2) parent.getChildAt(i);
                if (child != v) {
                    child.setSelected(false);
                    child.slider.setBackgroundColor(Colors.TRANSPARENT);
                    child.tv.setTextColor(normalColor);
                }
            }
            parent.selectedIndex = parent.indexOfChild(this);
            parent.selectedItem = this;
            this.setSelected(true);
            this.slider.setBackgroundColor(selectedColor);
            this.tv.setTextColor(selectedColor);
            int pos = parent.indexOfChild(v);
            if (parent.onSelected != null)
                parent.onSelected.onSelect(this, pos);
        });
    }


}
