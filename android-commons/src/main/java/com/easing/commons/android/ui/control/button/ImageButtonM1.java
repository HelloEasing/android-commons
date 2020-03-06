package com.easing.commons.android.ui.control.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.view.Views;

//可以自由定制文字图标位置
public class ImageButtonM1 extends LinearLayout {
  public ImageButtonM1(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);
    customStyle(context, attrs, style);
  }
  
  public ImageButtonM1(Context context, AttributeSet attrs) {
    super(context, attrs);
    customStyle(context, attrs, 0);
  }
  
  public ImageButtonM1(Context context) {
    super(context);
    customStyle(context, null, 0);
  }
  
  private void customStyle(Context context, AttributeSet attrs, int style) {
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonM1);
    Drawable drawable = array.getDrawable(R.styleable.ImageButtonM1_image);
    Drawable background = array.getDrawable(R.styleable.ImageButtonM1_background);
    String text = array.getText(R.styleable.ImageButtonM1_text).toString();
    float img_size = array.getFloat(R.styleable.ImageButtonM1_imageSize, 1);
    float text_size = array.getFraction(R.styleable.ImageButtonM1_fontSizeScale, 1, 1, 1);
    int text_width = array.getInt(R.styleable.ImageButtonM1_textWidth, 0);
    int text_color = array.getColor(R.styleable.ImageButtonM1_textColor, getResources().getColor(R.color.color_white));
    int left_padding = array.getInt(R.styleable.ImageButtonM1_leftPadding, 0);
    int mid_padding = array.getInt(R.styleable.ImageButtonM1_middlePadding, 0);
    int right_padding = array.getInt(R.styleable.ImageButtonM1_rightPadding, 0);
    
    LinearLayout root = Views.inflate(context, R.layout.layout_image_button_m01);
    root.setGravity(Gravity.CENTER);
    
    if (background == null)
      background = getResources().getDrawable(R.drawable.button_m06);
    setBackground(background);
    
    ImageView iv = (ImageView) root.findViewById(R.id.iv);
    if (drawable != null)
      iv.setImageDrawable(drawable);
    iv.setLayoutParams(new LinearLayout.LayoutParams((int) (64 * img_size), (int) (64 * img_size)));
    
    TextView tv = (TextView) root.findViewById(R.id.tv);
    tv.setText(text);
    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18 * text_size);
    tv.setTextColor(text_color);
    if (text_width != 0)
      tv.setLayoutParams(new LayoutParams(text_width, LayoutParams.MATCH_PARENT, 0));
    
    View left = Views.findView(root, R.id.left);
    View mid = Views.findView(root, R.id.mid);
    View right = Views.findView(root, R.id.right);
    if (left_padding != 0)
      Views.size(left, left_padding, 0);
    if (mid_padding != 0)
      Views.size(mid, mid_padding, 0);
    if (right_padding != 0)
      Views.size(right, right_padding, 0);
    
    super.setClickable(true);
    super.addView(root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
  }
}
