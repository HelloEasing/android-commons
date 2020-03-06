package com.easing.commons.android.ui.control.switcher;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.R;

import lombok.Getter;
import lombok.Setter;

public class SwitcherM1 extends View
{
  @Getter
  private State state = State.ON;

  @Getter
  @Setter
  private boolean switching = false;

  public static enum State
  {
    ON, OFF
  }

  public SwitcherM1(Context context)
  {
    super(context);
    init(context, null);
  }

  public SwitcherM1(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context, attrs);
  }

  public SwitcherM1(Context context, AttributeSet attrs, int style)
  {
    super(context, attrs, style);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs)
  {
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    if (this.state == State.ON)
      super.setBackgroundResource(R.drawable.switcher_m01_on);
    else
      super.setBackgroundResource(R.drawable.switcher_m01_off);
    super.onDraw(canvas);
  }

  synchronized public void setState(State state)
  {
    this.state = state;
    super.postInvalidate();
  }

  synchronized public void changeState()
  {
    if (this.state == State.ON)
      this.state = State.OFF;
    else
      this.state = State.ON;
    super.postInvalidate();
  }
}
