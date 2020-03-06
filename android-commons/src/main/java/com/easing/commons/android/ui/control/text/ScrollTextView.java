package com.easing.commons.android.ui.control.text;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

public class ScrollTextView extends AppCompatTextView {
  
  public ScrollTextView(Context context) {
    super(context);
    init(context, null, 0);
  }
  
  public ScrollTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, null, 0);
  }
  
  public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }
  
  private void init(Context context, AttributeSet attrSet, int style) {
    super.setSingleLine(true);
    super.setMovementMethod(ScrollingMovementMethod.getInstance());
  }
}
