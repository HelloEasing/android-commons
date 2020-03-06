package com.easing.commons.android.ui.control.button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.easing.commons.android.manager.FontUtil;
import com.easing.commons.android.manager.FontUtil.Font;
import com.easing.commons.android.R;

@SuppressLint("AppCompatCustomView")
public class ButtonM1 extends Button
{
  public ButtonM1(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    customStyle();
  }

  public ButtonM1(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    customStyle();
  }

  public ButtonM1(Context context)
  {
    super(context);
    customStyle();
  }

  private void customStyle()
  {
    super.setBackgroundResource(R.drawable.button_m02);
    super.setAllCaps(false);
    super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
    FontUtil.bindFont(this, Font.YAHEI_CONSOLAS);
  }
}
