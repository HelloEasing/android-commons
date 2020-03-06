package com.easing.commons.android.ui.control.compound;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.view.Views;

//将这个布局放在FrameLayout中，和其它布局一起叠加显示
public class ProgressLayout extends LinearLayout {
  
  private Context ctx;
  private TextView text;
  
  public ProgressLayout(Context context) {
    super(context);
    init(context, null);
  }
  
  public ProgressLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }
  
  public ProgressLayout(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);
    init(context, attrs);
  }
  
  private void init(Context context, AttributeSet attrSet) {
    this.ctx = context;
    //获取属性值
    TypedArray attrs = getContext().obtainStyledAttributes(attrSet, R.styleable.ProgressLayout);
    CharSequence message = attrs.getText(R.styleable.ProgressLayout_message);
    //添加子节点
    View root = Views.inflate(ctx, R.layout.layout_progress_layout_m01);
    super.addView(root, new LinearLayout.LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT, 1));
    //设置图片动画
    ImageView iv = root.findViewById(R.id.iv);
    AnimationDrawable animation = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_m01);
    iv.setImageDrawable(animation);
    animation.start();
    //设置提示信息
    this.text = root.findViewById(R.id.tv);
    if (message != null)
      text.setText(message);
    //居中
    super.setGravity(Gravity.CENTER);
    //默认隐藏
    super.setVisibility(View.GONE);
  }
  
  public ProgressLayout message(String msg) {
    text.setText(msg);
    return this;
  }
  
  public ProgressLayout show() {
    super.setVisibility(View.VISIBLE);
    return this;
  }
  
  public ProgressLayout hide() {
    super.setVisibility(View.GONE);
    return this;
  }
}
