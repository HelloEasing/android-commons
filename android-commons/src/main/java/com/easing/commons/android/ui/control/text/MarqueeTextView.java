package com.easing.commons.android.ui.control.text;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

public class MarqueeTextView extends AppCompatTextView {
  
  public MarqueeTextView(Context context) {
    super(context);
    init(context, null, 0);
  }
  
  public MarqueeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, null, 0);
  }
  
  public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }
  
  private void init(Context context, AttributeSet attrSet, int style) {
    super.setSingleLine(true);
    super.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    super.setMarqueeRepeatLimit(-1);
  }
  
  @Override
  public boolean isFocused() {
    return true;
  }
}
