package com.easing.commons.android.ui.control.button;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.easing.commons.android.manager.FontUtil;
import com.easing.commons.android.manager.FontUtil.Font;

@SuppressLint("AppCompatCustomView")
public class ButtonM2 extends AppCompatButton
{
  public ButtonM2(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    customStyle();
  }

  public ButtonM2(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    customStyle();
  }

  public ButtonM2(Context context)
  {
    super(context);
    customStyle();
  }

  private void customStyle()
  {
    super.setAllCaps(false);
    super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    FontUtil.bindFont(this, Font.LIBIAN);
  }
}
