package com.easing.commons.android.ui.control.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.color.Colors;

import lombok.Getter;
import lombok.Setter;

// 默认样式：
// 字体颜色：淡灰-淡蓝
// 字体大小：14dp
// 标题和图标间距：2dp
// Item上下内间距：5dp
// 使用方法：放在TabLayout中，设置属性即可
public class TabItem extends LinearLayout {

    private int icon;
    private String title;
    private float fontSize;

    @Getter
    @Setter
    private int normalColor;
    @Getter
    @Setter
    private int activeColor;

    private ImageView iv;
    private TextView tv;

    public TabItem(Context context) {
        super(context);
        init(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TabItem(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        //解析属性
        TypedArray attrs = context.obtainStyledAttributes(attrSet, R.styleable.TabItem);
        icon = attrs.getResourceId(R.styleable.TabItem_image, R.drawable.color_transparent);
        CharSequence charSequence = attrs.getText(R.styleable.TabItem_text);
        title = (charSequence == null) ? "" : charSequence.toString();
        fontSize = attrs.getDimension(R.styleable.TabItem_fontSize, Dimens.toPx(context, 14));
        normalColor = attrs.getColor(R.styleable.TabItem_normalColor, Colors.LIGHT_GREY);
        activeColor = attrs.getColor(R.styleable.TabItem_activeColor, Colors.LIGHT_BLUE);

        //设置Item布局和内边距
        super.setOrientation(LinearLayout.VERTICAL);
        super.setLayoutParams(new LayoutParams(0, Views.MATCH_PARENT, 1));
        super.setPadding(0, Dimens.toPx(context, 5), 0, Dimens.toPx(context, 5));
        //添加图片
        iv = new ImageView(context);
        iv.setImageResource(icon);
        iv.setColorFilter(normalColor);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        super.addView(iv, new LayoutParams(Views.MATCH_PARENT, 0, 1));
        //添加文字
        tv = new TextView(context);
        tv.setText(title);
        tv.setTextColor(normalColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        tv.setGravity(Gravity.CENTER);
        super.addView(tv, new LayoutParams(Views.MATCH_PARENT, Views.WRAP_CONTENT, 0));
        //设置图片文字间距
        LayoutParams tvLayoutParams = (LayoutParams) tv.getLayoutParams();
        tvLayoutParams.setMargins(0, Dimens.toPx(context, 2), 0, 0);
        tv.setLayoutParams(tvLayoutParams);
        //设置默认为非选中状态
        super.setSelected(false);
        //设置选中监听器
        super.setOnClickListener(v -> {
            TabLayout parent = (TabLayout) super.getParent();
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                TabItem child = (TabItem) parent.getChildAt(i);
                if (child != v) {
                    child.setSelected(false);
                    child.iv.setColorFilter(child.getNormalColor());
                    child.tv.setTextColor(child.getNormalColor());
                }
            }
            this.setSelected(true);
            this.iv.setColorFilter(activeColor);
            this.tv.setTextColor(activeColor);
            int pos = parent.indexOfChild(v);
            if (parent.onSelected != null)
                parent.onSelected.onSelect(this, pos);
        });
    }
}
